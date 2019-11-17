package lcwu.fyp.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText edtEmail, edtPasword;
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

                String strEmail = edtEmail.getText().toString();
                String strPasword = edtPasword.getText().toString();

                if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    edtEmail.setError("Enter a Valid email");
                } else {
                    edtEmail.setError(null);
                }


                if (strPasword.length() < 6) {
                    edtPasword.setError("Enter Valid pasword");
                } else {
                    edtPasword.setError(null);
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
}

