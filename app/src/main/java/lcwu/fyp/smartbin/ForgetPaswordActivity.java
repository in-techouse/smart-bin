package lcwu.fyp.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class ForgetPaswordActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnForSubmit;

    EditText edtForEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pasword);

        btnForSubmit = findViewById(R.id.btnForSubmit);
        edtForEmail = findViewById(R.id.edtForEmail);

        btnForSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
            switch (id){
                case R.id.btnForSubmit: {

                    String strForEmail = edtForEmail.getText().toString();

                    if (strForEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strForEmail).matches()) {
                        edtForEmail.setError("Enter a Valid email");
                    } else {
                        edtForEmail.setError(null);
                    }

                }
            }



    }
}
