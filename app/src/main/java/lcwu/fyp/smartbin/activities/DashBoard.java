package lcwu.fyp.smartbin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {

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
    private List <User> data;
    private EditText wasteWeight;
    private Editable wasteWeightValid;
    private Boolean wasteFlag = false;
    private CountDownTimer timer;
    private ProgressBar sheetProgress;
    private RelativeLayout mainSheet;
    private BottomSheetBehavior sheetBehavior;
    private double trashWeight;
    private Booking activeBooking;
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private Button cancelBooking;


    private FusedLocationProviderClient locationProviderClient;
    private Marker marker;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener bookingListener, driverListener,bookingsListener;
    private TextView driverName , driverAddress , driverDate , trashWeightDriver;
    private CircleImageView driverImage;
    private ValueEventListener driverValueListener;
    private List<User> users;
    private User activeDriver;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(DashBoard.this);
        helpers = new Helpers();
        user = session.getUser();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        locationAddress = findViewById(R.id.locationAddress);
        searching = findViewById(R.id.searching);
        confirm = findViewById(R.id.confirm);
        wasteWeight = findViewById(R.id.wasteSize);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        LinearLayout layoutBottomSheet = findViewById(R.id.customer_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetProgress = findViewById(R.id.customer_sheetProgress);
        mainSheet = findViewById(R.id.customer_mainSheet);

        driverName = findViewById(R.id.driver_name);
        driverAddress = findViewById(R.id.driver_bookingAddress);
        driverDate = findViewById(R.id.driver_bookingDate);
        trashWeightDriver = findViewById(R.id.trashWeight);
        driverImage = findViewById(R.id.driverImage);
        users = new ArrayList<>();

        cancelBooking = findViewById(R.id.cancelDriverBooking);
        cancelBooking.setOnClickListener(this);






        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View hadder = navigationView.getHeaderView(0);
        Profile_image = hadder.findViewById(R.id.profile_image);
        Profile_name = hadder.findViewById(R.id.profile_name);
        Profile_email = hadder.findViewById(R.id.profile_email);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(DashBoard.this);


        Profile_name.setText(user.getFirstName() +" " + user.getLastName());
        Profile_email.setText(user.getEmail());

        mapView =findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(DashBoard.this);
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
            helpers.showError(DashBoard.this, "Error", "Something Went Wrong try again");
        }
    }

    private boolean askForPermission() {
        if (ActivityCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoard.this, new String[]{
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
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(DashBoard.this);
                    current.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        public void onSuccess(Location location) {
                            getDeviceLocation();
                        }
                    });
                    return true;
                }
            });
            getDeviceLocation();
            getAllDrivers();
            listenToBookingChanges();
        }
    }

    private void getAllDrivers(){
        data = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    User driver = d.getValue(User.class);
                    if (driver!=null){
                        data.add(driver);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                helpers.showError(DashBoard.this,  Constants.ERROR_SOMETHING_WENT_WRONG , ex.toString());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("location" , "in network Provider try");
                helpers.showError(DashBoard.this, Constants.ERROR_SOMETHING_WENT_WRONG , ex.toString());

            }
            if (!gps_enabled && !network_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DashBoard.this);
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
                            Geocoder geocoder = new Geocoder(DashBoard.this);
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
                                helpers.showError(DashBoard.this, Constants.ERROR_SOMETHING_WENT_WRONG , exception.toString());
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("location" , "in get last location on failure");
                    helpers.showError(DashBoard.this, Constants.ERROR_SOMETHING_WENT_WRONG , e.toString());
                }
            });
        } catch (Exception e) {
            Log.e("location" , "in outer catch of unknown");
            helpers.showError(DashBoard.this, Constants.ERROR_SOMETHING_WENT_WRONG , e.toString());
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DashBoard.this, "1");
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
                Intent in = new Intent(DashBoard.this , UserProfileActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_notification: {
                Intent it = new Intent(DashBoard.this, DriverDashboard.class);
                startActivity(it);

                break;
            }
            case R.id.nav_onlinebooking: {
                Intent it = new Intent(DashBoard.this,BookingActivity.class);
                startActivity(it);
                break;

            }
            case R.id.nav_logout:{
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                session.destroySession();
                Intent it= new Intent(DashBoard.this,LoginActivity.class);
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
        removeAllDatabaseListeners();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.confirm : {

                if (!helpers.isConnected(DashBoard.this)) {
                    helpers.showError(DashBoard.this , "Connection Error" , "Check your Internet Connection");
                    return;
                }
                wasteFlag = wasteValidater();
                if(wasteFlag){
                    searching.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
//                    activeBooking = new Booking();
                    final Booking booking = new Booking();
                    String key = bookingReference.push().getKey();
                    booking.setId(key);
                    booking.setUserId(user.getId());
                    booking.setPayment(50);
                    booking.setPickup(locationAddress.getText().toString());
                    booking.setDriverId("");
                    booking.setStatus("New");
                    booking.setLat(marker.getPosition().latitude);
                    booking.setLng(marker.getPosition().longitude);
                    Date d = new Date();
                    String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
                    booking.setStartTime(date);
                    booking.setTrashWeightig(trashWeight);
                    bookingReference.child(booking.getId()).setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            timer = new CountDownTimer(30000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    Log.e("Dashboard", "OnTick");
                                }
                                @Override
                                public void onFinish() {
                                    Log.e("Dashboard", "onFinish");
                                    if(booking.getStatus().equals("New")){
                                        booking.setStatus("Rejected");
                                        bookingReference.child(booking.getId()).setValue(booking);
                                        searching.setVisibility(View.GONE);
                                        confirm.setVisibility(View.VISIBLE);
                                        helpers.showError(DashBoard.this, "Service Error" ,  "No Service Provider Available Please Try Again Later!");
                                    }else if (activeBooking.getStatus().equals("In Progress")){
                                        searching.setVisibility(View.GONE);
                                        confirm.setVisibility(View.VISIBLE);
                                        mainSheet.setVisibility(View.VISIBLE);
                                        showBottomSheet();
                                    }
                                }
                            };
                            timer.start();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.showError(DashBoard.this , "Error" , "Something Went Wrong!");
                        }
                    });

                }

                break;
            }
            case R.id.cancelDriverBooking : {
                Log.e("cancel" , "button clicked");
                activeBooking.setStatus("Cancelled");
                final Notification notification = new Notification();
                final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
                String nId = notificationReference.push().getKey();
                notification.setId(nId);
                notification.setBookingId(activeBooking.getId());
                notification.setUserId(user.getPhoneNumber());
                if(activeDriver!=null){
                    Log.e("active driver" , "values "+activeDriver.getFirstName());

                }else {                Log.e("active driver" , "active driver is null");
                }
                notification.setDriverId(activeDriver.getPhoneNumber());
                notification.setRead(false);
                Date d = new Date();
                String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
                notification.setDate(date);
                notification.setDriverText("Your booking has been cancelled by " + user.getFirstName()+" "+user.getLastName());
                notification.setUserText("You cancelled your booking with " + activeDriver.getFirstName()+" "+activeDriver.getLastName());
                bookingReference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notificationReference.child(notification.getId()).setValue(notification);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Booking" , "Booking cancellation failed");
                    }
                });

                break;
            }
        }
    }

    Boolean wasteValidater(){
        if(wasteWeight != null && wasteWeight.length() > 0){
            wasteWeightValid = wasteWeight.getText();
            trashWeight = Integer.parseInt(String.valueOf(wasteWeightValid));
            if (trashWeight >= 10){
                Log.e("waste" , "weight is ok");
                wasteFlag = true;

            }else{
                wasteWeight.setError("weight must be greater then 10");
                wasteFlag = false;
            }
        }else {
            wasteWeight.setError("Waste weight cannot be empty");
            wasteFlag = false;
        }
     return wasteFlag;
    }

    private void showBottomSheet(){
        sheetBehavior.setHideable(false);
        sheetBehavior.setSkipCollapsed(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onInProgress(){
        if (timer != null)
            timer.cancel();

        showBottomSheet();

        searching.setVisibility(View.GONE);
        confirm.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        sheetProgress.setVisibility(View.VISIBLE);

        bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Booking Listener");
                Booking booking = dataSnapshot.getValue(Booking.class);
                Log.e("Dashboard", "Active Booking Updated");
                if(booking != null && activeBooking != null){
                    Log.e("Dashboard", "Active Booking Status: " + booking.getStatus());
//                    Log.e("Dashboard", "Active Booking Fare: " + booking.get());
                    switch (booking.getStatus()){
                        case "Cancelled": {
                            onBookingCancelled();
                            break;
                        }
                        case "Completed": {
                            onBookingCompleted();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        bookingReference.child(activeBooking.getId()).addValueEventListener(bookingListener);

        driverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Driver Listener Captured");
                if(activeDriver == null){
                    userReference.removeEventListener(driverListener);
                    Log.e("active driver" , "driver value before ");
                    activeDriver = dataSnapshot.getValue(User.class);
                    Log.e("active driver" , "driver value "+activeDriver.getFirstName());

                    if(activeDriver != null) {
                        //Fil bottomsshhet here
                        driverName.setText(activeDriver.getFirstName()+" "+activeDriver.getLastName());
                        driverDate.setText(activeBooking.getStartTime());
                        driverAddress.setText(activeBooking.getPickup());
                        trashWeightDriver.setText(activeBooking.getTrashWeightig()+"");
                        if (activeDriver.getImage() != null && activeDriver.getImage().length() > 1) {
                            Glide.with(getApplicationContext()).load(activeDriver.getImage()).into(driverImage);
                        }
                        else {
                            driverImage.setImageResource(R.drawable.profile);
                        }
                    }
                }
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        userReference.child(activeBooking.getDriverId()).addValueEventListener(driverListener);
    }

    private void onBookingCancelled(){
        bookingReference.child(activeBooking.getId()).removeEventListener(bookingListener);
        removeListeners();
        helpers.showError(DashBoard.this, "Booking Info" ,  "Your booking has been cancelled.");
//        helpers.sendNotification(DashboardActivity.this, "BOOKING", "Your booking has been cancelled.");
    }

    private void onBookingCompleted(){
        bookingReference.child(activeBooking.getId()).removeEventListener(bookingListener);
        removeListeners();
        helpers.showError(DashBoard.this, "Booking Info" ,  "Your Booking has been marked as completed.");
    }

    private void removeListeners(){
        removeAllDatabaseListeners();
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        getOnProviders();
        listenToBookingChanges();
        activeBooking = null;
    }

    private void removeAllDatabaseListeners(){
        if(bookingListener != null){
            bookingReference.removeEventListener(bookingListener);
        }
        if(bookingsListener != null){
            bookingReference.removeEventListener(bookingsListener);
        }
        if(driverValueListener!= null){
            userReference.removeEventListener(driverValueListener);
        }
        if(driverListener != null){
            userReference.removeEventListener(driverListener);
        }
    }

    private void listenToBookingChanges(){
        bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Bookings Listener");
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Log.e("Dashboard", "DataSnapShot Each Child: " + d.toString());
                    Booking booking = d.getValue(Booking.class);
                    if(booking != null){
                        Log.e("Dashboard", "Booking Status: " + booking.getStatus());
                        if (booking.getStatus().equals("In Progress")) {
                            Log.e("Dashboard", "Booking Status In Progress Found");
                            activeBooking = booking;
                            bookingReference.removeEventListener(bookingsListener);
                            if(driverListener != null){
                                userReference.removeEventListener(driverListener);
                            }
                            Log.e("Dashboard", "Bookings Listener Removed");
                            googleMap.clear();
                            getDeviceLocation();
                            onInProgress();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        bookingReference.orderByChild("userId").equalTo(user.getId()).addValueEventListener(bookingsListener);
    }

    private void getOnProviders() {
        driverValueListener = userReference.orderByChild("type").equalTo(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User u = data.getValue(User.class);
                    if (u != null && activeBooking != null) {
                        LatLng user_location = new LatLng(u.getLatitude(), u.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(user_location).title("Drivers");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.profile));
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                        marker.setTag(u);
                        users.add(u);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helpers.showError(DashBoard.this,"Error", Constants.ERROR_SOMETHING_WENT_WRONG);
            }
        });

    }




}