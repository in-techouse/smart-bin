package lcwu.fyp.smartbin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.User;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    Helpers helpers;
    Button btnRegister;
    String str1stName, strlastName, strRegEmail, strRegPasword, strConPasword, strPhone = "";
    TextView go_to_login;
    EditText edt1stName, edtlastName, edtEmail, edtPasword, edtConPasword, edtPhone;
    ProgressBar RegistrationProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        helpers = new Helpers();

        btnRegister = findViewById(R.id.btnRegistration);
        edt1stName = findViewById(R.id.edt1stName);
        edtlastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasword = findViewById(R.id.edtPasword);
        edtConPasword = findViewById(R.id.edtConPasword);
        edtPhone = findViewById(R.id.edtPhone);
        RegistrationProgress = findViewById(R.id.RegistrationProgress);

        go_to_login = findViewById(R.id.go_to_login);


        btnRegister.setOnClickListener(this);
        go_to_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnRegistration: {
                //Check Internet
                boolean isConn = helpers.isConnected(RegistrationActivity.this);
                if (!isConn){
                    helpers.showError(RegistrationActivity.this,"Internet Error", "No internet found check your internet connection and try again?");
                    return;
                }


                str1stName = edt1stName.getText().toString();
                strlastName = edtlastName.getText().toString();
                strRegEmail = edtEmail.getText().toString();
                strRegPasword = edtPasword.getText().toString();
                strConPasword = edtConPasword.getText().toString();
                strPhone = edtPhone.getText().toString();

                boolean flag = isValid();
                if (flag) {
                    //Firebase
                    RegistrationProgress.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.GONE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(strRegEmail, strRegPasword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    final User user = new User();
                                    user.setFirstName(str1stName);
                                    user.setLastName(strlastName);
                                    user.setEmail(strRegEmail);
                                    user.setPhoneNumber(strPhone);
                                    user.setType(0);
                                    user.setLicenseNumber("");

                                    String id = strRegEmail.replace("@","-");
                                    id = id.replace(".","_");
                                    user.setId(id);
                                   reference.child("Users").child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           Session session = new Session(RegistrationActivity.this);
                                           session.setSession(user);
                                           Intent intent = new Intent(RegistrationActivity.this,DashBoard.class);
                                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                           startActivity(intent);
                                           finish();

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           RegistrationProgress.setVisibility(View.GONE);
                                           btnRegister.setVisibility(View.VISIBLE);
                                           helpers.showError(RegistrationActivity.this,"Registration Failed","Something Went wrong");

                                       }
                                   });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    RegistrationProgress.setVisibility(View.GONE);
                                    btnRegister.setVisibility(View.VISIBLE);
                                    helpers.showError(RegistrationActivity.this, "Registration Failed", e.getMessage());
                                }
                            });

                }
                break;
            }
            case R.id.go_to_login: {
                Intent it = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(it);
                break;
            }
        }
    }
    private boolean isValid() {
        boolean flag = true;
        if (str1stName.length() < 3) {
            edt1stName.setError("Enter valid name");
            flag = false;
        } else {
            edt1stName.setError(null);

        }
        if (strlastName.length() < 3) {
            edtlastName.setError("Enter valid name");
            flag = false;
        } else {
            edtlastName.setError(null);
        }

        if (strPhone.length() != 11) {
            edtPhone.setError("Enter valid phone number");
            flag = false;
        } else {
            edtPhone.setError(null);
        }

        if (strRegEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strRegEmail).matches()) {
            edtEmail.setError("Enter valid email");
            flag = false;

        } else {
            edtEmail.setError(null);
        }
        if (strRegPasword.length() < 6) {
            edtPasword.setError("Enter valid password");
            flag = false;

        } else {
            edtPasword.setError(null);
        }
        if (strConPasword.length() < 6) {
            edtConPasword.setError("Enter valid password");
            flag = false;

        } else {
            edtConPasword.setError(null);
        }

        if(strRegPasword.length() > 5 && strConPasword.length() > 5 && !strRegPasword.equals(strConPasword)){
            edtConPasword.setError("Password does not match");
            flag = false;
        }
        else{
            edtConPasword.setError(null);
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
