package lcwu.fyp.smartbin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import lcwu.fyp.smartbin.adapters.NotificationAdapter;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.Booking;
import lcwu.fyp.smartbin.model.Notification;
import lcwu.fyp.smartbin.model.User;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout loading;
    private TextView noRecord;
    private RecyclerView notifications;
    private Session session;
    private User user;
    private List<Notification> Data;
    private NotificationAdapter notificationAdapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener notificationListener, bookingListener, userListener;
    private String orderBy;
    private BottomSheetBehavior sheetBehavior;
    private Button closeSheet;
    private ProgressBar sheetProgress;
    private LinearLayout mainSheet;
    private CircleImageView image;
    private TextView about, name, date,  status,trashWeight, totalCharge, address;
    private Helpers helpers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        helpers = new Helpers();

        loading = findViewById(R.id.loading);
        noRecord = findViewById(R.id.noRecord);
        notifications = findViewById(R.id.notifications);
        session = new Session(NotificationActivity.this);
        user = session.getUser();
        Data = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(NotificationActivity.this, user.getType(), NotificationActivity.this);
        notifications.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        notifications.setAdapter(notificationAdapter);

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
        trashWeight = findViewById(R.id.trashWeight);
        totalCharge = findViewById(R.id.totalCharge);
        address = findViewById(R.id.address);

        if (user.getType() == 0) {
            orderBy = "userId";
        } else {
            orderBy = "driverId";
        }

        loadNotification();


    }

    private void loadNotification() {
        if (!helpers.isConnected(NotificationActivity.this)) {
            helpers.showError(NotificationActivity.this, "Internet Error", "No internet connection check your connection try again");
            return;
        }
        loading.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        notifications.setVisibility(View.GONE);
        notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Notification n = d.getValue(Notification.class);
                    if (n != null) {
                        Data.add(n);
                    }
                }
                Collections.reverse(Data);

                if (Data.size() > 0) {
                    notifications.setVisibility(View.VISIBLE);
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                    notifications.setVisibility(View.GONE);

                }

                loading.setVisibility(View.GONE);
                notificationAdapter.setData(Data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecord.setVisibility(View.VISIBLE);
                notifications.setVisibility(View.GONE);
            }
        };
        reference.orderByChild(orderBy).equalTo(user.getPhoneNumber()).addValueEventListener(notificationListener);

    }

    public void showBottomSheet(final Notification notification) {
        sheetBehavior.setHideable(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);


        if (user.getType() == 0) {
            about.setText("Driver details");
        } else {
            about.setText("Customer details");
        }

        bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingReference.child(notification.getBookingId()).removeEventListener(bookingListener);
                bookingReference.removeEventListener(bookingListener);
                final Booking booking = dataSnapshot.getValue(Booking.class);
                if (booking != null) {
                    sheetBehavior.setHideable(false);
                    sheetBehavior.setPeekHeight(800);
                    sheetProgress.setVisibility(View.GONE);
                    mainSheet.setVisibility(View.VISIBLE);
                    date.setText(booking.getStartTime());
                    address.setText(booking.getPickup());
                    totalCharge.setText(booking.getAmountCharged() + " RS.");
                    trashWeight.setText(booking.getTrashWeight()+" kg");

                    status.setText(booking.getStatus());
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
                                    Glide.with(NotificationActivity.this).load(tempUser.getImage()).into(image);
                                } else {
                                    image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
                                }
                                String strName = tempUser.getFirstName() + " " + tempUser.getLastName();
                                name.setText(strName);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bookingReference.child(notification.getBookingId()).removeEventListener(bookingListener);
                bookingReference.removeEventListener(bookingListener);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };
        bookingReference.child(notification.getBookingId()).addValueEventListener(bookingListener);
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

        if (notificationListener != null) {
            reference.removeEventListener(notificationListener);
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


}
