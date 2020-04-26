package com.application.blog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Model.Blog;

public class AddPostActivity extends AppCompatActivity {
    private EditText postTitle;
    private EditText postDesc;
    private Button btnSave;
    private ImageButton postImage;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db;
    private StorageReference storageReference;

    private ProgressDialog mProgress;

    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        

        mProgress = new ProgressDialog(this);
        db = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("Blog_images");

        postTitle = (EditText) findViewById(R.id.addpostTitleList);
        postDesc = (EditText) findViewById(R.id.addpostTextList);
        postImage = (ImageButton) findViewById(R.id.addpostImageListID);
        btnSave = (Button) findViewById(R.id.btnAddPostID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startPosting();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            postImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {
        final String title = postTitle.getText().toString();
        final String desc = postDesc.getText().toString().trim();

        if (!title.isEmpty() && !desc.isEmpty() && mImageUri != null ){

            mProgress.setMessage("Posting to blog...");
            mProgress.show();
            final StorageReference filepath = storageReference.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            DatabaseReference newPost = db.push();
                            Map<String, String> dataToSave = new HashMap<>();
                            dataToSave.put("title", title);
                            dataToSave.put("desc", desc);
                            dataToSave.put("image", uri.toString());
                            dataToSave.put("date", String.valueOf(java.lang.System.currentTimeMillis()));
                            dataToSave.put("userId", mUser.getUid());
                            Log.d("dataToSave", dataToSave.toString());
                            newPost.setValue(dataToSave);

                            mProgress.dismiss();

                            startActivity(new Intent(AddPostActivity.this, PostlistActivity.class));
                            finish();
                        }
                    });
                }

            });


        } else {
            Toast.makeText(this, "Fill in the blog info", Toast.LENGTH_SHORT).show();
        }

    }
}
