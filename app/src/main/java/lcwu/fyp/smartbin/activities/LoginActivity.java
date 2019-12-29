package lcwu.fyp.smartbin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.User;

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
                    helpers.showError(LoginActivity.this, "Internet Error","No internet found check your internet connection and try again?");
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
                                      DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                      String id = strEmail.replace("@","-");
                                      id = id.replace(".","_");
                                      reference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              if (dataSnapshot.getValue()!=null){
                                                  //Data is valid
                                                  User u = dataSnapshot.getValue(User.class);
                                                  Session session = new Session(LoginActivity.this);
                                                  session.setSession(u);
                                                  Intent intent = new Intent(LoginActivity.this,DashBoard.class);
                                                  startActivity(intent);
                                                  finish();
                                              }
                                              else{
                                                  LoginProgress.setVisibility(View.GONE);
                                                  btnLogin.setVisibility(View.VISIBLE);
                                                  helpers.showError(LoginActivity.this,"Login Failed","Somethng Went Wrong");
                                              }

                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {
                                              LoginProgress.setVisibility(View.GONE);
                                              btnLogin.setVisibility(View.VISIBLE);
                                              helpers.showError(LoginActivity.this,"Login Failed","Something went wrong");

                                          }
                                      });

                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      LoginProgress.setVisibility(View.GONE);
                                      btnLogin.setVisibility(View.VISIBLE);
                                      helpers.showError(LoginActivity.this, "Login Failed", e.getMessage());
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

