package com.application.blog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccActivity extends AppCompatActivity {
    private EditText userFName;
    private EditText userLName;
    private EditText userEmail;
    private EditText userPsd;
    private Button btnSaveNewAcc;

    private ImageButton btnUserImage;
    private static final int CODE = 2;
    private Uri userImageUri;
    private Uri resultUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        userFName = (EditText) findViewById(R.id.createAccUserNameID);
        userLName = (EditText) findViewById(R.id.createAccUserLastNameID);
        userEmail =(EditText) findViewById(R.id.createAccUserEmailID);
        userPsd = (EditText) findViewById(R.id.createAccUserPsdID);
        btnSaveNewAcc = (Button) findViewById(R.id.btnSaveNewAccID);
        btnUserImage= (ImageButton) findViewById(R.id.btnUserImageID);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("users_profile_pics");

        btnSaveNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        btnUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getGallery = new Intent();
                getGallery.setAction(Intent.ACTION_GET_CONTENT);
                getGallery.setType("image/*");
                startActivityForResult(getGallery, CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE && resultCode == RESULT_OK){
            //so user can crop image
            userImageUri = data.getData();
            CropImage.activity(userImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                btnUserImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void createNewAccount() {
        final String name = userFName.getText().toString();
        final String lName = userLName.getText().toString();
        final String email = userEmail.getText().toString();
        final String psd = userPsd.getText().toString();

        if(!name.isEmpty() && !lName.isEmpty() && !email.isEmpty() && !psd.isEmpty() && resultUri != null){
            progressDialog= new ProgressDialog(this);
            progressDialog.setMessage("Adding user...");
            Log.d("gonna add user now", "doing...");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,psd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null){
                        StorageReference imagePath = storageReference.child(resultUri.getLastPathSegment());
                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDB = dbRef.child(userId);

                                        currentUserDB.child("firstName").setValue(name);
                                        currentUserDB.child("lastName").setValue(lName);
                                        currentUserDB.child("image").setValue(uri.toString());

                                        progressDialog.dismiss();

                                        startActivity(new Intent(CreateAccActivity.this, PostlistActivity.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        } else {
            Toast.makeText(this, "Please fill in the new user information", Toast.LENGTH_SHORT).show();
        }
    }
}
