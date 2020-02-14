package lcwu.fyp.smartbin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
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
import lcwu.fyp.smartbin.model.User;

public class BookingActivity extends AppCompatActivity {

    private LinearLayout loading;
    private TextView noBooking;
    private RecyclerView bookings;
    private Session session;
    private User user;
    private Helpers helpers;
    private List<Booking> data;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        loading = findViewById(R.id.loading);
        noBooking = findViewById(R.id.noBooking);
        bookings = findViewById(R.id.bookings);
        session = new Session(BookingActivity.this);
        user = session.getUser();
        helpers = new Helpers();
        data = new ArrayList<>();
        loadBooking();

    }
    private void loadBooking(){
        if(!helpers.isConnected(BookingActivity.this)){
            helpers.showError(BookingActivity.this,"Internet Error","No internet connection check your connection try again");
            return;

        }
        loading.setVisibility(View.VISIBLE);
        noBooking.setVisibility(View.GONE);
        bookings.setVisibility(View.GONE);
        reference.orderByChild("userId").equalTo(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    Booking booking =d.getValue(Booking.class);
                    if (booking!=null){
                        data.add(booking);
                    }

                }
                if (data.size()>0){
                    bookings.setVisibility(View.VISIBLE);
                    noBooking.setVisibility(View.GONE);


                }
                else{
                    bookings.setVisibility(View.GONE);
                    noBooking.setVisibility(View.VISIBLE);

                }
                loading.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noBooking.setVisibility(View.VISIBLE);
                bookings.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onBackPressed() {
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
}