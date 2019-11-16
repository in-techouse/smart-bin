package lcwu.fyp.smartbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.os.Bundle;
import android.widget.Toast;

import com.rbddevs.splashy.Splashy;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
//                .setBackgroundResource("#000000")
                .setFullScreen(true)
                .setTime(5000)
                .showProgress(true)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_BOTTOM, 1000)
                .show();
    }

    void checkLogin(){
        new Splashy(this).setInfiniteDuration(true).show();   // For JAVA : new Splashy(this)

        // Some mock example response operation
//        Response.onResponse(object  : Response.onResponse{
//            override fun onResponse(response){
//                Splashy.hide()				// Hide after operation
//            }
//
//        }

        // Listener for completion of splash screen
//        Splashy.onComplete(object : Splashy.OnComplete {
//            override fun onComplete() {
//                Toast.makeText(this@MainActivity, "Logged In", Toast.LENGTH_SHORT).show()
//            }
//
//        })
    }
}
