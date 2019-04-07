package com.example.blogapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mBlogList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference dddd;
    private DatabaseReference databaseLike;
    private boolean process_like=false;
    private DatabaseReference databaseCurrentUser;
    private Query databaseCurrent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        dddd=FirebaseDatabase.getInstance().getReference().child("Users");
        databaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        databaseCurrentUser=FirebaseDatabase.getInstance().getReference().child("Blog");
        //databaseCurrent=databaseCurrentUser.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( firebaseAuth.getCurrentUser()== null )
                {
                   Intent loginIntent=new Intent(MainActivity.this,
                           Login_Activity.class);
                   loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(loginIntent);
                }
//                else
//                    {
//                    chek();
//                }
            }
        };
        mBlogList=findViewById(R.id.blog_list);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Blog");

        //databaseReference.keepSynced(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        //chek();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //chek();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,R.layout.blog_row,BlogViewHolder.class,databaseReference){
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder,Blog model,int position){

              final String post_key=getRef(position).getKey();
              viewHolder.setTitle(model.getTitle());
              viewHolder.setDesc(model.getDescription());
              viewHolder.setImage(getApplicationContext(),model.getImage());
              viewHolder.setUsername(model.getUsername());
              viewHolder.setLikeButton(post_key);

              viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
//                      Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_LONG).show();

                      Intent singleBlogIntent=new Intent(MainActivity.this,
                              BlogSingleActivity.class);
                      singleBlogIntent.putExtra("Blog_id",post_key);
                      startActivity(singleBlogIntent);

                  }
              });
              viewHolder.like.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      process_like=true;

                           databaseLike.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                               {
                                   if(process_like) {
                                       if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                           databaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                           process_like = false;
                                       } else {
                                           databaseLike.child(post_key).
                                                   child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                                           process_like = false;
                                       }
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError)
                               {

                               }
                           });
                      }


              });
            }

        };



       mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView post_title;
        ImageButton like;
        DatabaseReference LikeDatabase=FirebaseDatabase.getInstance().getReference().child("Likes");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
             post_title=mView.findViewById(R.id.post_title);
            like = mView.findViewById(R.id.likes);
             post_title.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                   //  Toast.makeText(MainActivity.this,"text is clicked",Toast.LENGTH_LONG).show();
                 }
             });

        }
        public void setTitle(String title)
        {
//            TextView post_title=mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc){

            TextView post_desc=mView.findViewById(R.id.post_text);
            post_desc.setText(desc);
        }
        public void setImage(final Context ctx, final String image){
            final ImageView imageView=mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(imageView);
//            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onError() {
//                    Picasso.with(ctx).load(image).into(imageView);
//                }
//            });


        }
        public void setUsername(String username){
            TextView post_username=mView.findViewById(R.id.post_username);
            post_username.setText(username);

        }
        public void setLikeButton(final String post_key){

            LikeDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        like.setImageResource(R.mipmap.sharp_thumb_up_black_18);
                    }
                    else{
                        like.setImageResource(R.mipmap.outline_thumb_up_black_18);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }

        if(item.getItemId()==R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);

    }

    private void logout() {
        mAuth.signOut();
    }
//    private void chek()
//    {
////        if(mAuth.getCurrentUser()==null )
////        {
////            Intent loginIntent=new Intent(MainActivity.this,
////                    Login_Activity.class);
////            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////            startActivity(loginIntent);
////        }
//        final String user_Id=mAuth.getCurrentUser().getUid();
//        dddd.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.hasChild(user_Id)){
//                    Intent mainIntent=new Intent(MainActivity.this,
//                            SetupActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(mainIntent);
//                }
////                else
////                {
////                    Toast.makeText(MainActivity.this,
////                            "You need to setup your account",Toast.LENGTH_LONG).show();
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
//package com.example.blogapp;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Callback;
//import com.squareup.picasso.NetworkPolicy;
//import com.squareup.picasso.Picasso;
//
//public class MainActivity extends AppCompatActivity
//{
//
//
//    private RecyclerView mBlogList;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private DatabaseReference dddd;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mAuth=FirebaseAuth.getInstance();
//        dddd=FirebaseDatabase.getInstance().getReference().child("Users");
//        mAuthListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(firebaseAuth.getCurrentUser()==null )
//                {
//                    Intent loginIntent=new Intent(MainActivity.this,
//                            Login_Activity.class);
//                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(loginIntent);
//                }
////                else
////                    {
////                    chek();
////                }
//            }
//        };
//        mBlogList=findViewById(R.id.blog_list);
//        databaseReference=FirebaseDatabase.getInstance().getReference().child("Blog");
//        mBlogList.setHasFixedSize(true);
//        mBlogList.setLayoutManager(new LinearLayoutManager(this));
//       // chek();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        ///  chek();
//        mAuth.addAuthStateListener(mAuthListener);
//
//        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=
//                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
//                        Blog.class,R.layout.blog_row,BlogViewHolder.class,databaseReference){
//                    @Override
//                    protected void populateViewHolder(BlogViewHolder viewHolder,Blog model,int position){
//                        viewHolder.setTitle(model.getTitle());
//                        viewHolder.setDesc(model.getDescription());
//                        viewHolder.setImage(getApplicationContext(),model.getImage());
//                        //viewHolder.setUsername(model.getUsername());
//                    }
//                };
//        mBlogList.setAdapter(firebaseRecyclerAdapter);
//    }
//    public static class BlogViewHolder extends RecyclerView.ViewHolder{
//
//        View mView;
//        public BlogViewHolder(View itemView) {
//            super(itemView);
//            mView=itemView;
//        }
//        public void setTitle(String title)
//        {
//            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
//            post_title.setText(title);
//        }
//        public void setDesc(String desc){
//
//            TextView post_desc=mView.findViewById(R.id.post_text);
//            post_desc.setText(desc);
//        }
//        public void setImage(final Context ctx, final String image){
//            final ImageView imageView=mView.findViewById(R.id.post_image);
//            Picasso.with(ctx).load(image).into(imageView);
////            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
////                @Override
////                public void onSuccess() {
////
////                }
////
////                @Override
////                public void onError() {
////                    Picasso.with(ctx).load(image).into(imageView);
////                }
////            });
//
//        }
////        public void setUsername(String username){
////            TextView post_username=mView.findViewById(R.id.post_username);
////            post_username.setText(username);
////
////        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId()==R.id.action_add)
//        {
//            startActivity(new Intent(MainActivity.this,PostActivity.class));
//        }
//
//        if(item.getItemId()==R.id.action_logout){
//            logout();
//        }
//        return super.onOptionsItemSelected(item);
//
//    }
//
//    private void logout() {
//        mAuth.signOut();
//    }
//    private void chek()
//    {
//        final String user_Id=mAuth.getCurrentUser().getUid();
//        dddd.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.hasChild(user_Id)){
//                    Intent mainIntent=new Intent(MainActivity.this,
//                            SetupActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(mainIntent);
//                }
////                else
////                {
////                    Toast.makeText(MainActivity.this,
////                            "You need to setup your account",Toast.LENGTH_LONG).show();
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//}
