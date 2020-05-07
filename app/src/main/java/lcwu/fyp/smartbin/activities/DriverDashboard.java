package lcwu.fyp.smartbin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

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
    private User user, activeCustomer;
    private DrawerLayout drawer;
    private MapView mapView;
    private GoogleMap googleMap;
    private TextView locationAddress;
    private ProgressBar sheetProgress;
    private RelativeLayout mainSheet;
    private BottomSheetBehavior sheetBehavior;
    private Booking activeBooking;
    private CircleImageView customerImage;
    private TextView customerName, trashWeight, customerContact, customerEmail, bookingDate, bookingAddress;
    private ValueEventListener bookingsValueListener, bookingValueListener, userValueListener;
    private DatabaseReference bookingsReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private Marker customerMarker;
    private EditText totalCharge, totalTrashWeight;
    private LinearLayout amountLayout;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        session = new Session(DriverDashboard.this);
        helpers = new Helpers();
        user = session.getUser();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        locationAddress = findViewById(R.id.locationAddress);
        LinearLayout searching = findViewById(R.id.searching);
        Button confirm = findViewById(R.id.confirm);

        LinearLayout layoutBottomSheet = findViewById(R.id.driver_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetProgress = findViewById(R.id.driver_sheetProgress);
        mainSheet = findViewById(R.id.driver_mainSheet);

        customerName = findViewById(R.id.driverName);
        trashWeight = findViewById(R.id.trashWeight);
        customerContact = findViewById(R.id.customerPhone);
        customerImage = findViewById(R.id.customerImage);
        bookingAddress = findViewById(R.id.driver_bookingAddress);
        bookingDate = findViewById(R.id.driver_bookingDate);
        customerEmail = findViewById(R.id.customerEmail);
        ImageView call = findViewById(R.id.call);
        call.setOnClickListener(this);

        Button completeBooking = findViewById(R.id.completeBooking);
        Button cancelBooking = findViewById(R.id.cancelBooking);

        totalCharge = findViewById(R.id.totalCharge);
        totalTrashWeight = findViewById(R.id.totalTrashWeight);
        amountLayout = findViewById(R.id.amountLayout);
        Button amountSubmit = findViewById(R.id.amountSubmit);

        completeBooking.setOnClickListener(this);
        cancelBooking.setOnClickListener(this);
        amountSubmit.setOnClickListener(this);


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View hadder = navigationView.getHeaderView(0);
        CircleImageView profile_image = hadder.findViewById(R.id.profile_image);
        TextView profile_name = hadder.findViewById(R.id.profile_name);
        TextView profile_email = hadder.findViewById(R.id.profile_email);

        profile_name.setText(user.getFirstName() + " " + user.getLastName());
        profile_email.setText(user.getEmail());

        if (user.getImage() != null && user.getImage().length() > 0) {
            Glide.with(getApplicationContext()).load(user.getImage()).into(profile_image);
        }


        locationProviderClient = LocationServices.getFusedLocationProviderClient(DriverDashboard.this);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(DriverDashboard.this);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps", "Call back received");
                    googleMap = gM;
                    LatLng defaultPosition = new LatLng(31.5204, 74.3487);
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
        } catch (Exception e) {
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
        Log.e("location", "in Enable Location");
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
            listenToBookings();
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
                Log.e("location", "in getDevice location catch");
                helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG, ex.toString());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("location", "in network Provider try");
                helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG, ex.toString());

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
                            googleMap.addMarker(new MarkerOptions().position(me).title("You're Here")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                            Geocoder geocoder = new Geocoder(DriverDashboard.this);
                            List<Address> addresses = null;
                            Log.e("location", "goint to try");
                            try {
                                Log.e("location", "lat is " + me.latitude + " and long " + me.longitude);
                                addresses = geocoder.getFromLocation(me.latitude, me.longitude, 1);
                                Log.e("location", "address is " + addresses.toString());
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String strAddress = "";
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        strAddress = strAddress + address.getAddressLine(i) + " ";
                                    }
                                    locationAddress.setText(strAddress);
                                    updateUserLocation(me.latitude, me.longitude);
                                }
                            } catch (Exception exception) {
                                Log.e("location", "in getlastlocation catch");
                                helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG, exception.toString());
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("location", "in get last location on failure");
                    helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG, e.toString());
                }
            });
        } catch (Exception e) {
            Log.e("location", "in outer catch of unknown");
            helpers.showError(DriverDashboard.this, Constants.ERROR_SOMETHING_WENT_WRONG, e.toString());
        }
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
        Log.e("DriverDashboard", "Update driver called with lat: " + lat + " lng: " + lng);
        user.setLatitude(lat);
        user.setLongitude(lng);
        session.setSession(user);
        userReference.child(user.getId()).setValue(user);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home: {
                break;
            }
            case R.id.nav_profile: {
                Intent in = new Intent(DriverDashboard.this, UserProfileActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_bookings: {
                Intent in = new Intent(DriverDashboard.this, BookingActivity.class);
                startActivity(in);
                break;
            }

            case R.id.nav_notifications: {
                Intent it = new Intent(DriverDashboard.this, NotificationActivity.class);
                startActivity(it);

                break;
            }

            case R.id.nav_logout: {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                session.destroySession();
                Intent it = new Intent(DriverDashboard.this, LoginActivity.class);
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
        if (bookingsValueListener != null) {
            bookingsReference.removeEventListener(bookingsValueListener);
        }
        if (bookingValueListener != null) {
            bookingsReference.removeEventListener(bookingValueListener);
        }

        if (userValueListener != null) {
            userReference.removeEventListener(userValueListener);
        }
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
        bookingsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ProviderDashboard", "Bookings value event Listener");
                if (activeBooking == null) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Booking booking = d.getValue(Booking.class);
                        if (booking != null) {
                            Log.e("ProviderDashboard", "Bookings value event Listener, booking found with status: " + booking.getStatus());
                            if (booking.getStatus().equals("New") && (booking.getDriverId() == null || booking.getDriverId().length() < 1)) {
                                showBookingDialog(booking);
                            } else if (booking.getStatus().equals("In Progress") && booking.getDriverId().equals(user.getId())) {
                                activeBooking = booking;
                                onBookingInProgress();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard", "in onCancelled " + databaseError.toString());
            }
        };
        bookingsReference.addValueEventListener(bookingsValueListener);
    }

    private void showBookingDialog(final Booking booking) {

        helpers.showNotification(DriverDashboard.this, "New Booking", "We have a new booking for you. It's time to get some revenue.");

        final MaterialDialog dialog = new MaterialDialog.Builder(DriverDashboard.this)
                .setTitle("NEW BOOKING")
                .setMessage("A NEW BOOKING HAS ARRIVED, DO YOU WANT TO EARN SOME MORE PROFIT?")
                .setCancelable(false)
                .setPositiveButton("DETAILS", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        Intent it = new Intent(DriverDashboard.this, ShowBookingDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Booking", booking);
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
        dialog.show();
    }

    private void sendCancelledNotification() {
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        Notification notification = new Notification();
        String id = notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(activeBooking.getId());
        notification.setUserId(activeBooking.getUserId());
        notification.setDriverId(activeBooking.getDriverId());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setUserText("Your booking has been cancelled by " + user.getFirstName() + " " + user.getLastName());
        notification.setDriverText("You cancelled your booking with " + activeCustomer.getFirstName() + " " + activeCustomer.getLastName());
        notificationReference.child(notification.getId()).setValue(notification);
        activeBooking = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.completeBooking: {
                mainSheet.setVisibility(View.GONE);
                amountLayout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.cancelBooking: {
                mainSheet.setVisibility(View.GONE);
                sheetProgress.setVisibility(View.VISIBLE);
                activeBooking.setStatus("Cancelled");
                bookingsReference.child(activeBooking.getId()).child("status").setValue(activeBooking.getStatus()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("ProviderDashboard", "Cancelled");
                        sendCancelledNotification();
                        sheetBehavior.setHideable(true);
                        sheetProgress.setVisibility(View.GONE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProviderDashboard", "Cancellation Failed");
                        helpers.showError(DriverDashboard.this, "Error", "something went wrong while cancelling the booking,plz try later");
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                    }
                });
                break;
            }
            case R.id.amountSubmit: {
                String strTotalTrashWeight = totalTrashWeight.getText().toString();
                String strTotalCharge = totalCharge.getText().toString();
                if (strTotalTrashWeight.length() < 1) {
                    totalTrashWeight.setError("Enter some valid weight.");
                    return;
                }
                if (strTotalCharge.length() < 1) {
                    totalCharge.setError("Enter some valid amount.");
                    return;
                }
                int weight = 0;
                try {
                    weight = Integer.parseInt(strTotalTrashWeight);
                } catch (Exception e) {
                    totalTrashWeight.setError("Enter some valid amount.");
                    return;
                }
                int amount = 0;
                try {
                    amount = Integer.parseInt(strTotalCharge);
                } catch (Exception e) {
                    totalCharge.setError("Enter some valid amount.");
                    return;
                }

                mainSheet.setVisibility(View.GONE);
                amountLayout.setVisibility(View.GONE);
                sheetBehavior.setPeekHeight(120);
                sheetProgress.setVisibility(View.VISIBLE);
                activeBooking.setStatus("Completed");
                activeBooking.setAmountCharged(amount);
                activeBooking.setTrashWeight(weight);
                totalCharge.setText("");
                bookingsReference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("ProviderDashboard", "Completed");
                        sheetBehavior.setHideable(true);
                        sheetProgress.setVisibility(View.GONE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProviderDashboard", "Complete");
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                    }
                });

            }
            case R.id.call: {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + activeCustomer.getPhoneNumber()));
                startActivity(intent);
                break;
            }
        }
    }


    private void onBookingInProgress() {
        bookingsReference.removeEventListener(bookingsValueListener);
        sheetBehavior.setHideable(false);
        sheetBehavior.setPeekHeight(220);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        amountLayout.setVisibility(View.GONE);
        listenToBookingChanges();

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userValueListener != null)
                    userReference.child(activeBooking.getUserId()).removeEventListener(userValueListener);
                if (userValueListener != null)
                    userReference.removeEventListener(userValueListener);

                Log.e("ProviderDashboard", "User value event listener called SnapShot: " + dataSnapshot.toString());
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                activeCustomer = dataSnapshot.getValue(User.class);
                if (activeCustomer != null && activeBooking != null) {
                    if (activeCustomer.getImage() != null && activeCustomer.getImage().length() > 0) {
                        Glide.with(DriverDashboard.this).load(activeCustomer.getImage()).into(customerImage);
                    }
                    customerName.setText(activeCustomer.getFirstName() + " " + activeCustomer.getLastName());
                    customerContact.setText(activeCustomer.getPhoneNumber());
                    customerEmail.setText(activeCustomer.getEmail());
                    trashWeight.setText(activeBooking.getTrashWeight() + " KG");

                    bookingAddress.setText(activeBooking.getPickup());
                    bookingDate.setText(activeBooking.getStartTime());

                    if (customerMarker != null) {
                        customerMarker.remove();
                    }
                    LatLng latLng = new LatLng(activeCustomer.getLatitude(), activeCustomer.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(activeCustomer.getFirstName());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    customerMarker = googleMap.addMarker(markerOptions);
                    customerMarker.showInfoWindow();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProviderDashboard", "User value event listener called");
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };

        userReference.child(activeBooking.getUserId()).addValueEventListener(userValueListener);

    }

    private void listenToBookingChanges() {
        bookingValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ProviderDashboard", "Booking Value Listener");
                Booking booking = dataSnapshot.getValue(Booking.class);
                if (activeBooking != null && booking != null) {
                    activeBooking = booking;
                    if (activeBooking.getStatus() != null) {
                        switch (activeBooking.getStatus()) {
                            case "Cancelled":
                                forBothCancelledAndCompleted();
                                break;
                            case "Completed":
                                forBothCancelledAndCompleted();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        bookingValueListener = bookingsReference.child(activeBooking.getId()).addValueEventListener(bookingValueListener);
    }

    private void forBothCancelledAndCompleted() {
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        if (userValueListener != null) {
            userReference.removeEventListener(userValueListener);
        }
        if (bookingValueListener != null) {
            bookingsReference.addValueEventListener(bookingValueListener);
        }
        if (customerMarker != null) {
            customerMarker.remove();
        }
        sheetProgress.setVisibility(View.GONE);
        mainSheet.setVisibility(View.VISIBLE);
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        listenToBookings();
    }
}
