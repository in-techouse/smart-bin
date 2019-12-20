package lcwu.fyp.smartbin.activities;

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

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText edtEmail, edtPasword;
    String strEmail, strPasword;
    TextView go_to_registertaion;
    TextView go_to_forgetPasword;
    ProgressBar LoginProgress;
    Helpers helpers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasword = findViewById(R.id.edtPasword);
        go_to_registertaion = findViewById(R.id.go_to_registration);
        go_to_forgetPasword = findViewById(R.id.go_to_forgetPasword);
        LoginProgress = findViewById(R.id.LoginProgress);

        btnLogin.setOnClickListener(this);
        go_to_registertaion.setOnClickListener(this);
        go_to_forgetPasword.setOnClickListener(this);

        helpers = new Helpers();
        }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.btnLogin: {

                //Check Internet
                boolean isConn =helpers.isConnected(LoginActivity.this);
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

                 strEmail = edtEmail.getText().toString();
                 strPasword = edtPasword.getText().toString();

                  boolean flag= isValid();
                  if (flag) {

                      //Firebase
                      LoginProgress.setVisibility(View.VISIBLE);
                      btnLogin.setVisibility(View.GONE);
                      FirebaseAuth auth = FirebaseAuth.getInstance();
                      auth.signInWithEmailAndPassword(strEmail, strPasword)
                              .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                  @Override
                                  public void onSuccess(AuthResult authResult) {
                                      LoginProgress.setVisibility(View.GONE);
                                      btnLogin.setVisibility(View.VISIBLE);
                                      Intent it = new Intent(LoginActivity.this,DashBoard.class);
                                      startActivity(it);
                                      finish();
                                      Log.e("Login", "Success");

                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      LoginProgress.setVisibility(View.GONE);
                                      btnLogin.setVisibility(View.VISIBLE);
                                      MaterialDialog mDialog = new MaterialDialog.Builder(LoginActivity.this)
                                              .setTitle("Login Failed")
                                              .setMessage(e.getMessage())
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
                                      Log.e("Login", "Faliure" +e.getMessage());
                                  }
                              });
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

