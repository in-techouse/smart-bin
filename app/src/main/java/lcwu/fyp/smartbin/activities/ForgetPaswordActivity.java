package lcwu.fyp.smartbin.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;

public class ForgetPaswordActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnForSubmit;
    String strForEmail;
    ProgressBar ForgetProgress;
    Helpers helpers;
    EditText edtForEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pasword);

        btnForSubmit = findViewById(R.id.btnForSubmit);
        edtForEmail = findViewById(R.id.edtForEmail);
        ForgetProgress = findViewById(R.id.ForgetProgress);

        btnForSubmit.setOnClickListener(this);

        helpers = new Helpers();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnForSubmit: {
                if (!helpers.isConnected(ForgetPaswordActivity.this)) {
                    helpers.showError(ForgetPaswordActivity.this, "ERROR!", "No internet connection found.\nPlease connect to a network and try again.");
                }
                strForEmail = edtForEmail.getText().toString();

                boolean flag = isVisibal();
                if (flag) {
                    //Firebase
                    ForgetProgress.setVisibility(View.VISIBLE);
                    btnForSubmit.setVisibility(View.GONE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(strForEmail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ForgetProgress.setVisibility(View.GONE);
                                    btnForSubmit.setVisibility(View.VISIBLE);
                                    helpers.showError(ForgetPaswordActivity.this, "A password recovery email has been sent to your account", "");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ForgetProgress.setVisibility(View.GONE);
                            btnForSubmit.setVisibility(View.VISIBLE);
                            helpers.showError(ForgetPaswordActivity.this, "ERROR!", e.getMessage());
                        }
                    });


                }
                break;

            }
        }


    }


    private boolean isVisibal() {
        boolean flag = true;
        if (strForEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strForEmail).matches()) {
            edtForEmail.setError("Enter a Valid email");
            flag = false;
        } else {
            edtForEmail.setError(null);
        }
        return flag;
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
