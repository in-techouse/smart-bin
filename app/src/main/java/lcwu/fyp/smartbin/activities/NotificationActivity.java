package lcwu.fyp.smartbin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import lcwu.fyp.smartbin.R;

public class NotificationActivity extends AppCompatActivity {

    private LinearLayout loading;
    private TextView noNotification;
    private RecyclerView notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        loading = findViewById(R.id.loading);
        noNotification= findViewById(R.id.noNotifications);
        notifications = findViewById(R.id.notifications);
    }
}
