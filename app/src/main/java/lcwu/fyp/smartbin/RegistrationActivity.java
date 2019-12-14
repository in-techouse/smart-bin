package lcwu.fyp.smartbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import lcwu.fyp.smartbin.director.Helpers;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    Helpers helpers;
    Button btnRegister;
    String str1stName, strlastName, strRegEmail, strRegPasword, strConPasword;
    TextView go_to_login;
    EditText edt1stName, edtlastName, edtEmail, edtPasword, edtConPasword;
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
                    //Show Error Message, because no internet found
                    MaterialDialog mDialog = new MaterialDialog.Builder(this)
                            .setTitle("Internet Error?")
                            .setMessage("No internet found check your internet connection and try again?")
                            .setCancelable(false)
                            .setPositiveButton("Ok", R.drawable.ic_action_ok, new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    // Delete Operation
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Close", R.drawable.ic_action_close, new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .build();

                    // Show Dialog
                    mDialog.show();
                    return;
                }


                str1stName = edt1stName.getText().toString();
                strlastName = edtlastName.getText().toString();
                strRegEmail = edtEmail.getText().toString();
                strRegPasword = edtPasword.getText().toString();
                strConPasword = edtConPasword.getText().toString();

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
                                    RegistrationProgress.setVisibility(View.GONE);
                                    btnRegister.setVisibility(View.VISIBLE);
                                    Intent it = new Intent(RegistrationActivity.this,DashBoard.class);
                                    startActivity(it);
                                    finish();
                                    Log.e("Registration","Success");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    RegistrationProgress.setVisibility(View.GONE);
                                    btnRegister.setVisibility(View.VISIBLE);
                                    Log.e("Registration", "Faliure " + e.getMessage());

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
            edt1stName.setError("Enter a Valid 1stName");
            flag = false;
        } else {
            edt1stName.setError(null);

        }
        if (strlastName.length() < 3) {
            edtlastName.setError("Enter a Valid lastName");
            flag = false;


        } else {
            edtlastName.setError(null);
        }

        if (strRegEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strRegEmail).matches()) {
            edtEmail.setError("Enter a Valid RegEmail");
            flag = false;

        } else {
            edtEmail.setError(null);
        }
        if (strRegPasword.length() < 6) {
            edtPasword.setError("Enter Valid RegPasword");
            flag = false;

        } else {
            edtPasword.setError(null);
        }
        if (strConPasword.length() < 6) {
            edtConPasword.setError("Enter Valid ConPasword");
            flag = false;

        } else {
            edtConPasword.setError(null);
        }
        return flag;
    }
}
