package lcwu.fyp.smartbin.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rbddevs.splashy.Splashy;

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Splashy.OnComplete() {
            @Override
            public void onComplete() {
                Session session = new Session(SplashActivity.this);
                User user = session.getUser();
                if (user == null) {
                    Intent It = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(It);
                    finish();
                } else {
                    if (user.getType() == 0) {
                        Intent It = new Intent(SplashActivity.this, DashBoard.class);
                        startActivity(It);
                    } else {
                        Intent It = new Intent(SplashActivity.this, DriverDashboard.class);
                        startActivity(It);
                    }
                    finish();
                }

            }
        }.onComplete();
        setSplashy();
    }

    void setSplashy() {

        new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle("Smart Bin")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Smart bin is use full for environment")
                .setProgressColor(R.color.colorAccent)
                .setBackgroundColor("#FFFFFF")
                .setFullScreen(true)
                .setTime(2000)
                .showProgress(true)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_BOTTOM, 1000)
                .show();
    }
}
