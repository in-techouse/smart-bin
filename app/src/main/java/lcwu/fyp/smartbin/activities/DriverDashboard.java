package lcwu.fyp.smartbin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Constants;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.Booking;
import lcwu.fyp.smartbin.model.Notification;
import lcwu.fyp.smartbin.model.User;

public class DriverDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Session session;
    private Helpers helpers;
    private User user;
    private CircleImageView Profile_image;
    private TextView Profile_name;
    private TextView Profile_email;
    private DrawerLayout drawer;
    private MapView mapView;
    private GoogleMap googleMap;
    private NavigationView navigationView;
    private TextView locationAddress;
    private LinearLayout searching;
    private Button confirm;
    private ProgressBar sheetProgress;
    private RelativeLayout mainSheet;
    private BottomSheetBehavior sheetBehavior;
    private Booking activeBooking;
    private User activeCustomer;
    private CircleImageView customerImage;
    private TextView customerName, trashWeight,customerContact , customerEmail, bookingDate, bookingAddress;
    private ValueEventListener bookingsValueEventListener, userValueEventListener, bookingValueEventListener;
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");



    private FusedLocationProviderClient locationProviderClient;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(DriverDashboard.this);
        helpers = new Helpers();
        user = session.getUser();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        locationAddress = findViewById(R.id.locationAddress);
        searching = findViewById(R.id.searching);
        confirm = findViewById(R.id.confirm);

        LinearLayout layoutBottomSheet = findViewById(R.id.driver_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetProgress = findViewById(R.id.driver_sheetProgress);
        mainSheet = findViewById(R.id.driver_mainSheet);

        customerName = findViewById(R.id.driverName);
        customerImage = findViewById(R.id.driverImage);
        bookingAddress = findViewById(R.id.driver_bookingAddress);
        bookingDate = findViewById(R.id.driver_bookingDate);
        trashWeight = findViewById(R.id.bookingFare);
        customerEmail = findViewById(R.id.customerEmail);

        Button completeBooking = findViewById(R.id.completeBooking);
        Button cancelBooking = findViewById(R.id.cancelBooking);

        completeBooking.setOnClickListener(this);
        cancelBooking.setOnClickListener(this);







        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View hadder = navigationView.getHeaderView(0);
        Profile_image = hadder.findViewById(R.id.profile_image);
        Profile_name = hadder.findViewById(R.id.profile_name);
        Profile_email = hadder.findViewById(R.id.profile_email);

        Profile_name.setText(user.getFirstName() +" " + user.getLastName());
        Profile_email.setText(user.getEmail());

        locationProviderClient = LocationServices.getFusedLocationProviderClient(DriverDashboard.this);

        mapView =findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(DriverDashboard.this);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps","Call back received");
                    googleMap =gM;
                    LatLng defaultPosition= new LatLng(31.5204,74.3487);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    rlp.setMargins(0, 350, 100, 0);

                    enableLocation();
                }
            });
        }
        catch (Exception e){
            helpers.showError(DriverDashboard.this, "Error", "Something Went Wrong try again");
        }
    }

    private boolean askForPermission() {
        if (ActivityCompat.checkSelfPermission(DriverDashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DriverDashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverDashboard.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return false;
        }
        return true;
    }

    public void enableLocation() {
        Log.e("location" , "in Enable Location");
        if (askForPermission()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(DriverDashboard.this);
                    current.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        public void onSuccess(Location location) {
                            getDeviceLocation();
                        }
                    });
                    return true;
                }
            });
            getDeviceLocation();
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            listenToBookings();
//            getOnProviders();
        }
    }


    private void getDeviceLocation() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                Log.e("location" , "in getDevice location catch");
                helpers.showError(DriverDashboard.this,  Constants.ERROR_SOMETHING_WENT_WRONG , ex.toString());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("location" , "in network Provider try");
                helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG , ex.toString());

            }
            if (!gps_enabled && !network_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverDashboard.this);
                dialog.setMessage("Oppsss.Your Location Service is off.\n Please turn on your Location and Try again Later");
                dialog.setPositiveButton("Let me On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
                return;
            }

            locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            googleMap.clear();
                            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                            marker = googleMap.addMarker(new MarkerOptions().position(me).title("You're Here")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                            Geocoder geocoder = new Geocoder(DriverDashboard.this);
                            List<Address> addresses = null;
                            Log.e("location" , "goint to try");
                            try {
                                Log.e("location" , "lat is "+me.latitude+" and long "+me.longitude);
                                addresses = geocoder.getFromLocation(me.latitude, me.longitude, 1);
                                Log.e("location" , "address is "+addresses.toString());
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String strAddress = "";
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        strAddress = strAddress + " " + address.getAddressLine(i);
                                    }
                                    locationAddress.setText(strAddress);
                                    updateUserLocation(me.latitude, me.longitude);
                                }
                            } catch (Exception exception) {
                                Log.e("location" , "in getlastlocation catch");
                                helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG , exception.toString());
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("location" , "in get last location on failure");
                    helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG , e.toString());
                }
            });
        } catch (Exception e) {
            Log.e("location" , "in outer catch of unknown");
            helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG , e.toString());
        }
        listenToNotifications();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            }
        }
    }

    private void updateUserLocation(double lat, double lng) {
//        user.setLatidue(lat);
//        user.setLongitude(lng);
//        session.setSession(user);
//        reference.child(user.getPhone()).setValue(user);
    }

    private void listenToNotifications() {
//        notificationRefrence.orderByChild("userId").equalTo(user.getPhone()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        Notification n = data.getValue(Notification.class);
//                        if (n != null && !n.isRead()) {
//                            showNotificationsDialog(n);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void showNotificationsDialog(final Notification notification) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DriverDashboard.this, "1");
        builder.setTicker("New Notification");
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo("New Notification ");
        builder.setContentTitle("New Notification ");
//        builder.setContentText(notification.getMessage());
        builder.setContentText("Notification Content");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
//        Intent notificationIntent = new Intent(Dashboard.this,BookingDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("Notification", notification);
//        notificationIntent.putExtras(bundle);
//        PendingIntent conPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(conPendingIntent);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (manager != null) {
//            manager.notify(10, builder.build());
//        }
//        final MaterialDialog dialog = new MaterialDialog.Builder(Dashboard.this)
//                .setTitle("NEW Notification")
//                .setMessage(notification.getMessage())
//                .setCancelable(false)
//                .setPositiveButton("DETAILS", R.drawable.ic_okay, new MaterialDialog.OnClickListener() {
//                    @Override
//                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
//                        dialogInterface.dismiss();
//                        Intent it = new Intent(Dashboard.this, BookingDetailActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("Notification",notification);
//                        it.putExtras(bundle);
//                        startActivity(it);
//                    }
//                })
//                .setNegativeButton("CLOSE", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
//                    @Override
//                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .build();
        // Show Dialog
//        dialog.show();

    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home: {
                break;
            }
            case R.id.nav_profile: {
                Intent in  = new Intent(DriverDashboard.this , UserProfileActivity.class);
                startActivity(in);
                break;
            }

            case R.id.nav_notification: {
                Intent it = new Intent(DriverDashboard.this, NotificationActivity.class);
                startActivity(it);

                break;
            }
            case R.id.nav_onlinebooking: {
                Intent it = new Intent(DriverDashboard.this,BookingActivity.class);
                startActivity(it);
                break;

            }
            case R.id.nav_logout:{
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                session.destroySession();
                Intent it= new Intent(DriverDashboard.this,LoginActivity.class);
                startActivity(it);
                finish();
                break;
            }
        }


        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void listenToBookings() {
        Log.e("VendorDashboard" , "in func");
        bookingsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Log.e("VendorDashboard", "Bookings Listener DataSnapshot: " + data.toString());
                    final Booking booking = data.getValue(Booking.class);
                    if(booking != null && booking.getDriverId() != null ){
                        Log.e("VendorDashboard", "Booking Status: " + booking.getStatus());
                        if(booking.getStatus().equals("In Progress")){
                            bookingReference.removeEventListener(bookingsValueEventListener);
                            Log.e("VendorDashboard" , "Booking status in Progress found");
                            activeBooking = booking;
                            sheetBehavior.setHideable(false);
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            loadCustomerDetails();
                        }
                        else if (booking.getDriverId().length() < 1 && booking.getStatus().equals("New")) {
                            showBookingDialog(booking);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard" , "in onCancelled "+databaseError.toString());
            }
        };
        bookingReference.addValueEventListener(bookingsValueEventListener);
    }

    private void loadCustomerDetails(){
        Log.e("VendorDashboard", "Call Received to Load Customer");
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userReference.removeEventListener(userValueEventListener);
                if(activeCustomer == null) {
                    Log.e("VendorDashboard", "User Value Received, on Data Changed: " + dataSnapshot.toString());
                    activeCustomer = dataSnapshot.getValue(User.class);
                    if (activeCustomer != null) {
                        if (activeCustomer.getImage() != null && activeCustomer.getImage().length() > 1) {
                            Log.e("VendorDashboard", "Active Customer Image Found");
//                            Glide.with(getApplicationContext()).load(activeCustomer.getImage()).into(customerImage);
                        }
                        Log.e("VendorDashboard", "Active Customer Detail set, Id: " + activeCustomer.getId());
                        Log.e("VendorDashboard", "Active Customer Detail set, Name: " + activeCustomer.getFirstName() + " Contact: " + activeCustomer.getPhoneNumber());
                        Log.e("VendorDashboard", "Active Customer Detail set, Image: " + activeCustomer.getImage() + " Email: " + activeCustomer.getEmail());
                        customerName.setText(activeCustomer.getFirstName()+" "+activeCustomer.getLastName());
//                        customerContact.setText(activeCustomer.getPhoneNumber()+"");
                        customerEmail.setText(activeCustomer.getEmail());
                        bookingDate.setText(activeBooking.getStartTime());
                        bookingAddress.setText(activeBooking.getPickup());
                        trashWeight.setText(activeBooking.getTrashWeightig()+"");
                    }
                }
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userReference.removeEventListener(userValueEventListener);
                Log.e("VendorDashboard", "User Value Received, on Cancelled: " + databaseError.getMessage());
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };
        userReference.child(activeBooking.getUserId()).addValueEventListener(userValueEventListener);
        listenToActiveBookingChange();
    }

    private void showBookingDialog(final Booking booking){
        Log.e("show Booking" , "in funtion start");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DriverDashboard.this, "1");
        builder.setTicker("New Booking");
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo("New Booking Found.");
        builder.setContentTitle("New Booking Found.");
        builder.setContentText("We have a new booking for you. It's time to get some revenue.");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
        //make another activity to show bookings details
        Log.e("show Booking" , "Going to start another activity");
        Intent notificationIntent = new Intent(DriverDashboard.this, ShowBookingDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Booking", booking);
        notificationIntent.putExtras(bundle);
        PendingIntent conPendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(conPendingIntent);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(10,builder.build());
        }

        final MaterialDialog dialog = new MaterialDialog.Builder(DriverDashboard.this)
                .setTitle("NEW BOOKING")
                .setMessage("A NEW BOOKING HAS ARRIVED, DO YOU WANT TO EARN SOME MORE PROFIT?")
                .setCancelable(false)
                .setPositiveButton("DETAILS", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        Log.e("booking" , "going to start in positive click");
                        Intent it = new Intent(DriverDashboard.this, ShowBookingDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Booking",booking);
                        it.putExtras(bundle);
                        startActivity(it);
                    }
                })
                .setNegativeButton("REJECT", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();
        // Show Dialog
        dialog.show();
    }

    private void listenToActiveBookingChange(){
        bookingValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("VendorDashboard", "Booking Listener: " + dataSnapshot.toString());
                Booking booking = dataSnapshot.getValue(Booking.class);
                if(booking != null && activeBooking != null){
                    switch (booking.getStatus()){
                        case "Cancelled":{
                            Log.e("VendorDashboard", "Booking Cancelled");
                            activeBooking = booking;
                            showCancelledNotification();
                            break;
                        }
                        case "Completed": {
                            Log.e("VendorDashboard", "Booking Completed");
                            activeBooking = booking;
                            showCompletedNotification();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        bookingReference.child(activeBooking.getId()).addValueEventListener(bookingValueEventListener);
    }



    private void showCompletedNotification(){
        helpers.showError(DriverDashboard.this, "Booking Completed", "Your booking has been Completed.");
        helpers.showError(DriverDashboard.this, "Booking Completed", "Your booking with " + activeCustomer.getFirstName() +" has been Completed.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
    }


    private void showCancelledNotification(){
        bookingReference.removeEventListener(bookingValueEventListener);
        helpers.showError(DriverDashboard.this, "Booking Cancelled", "Your booking has been cancelled.");
        helpers.showError(DriverDashboard.this, "Booking Cancelled", "Your booking with " + activeCustomer.getFirstName() +" has been cancelled.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
        listenToBookings();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.completeBooking:{
                onBookingCompleted();
                break;
            }
            case R.id.cancelBooking:{
                onBookingCancelled();
                break;
            }
        }
    }

    private void onBookingCompleted(){
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Completed");
        final Notification notification = new Notification();
        final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String id = notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(activeBooking.getId());
        notification.setUserId(activeCustomer.getPhoneNumber());
        notification.setDriverId(user.getPhoneNumber());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setDriverText("You completed the booking of " + activeCustomer.getFirstName());
        notification.setUserText("Your booking has been completed by " + user.getFirstName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        reference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notificationReference.child(notification.getId()).setValue(notification);
                sheetBehavior.setHideable(true);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                helpers.showError(DriverDashboard.this, "ERROR", "Something went wrong.");
            }
        });
    }

    private void onBookingCancelled(){
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Cancelled");
        final Notification notification = new Notification();
        final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String id = notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(activeBooking.getId());
        notification.setUserId(activeCustomer.getPhoneNumber());
        notification.setDriverId(user.getPhoneNumber());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setDriverText("You cancelled the booking of " + activeCustomer.getFirstName());
        notification.setUserText("Your booking has been cancelled by " + user.getFirstName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        reference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notificationReference.child(notification.getId()).setValue(notification);
                sheetBehavior.setHideable(true);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                helpers.showError(DriverDashboard.this, "ERROR", "Something went wrong.");
            }
        });
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
