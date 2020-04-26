package com.application.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference refDB;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private Button btnLogin;
    private Button btnCreateAcc;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLoginID);
        btnCreateAcc = (Button) findViewById(R.id.btnCreateAccID);

        email = (EditText) findViewById(R.id.emailID);
        password = (EditText) findViewById(R.id.passwordID);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                if(mUser != null){
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(MainActivity.this, "Not signed In", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText().toString()) &&
                        !TextUtils.isEmpty(password.getText().toString())){
                    String emailStr = email.getText().toString();
                    String psd = password.getText().toString();
                    login(emailStr, psd);
                }
            }
        });

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccActivity.class));
                finish();
            }
        });


    }

    private void login(String emailStr, String psd) {
        mAuth.signInWithEmailAndPassword(emailStr, psd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("signInWithEmail:", "success");
                            Toast.makeText(MainActivity.this, "Signed in.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, PostlistActivity.class));
                            finish();
                        } else {
                            Log.w("signInWithEmail:", "failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.signOutID){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
