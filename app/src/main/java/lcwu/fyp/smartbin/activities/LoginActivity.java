package lcwu.fyp.smartbin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener;
    private Button btnLogin;
    private EditText edtEmail, edtPasword;
    private String strEmail, strPasword;
    private ProgressBar LoginProgress;
    private Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasword = findViewById(R.id.edtPasword);
        TextView go_to_registertaion = findViewById(R.id.go_to_registration);
        TextView go_to_forgetPasword = findViewById(R.id.go_to_forgetPasword);
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
                boolean isConn = helpers.isConnected(LoginActivity.this);
                if (!isConn) {
                    helpers.showError(LoginActivity.this, "Internet Error", "No internet found check your internet connection and try again?");
                    return;
                }

                strEmail = edtEmail.getText().toString();
                strPasword = edtPasword.getText().toString();

                boolean flag = isValid();
                if (flag) {

                    //Firebase
                    LoginProgress.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(strEmail, strPasword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String uid = strEmail.replace("@", "-");
                                    final String id = uid.replace(".", "_");
                                    listener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (listener != null)
                                                reference.child("Users").child(id).removeEventListener(listener);
                                            if (listener != null)
                                                reference.removeEventListener(listener);

                                            if (dataSnapshot.getValue() != null) {
                                                // Data is valid
                                                User u = dataSnapshot.getValue(User.class);
                                                if (u == null) {
                                                    LoginProgress.setVisibility(View.GONE);
                                                    btnLogin.setVisibility(View.VISIBLE);
                                                    helpers.showError(LoginActivity.this, "Login Failed", "Somethng Went Wrong");
                                                    return;
                                                }
                                                Session session = new Session(LoginActivity.this);
                                                session.setSession(u);
                                                if (u.getType() == 0) {
                                                    Intent it = new Intent(LoginActivity.this, DashBoard.class);
                                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(it);
                                                } else {
                                                    Intent intent = new Intent(LoginActivity.this, DriverDashboard.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            } else {
                                                LoginProgress.setVisibility(View.GONE);
                                                btnLogin.setVisibility(View.VISIBLE);
                                                helpers.showError(LoginActivity.this, "Login Failed", "Somethng Went Wrong");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            if (listener != null)
                                                reference.child("Users").child(id).removeEventListener(listener);
                                            if (listener != null)
                                                reference.removeEventListener(listener);

                                            LoginProgress.setVisibility(View.GONE);
                                            btnLogin.setVisibility(View.VISIBLE);
                                            helpers.showError(LoginActivity.this, "Login Failed", "Something went wrong");
                                        }
                                    };
                                    reference.child("Users").child(id).addValueEventListener(listener);

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

    private boolean isValid() {
        boolean flag = true;
        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError("Enter valid Email");
            flag = false;
        } else {
            edtEmail.setError(null);
        }

        if (strPasword.length() < 6) {
            edtPasword.setError("Enter valid Password");
            flag = false;
        } else {
            edtPasword.setError(null);
        }
        return flag;
    }
}

