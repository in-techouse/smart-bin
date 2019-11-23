package lcwu.fyp.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText edtEmail, edtPasword;
    String strEmail, strPasword;
    TextView go_to_registertaion;
    TextView go_to_forgetPasword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasword = findViewById(R.id.edtPasword);
        go_to_registertaion = findViewById(R.id.go_to_registration);
        go_to_forgetPasword = findViewById(R.id.go_to_forgetPasword);

        btnLogin.setOnClickListener(this);
        go_to_registertaion.setOnClickListener(this);
        go_to_forgetPasword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.btnLogin: {

                 strEmail = edtEmail.getText().toString();
                 strPasword = edtPasword.getText().toString();

                  boolean flag= isValid();
                  if (flag) {
                      //Firebase
                      FirebaseAuth auth = FirebaseAuth.getInstance();
                      auth.signInWithEmailAndPassword(strEmail, strPasword);
                  }
                break;


            }
            case R.id.go_to_registration: {
                Intent it = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(it);
                break;

            }
            case R.id.go_to_forgetPasword: {
                Intent it = new Intent(LoginActivity.this, ForgetPaswordActivity.class);
                startActivity(it);
                break;
            }
        }
    }
    private boolean isValid(){
        boolean flag = true;
        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError("Enter a Valid email");
            flag = false;
        } else {
            edtEmail.setError(null);
        }


        if (strPasword.length() < 6) {
            edtPasword.setError("Enter Valid pasword");
            flag = false;
        } else {
            edtPasword.setError(null);
        }
        return flag;
    }


}

