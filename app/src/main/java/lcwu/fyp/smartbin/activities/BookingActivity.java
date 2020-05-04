package lcwu.fyp.smartbin.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.adapters.BookingAdapter;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.Booking;
import lcwu.fyp.smartbin.model.User;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout loading;
    private TextView noBooking;
    private RecyclerView bookings;
    private Session session;
    private User user;
    private List<Booking> Data;
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener bookingListener, userListener;
    private BookingAdapter bookingAdapter;
    private String orderBy;
    private BottomSheetBehavior sheetBehavior;
    private Button closeSheet;
    private ProgressBar sheetProgress;
    private LinearLayout mainSheet;
    private CircleImageView image;
    private TextView about, name, date, status, totalCharge, address, trashWeight;
    private RelativeLayout userLayout;
    private Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        loading = findViewById(R.id.loading);
        noBooking = findViewById(R.id.noBooking);
        bookings = findViewById(R.id.bookings);
        session = new Session(BookingActivity.this);
        user = session.getUser();
        bookingAdapter = new BookingAdapter(BookingActivity.this, BookingActivity.this);
        bookings.setLayoutManager(new LinearLayoutManager(BookingActivity.this));
        bookings.setAdapter(bookingAdapter);
        Data = new ArrayList<>();
        helpers = new Helpers();

        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(true);
        sheetBehavior.setPeekHeight(0);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        closeSheet = findViewById(R.id.closeSheet);
        closeSheet.setOnClickListener(this);
        sheetProgress = findViewById(R.id.sheetProgress);
        mainSheet = findViewById(R.id.mainSheet);

        image = findViewById(R.id.image);
        about = findViewById(R.id.about);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);

        status = findViewById(R.id.status);
        totalCharge = findViewById(R.id.totalCharge);
        address = findViewById(R.id.address);
        trashWeight = findViewById(R.id.trashWeight);
        userLayout = findViewById(R.id.userLayout);

        if (user.getType() == 0) {
            orderBy = "userId";
        } else {
            orderBy = "driverId";
        }
        loadBookings();


    }

    private void loadBookings() {

        if (!helpers.isConnected(BookingActivity.this)) {
            helpers.showError(BookingActivity.this, "Internet Error", "No internet connection check your connection try again");
            return;

        }
        loading.setVisibility(View.VISIBLE);
        noBooking.setVisibility(View.GONE);
        bookings.setVisibility(View.GONE);

        bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Bookings", "Data Snap Shot: " + dataSnapshot.toString());
                Data.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Booking b = d.getValue(Booking.class);
                    if (b != null) {
                        Data.add(b);
                    }
                }
                Collections.reverse(Data);
                Log.e("Bookings", "Data List Size: " + Data.size());
                if (Data.size() > 0) {
                    Log.e("Bookings", "If, list visible");
                    bookings.setVisibility(View.VISIBLE);
                    noBooking.setVisibility(View.GONE);
                } else {
                    Log.e("Bookings", "Else, list invisible");
                    noBooking.setVisibility(View.VISIBLE);
                    bookings.setVisibility(View.GONE);
                }
                loading.setVisibility(View.GONE);
                bookingAdapter.setData(Data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noBooking.setVisibility(View.VISIBLE);
                bookings.setVisibility(View.GONE);
            }
        };
        bookingReference.orderByChild(orderBy).equalTo(user.getId()).addValueEventListener(bookingListener);
    }

    public void showBottomSheet(final Booking booking) {
        sheetBehavior.setHideable(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);

        if (user.getType() == 0) {
            about.setText("Provider details");
        } else {
            about.setText("Customer details");
        }
        date.setText(booking.getStartTime());
        address.setText(booking.getPickup());
        totalCharge.setText(booking.getAmountCharged() + " RS.");
        trashWeight.setText(booking.getTrashWeight() + " kg");
        status.setText(booking.getStatus());

        if (booking.getDriverId().equals("")) {
            about.setVisibility(View.GONE);
            userLayout.setVisibility(View.GONE);
            sheetProgress.setVisibility(View.GONE);
            mainSheet.setVisibility(View.VISIBLE);
            return;
        }

        about.setVisibility(View.VISIBLE);
        userLayout.setVisibility(View.VISIBLE);

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (user.getType() == 0) {
                    userReference.child(booking.getDriverId()).removeEventListener(userListener);
                } else {
                    userReference.child(booking.getUserId()).removeEventListener(userListener);
                }
                userReference.removeEventListener(userListener);
                User tempUser = dataSnapshot.getValue(User.class);
                if (tempUser != null) {
                    if (tempUser.getImage() != null && tempUser.getImage().length() > 0) {
                        Glide.with(BookingActivity.this).load(tempUser.getImage()).into(image);
                    } else {
                        image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
                    }
                    String strName = tempUser.getFirstName() + " " + tempUser.getLastName();
                    name.setText(strName);
                    sheetBehavior.setPeekHeight(800);
                    sheetProgress.setVisibility(View.GONE);
                    mainSheet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (user.getType() == 0) {
                    userReference.child(booking.getDriverId()).removeEventListener(userListener);
                } else {
                    userReference.child(booking.getUserId()).removeEventListener(userListener);
                }
                userReference.removeEventListener(userListener);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };
        if (user.getType() == 0) {
            userReference.child(booking.getDriverId()).addValueEventListener(userListener);
        } else {
            userReference.child(booking.getUserId()).addValueEventListener(userListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setHideable(true);
            sheetBehavior.setPeekHeight(0);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.closeSheet: {
                sheetBehavior.setHideable(true);
                sheetBehavior.setPeekHeight(0);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingListener != null) {
            bookingReference.removeEventListener(bookingListener);
        }
        if (userListener != null) {
            userReference.removeEventListener(userListener);
        }
    }


}