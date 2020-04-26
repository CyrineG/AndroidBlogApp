package com.application.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Model.Blog;
import UI.BlogRecycleViewAdapter;

public class PostlistActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference refDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView recyclerView;
    private BlogRecycleViewAdapter adapter;
    private List<Blog> blogList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        refDB = database.getReference().child("Blog");
        refDB.keepSynced(true);
        mUser = mAuth.getCurrentUser();

        blogList = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        refDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);

                //to make latest posts appear first
                Collections.reverse(blogList);

                adapter = new BlogRecycleViewAdapter(blogList,getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.signOutID){
            if(mUser != null && mAuth != null) {
                mAuth.signOut();
                startActivity(new Intent(PostlistActivity.this, MainActivity.class));
                finish();
            }
        }

        if (item.getItemId()==R.id.addID){
            if(mUser != null && mAuth != null) {
                startActivity(new Intent(PostlistActivity.this, AddPostActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
