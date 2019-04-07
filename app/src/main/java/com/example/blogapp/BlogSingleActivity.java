package com.example.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private String post_key;
    private ImageView singleView;
    private TextView titleview;
    private TextView descview;
    private DatabaseReference databaseReference;
    private Button removeButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        post_key=getIntent().getExtras().getString("Blog_id");
        singleView=findViewById(R.id.singleView);
        titleview=findViewById(R.id.titleView);
        descview=findViewById(R.id.descView);
        removeButton=findViewById(R.id.removePost);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String title= (String) dataSnapshot.child("title").getValue();
                String desc=(String) dataSnapshot.child("description").getValue();
                String image=(String) dataSnapshot.child("image").getValue();
                String uid=(String) dataSnapshot.child("userId").getValue();

                titleview.setText(title);
                descview.setText(desc);
                Picasso.with(BlogSingleActivity.this).load(image).into(singleView);
                if(auth.getCurrentUser().getUid().equals(uid)){
                    removeButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(post_key).removeValue();
                Intent main =new Intent(BlogSingleActivity.this,MainActivity.class);
                startActivity(main);

            }
        });

    }
}
