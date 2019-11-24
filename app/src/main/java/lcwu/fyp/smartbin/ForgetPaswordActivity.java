package lcwu.fyp.smartbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPaswordActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnForSubmit;
    String strForEmail;
    ProgressBar ForgetProgress;

    EditText edtForEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pasword);

        btnForSubmit = findViewById(R.id.btnForSubmit);
        edtForEmail = findViewById(R.id.edtForEmail);
        ForgetProgress = findViewById(R.id.ForgetProgress);

        btnForSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnForSubmit: {
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

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ForgetProgress.setVisibility(View.GONE);
                            btnForSubmit.setVisibility(View.VISIBLE);
                        }
                    });


                    }
                break;

                }
            }


        }


    private boolean isVisibal(){
        boolean flag = true;
        if (strForEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strForEmail).matches()) {
            edtForEmail.setError("Enter a Valid email");
            flag = false;
        }
        else {
            edtForEmail.setError(null);
        }
            return flag;

    }
}
