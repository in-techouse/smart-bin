package lcwu.fyp.smartbin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.Booking;
import lcwu.fyp.smartbin.model.Notification;
import lcwu.fyp.smartbin.model.User;

public class NotificationActivity extends AppCompatActivity {

    private LinearLayout loading;
    private TextView noNotification;
    private RecyclerView notifications;
    private Session session;
    private User user;
    private Helpers helpers;
    private List<Notification> data;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        loading = findViewById(R.id.loading);
        noNotification = findViewById(R.id.noNotifications);
        notifications = findViewById(R.id.notifications);
        session = new Session(NotificationActivity.this);
        user = session.getUser();
        helpers = new Helpers();
        data = new ArrayList<>();
        loadNotification();
    }

    private void loadNotification() {
        if (!helpers.isConnected(NotificationActivity.this)) {
            helpers.showError(NotificationActivity.this, "Internet Error", "No internet connection check your connection try again");
            return;
        }

        loading.setVisibility(View.VISIBLE);
        noNotification.setVisibility(View.GONE);
        notifications.setVisibility(View.GONE);
        reference.orderByChild("userId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    Notification notification =d.getValue(Notification.class);
                    if (notification!=null){
                        data.add(notification);
                    }

                }
                if (data.size()>0){
                    notifications.setVisibility(View.VISIBLE);
                    noNotification.setVisibility(View.GONE);


                }
                else{
                    notifications.setVisibility(View.GONE);
                    noNotification.setVisibility(View.VISIBLE);


                }
                loading.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noNotification.setVisibility(View.VISIBLE);
                notifications.setVisibility(View.GONE);

            }
        });

    }
}
