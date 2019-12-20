package lcwu.fyp.smartbin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.rbddevs.splashy.Splashy;

import lcwu.fyp.smartbin.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Splashy.OnComplete() {
            @Override
            public void onComplete() {
                Intent It = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(It);
                finish();
            }
        }.onComplete();

        setSplashy();

    }

    void setSplashy() {

        new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle("Smart Bin")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Smart bin is usfull for envorment")
                .setProgressColor(R.color.colorAccent)
                .setBackgroundResource(R.drawable.ic_launcher_background)
                .setFullScreen(true)
                .setTime(2500)
                .showProgress(true)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_BOTTOM, 1000)
                .show();
    }
}
