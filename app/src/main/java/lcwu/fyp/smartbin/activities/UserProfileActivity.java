package lcwu.fyp.smartbin.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import lcwu.fyp.smartbin.R;
import lcwu.fyp.smartbin.director.Helpers;
import lcwu.fyp.smartbin.director.Session;
import lcwu.fyp.smartbin.model.User;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    private EditText profileFirstName, profileLastName, profilePhone;
    Session session;
    User user;
    Button update;
    ProgressBar updateProgress;
    Helpers helpers;
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private String strFirstName, strlastName, strPhone;
    ImageView image;
    boolean isImage = false;
    Uri imageUri;

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileFirstName = findViewById(R.id.profileFirstName);
        profileLastName = findViewById(R.id.profileLastName);
        EditText profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        update = findViewById(R.id.btnUpdation);
        updateProgress = findViewById(R.id.updationProgress);

        session = new Session(UserProfileActivity.this);
        user = session.getUser();


        image = findViewById(R.id.profileImage);

        if (user.getImage() != null) {
            Glide.with(UserProfileActivity.this).load(user.getImage()).into(image);
        } else {
            image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }


        profileFirstName.setText(user.getFirstName());
        profileLastName.setText(user.getLastName());
        profileEmail.setText(user.getEmail());
        profilePhone.setText(user.getPhoneNumber());

        update.setOnClickListener(this);
        helpers = new Helpers();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUpdation: {
                boolean isConn = helpers.isConnected(UserProfileActivity.this);
                if (!isConn) {
                    helpers.showError(UserProfileActivity.this, "Internet Error", "No internet found check your internet connection and try again?");
                    return;
                }
                updateProgress.setVisibility(View.VISIBLE);
                update.setVisibility(View.GONE);

                strFirstName = profileFirstName.getText().toString();
                strlastName = profileLastName.getText().toString();
                strPhone = profilePhone.getText().toString();

                boolean flag = isValid();
                if (flag) {

                    if (isImage) {
                        uploadImage();
                    } else {
                        user.setImage("");
                        saveUser();
                    }

                }
                break;
            }
            case R.id.fab: {
                boolean flag = hasPermissions(UserProfileActivity.this, PERMISSIONS);
                if (!flag) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this, PERMISSIONS, 1);
                } else {
                    openGallery();
                }
                break;

            }

        }
    }

    private boolean isValid() {
        boolean flag = true;
        if (strFirstName.length() < 3) {
            profileFirstName.setError("Enter valid name");
            flag = false;
        } else {
            profileFirstName.setError(null);

        }
        if (strlastName.length() < 3) {
            profileLastName.setError("Enter valid name");
            flag = false;
        } else {
            profileLastName.setError(null);
        }

        if (strPhone.length() != 11) {
            profilePhone.setError("Enter valid phone number");
            flag = false;
        } else {
            profilePhone.setError(null);
        }

        return flag;
    }

    void saveUser() {

        final String userId = user.getId();
        user.setFirstName(strFirstName);
        user.setLastName(strlastName);
        user.setPhoneNumber(strPhone);

        userReference.child(userId).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateProgress.setVisibility(View.GONE);
                session.setSession(user);
                Log.e("user data", "user data is " + user.getFirstName() + " " + user.getLastName());
                Intent in;

                if (user.getType() == 0) {
                    in = new Intent(UserProfileActivity.this, DashBoard.class);
                } else {
                    in = new Intent(UserProfileActivity.this, DriverDashboard.class);
                }
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helpers.showError(UserProfileActivity.this, "Profile Error", "Data Could not Be Updated!");
            }
        });
    }

    private void uploadImage() {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(user.getId());
        Calendar calendar = Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis() + "").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        user.setImage(uri.toString());
                                        saveUser();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Login", "Upload Image Url: " + e.getMessage());
                                        updateProgress.setVisibility(View.GONE);
                                        update.setVisibility(View.VISIBLE);
                                        helpers.showError(UserProfileActivity.this, "ERROR!", "Something went wrong.\n Please try again later.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Login", "Upload Image Url: " + e.getMessage());
                        updateProgress.setVisibility(View.GONE);
                        update.setVisibility(View.VISIBLE);
                        helpers.showError(UserProfileActivity.this, "ERROR!", "Something went wrong.\n Please try again later.");
                    }
                });
    }

    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void openGallery() {
        BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("lcwu.fyp.gohytch.fileprovider")
                .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .hideCameraTile() //Default: show. Set this if you don't want user to take photo.
                .hideGalleryTile() //Default: show. Set this if you don't want to further let user select from a gallery app. In such case, I suggest you to set maximum displaying images to Integer.MAX_VALUE.
                .setTag("A request ID") //Default: null. Set this if you need to identify which picker is calling back your fragment / activity.
                .build();
        singleSelectionPicker.show(getSupportFragmentManager(), "picker");
    }


    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        isImage = true;
        imageUri = uri;
        Glide.with(UserProfileActivity.this).load(imageUri).into(image);
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
