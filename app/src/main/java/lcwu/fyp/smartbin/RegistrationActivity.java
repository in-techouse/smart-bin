package lcwu.fyp.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegister;
    TextView go_to_login;


    EditText edt1stName, edtlastName, edtEmail, edtPasword, edtConPasword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnRegister = findViewById(R.id.btnRegistration);
        edt1stName = findViewById(R.id.edt1stName);
        edtlastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasword = findViewById(R.id.edtPasword);
        edtConPasword = findViewById(R.id.edtConPasword);

        go_to_login = findViewById(R.id.go_to_login);




        btnRegister.setOnClickListener(this);
        go_to_login.setOnClickListener(this);




        }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnRegistration:{
                String str1stName = edt1stName.getText().toString();
                String strlastName= edtlastName.getText().toString();
                String strRegEmail = edtEmail.getText().toString();
                String strRegPasword = edtPasword.getText().toString();
                String strConPasword = edtConPasword.getText().toString();

                if (str1stName.length()<3){
                    edt1stName.setError("Enter a Valid 1stName");
                }
                else{
                    edt1stName.setError(null);
                }
                if (strlastName.length()<3){
                    edtlastName.setError("Enter a Valid lastName");

                }
                else {
                    edtlastName.setError(null);
                }

                if (strRegEmail.length()<6|| !Patterns.EMAIL_ADDRESS.matcher(strRegEmail).matches()){
                    edtEmail.setError("Enter a Valid RegEmail");
                }
                else {
                    edtEmail.setError(null);
                }
                if (strRegPasword.length()<6){
                    edtPasword.setError("Enter Valid RegPasword");
                }
                else {
                    edtPasword.setError(null);
                }
                if (strConPasword.length()<6){
                    edtConPasword.setError("Enter Valid ConPasword");
                }
                else{
                    edtConPasword.setError(null);
                }


                break;
            }
            case R.id.go_to_login:{
                Intent it = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(it);
                break;
            }

        }

    }
}
