package com.example.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private static final int GalleryReq = 1;
    private StorageReference mStorage;
    private EditText title;
    private EditText desc;
    private Uri imageUri = null;
    private Button SubButton;
    private ProgressDialog progressDialog;
    private Uri dow;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    //Intent intent;
    // Bundle bundle;
//    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageButton = findViewById(R.id.imagebutton);
        SubButton = findViewById(R.id.button2);
        title = findViewById(R.id.editText);
        desc = findViewById(R.id.editText2);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);
        progressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallerIntt = new Intent(Intent.ACTION_GET_CONTENT);
                gallerIntt.setType("image/*");
                //startActivityForResult(gallerIntt,GalleryReq);
                startActivityForResult(gallerIntt, GalleryReq);
            }
        });
//    }
        SubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startPosting();
            }
        });
    }

    private void startPosting(){
        progressDialog.setMessage("Posting to blog .......");

        final String tit=title.getText().toString();
        final String des=desc.getText().toString();
        if(!TextUtils.isEmpty(tit) && !TextUtils.isEmpty(des) && imageUri!=null){
            StorageReference filepath=mStorage.child("Blog_Images").child(UUID.randomUUID().toString());
            progressDialog.show();
            //Bundle extras = intent.getExtras();
           // Bundle extras=bundle;
           // Bitmap imageBitmap = (Bitmap) extras.get("data");
            //  imageView.setImageBitmap(imageBitmap);
//            sss ff=new sss();
//            Bitmap sfs=ff.get();
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //sfs.compress(Bitmap.CompressFormat.JPEG, 100, baos);
           // byte[] datas = baos.toByteArray();
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dow=uri;

                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    DatabaseReference databaseReference=mDatabase.push();
                                    databaseReference.child("title").setValue(tit);
                                    databaseReference.child("description").setValue(des);
                                    databaseReference.child("image").setValue(dow.toString());
                                    databaseReference.child("userId").setValue(mUser.getUid());
                                    databaseReference.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                startActivity(new Intent(PostActivity.this,MainActivity.class));
                                            }
                                            else
                                                {
                                                Toast.makeText(PostActivity.this,"ooopss",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });

                           // String downloadLink = uri.toString();
                           // Picasso.with(PostActivity.this).load(uri).fit().centerCrop().into(imageButton);
                        }
                    });

                }
            });

        }
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GalleryReq && resultCode == RESULT_OK) {
                 imageUri = data.getData();
                 //imageButton.setImageURI(imageUri);
                Picasso.with(PostActivity.this).load(imageUri).fit().centerCrop().into(imageButton);
//               // Intent intent = data;
//                //Bundle bundle = data.getExtras();
//                //Bitmap imageBitmap = (Bitmap) bundle.get("data");
////                sss sw = new sss();
////                sw.set(imageBitmap);
//
//               // imageButton.setImageURI(uri);
//                progressDialog.setMessage("Posting to blog .......");
//                progressDialog.show();
//                StorageReference filepath = mStorage.child("Blog_Images").child(UUID.randomUUID().toString());
//                  filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                      @Override
//                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                          progressDialog.dismiss();
//                          Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            // String downloadLink = uri.toString();
//                            Picasso.with(PostActivity.this).load(uri).fit().centerCrop().into(imageButton);
//                        }
//                    });
//                      }
//                  });
                //
// Bundle extras = data.getExtras();
//              // Bundle extras=bundle;
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] datas = baos.toByteArray();
//                filepath.putBytes(datas).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        progressDialog.dismiss();
////                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
////                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
////                        @Override
////                        public void onSuccess(Uri uri) {
////                            // String downloadLink = uri.toString();
////                            Picasso.with(PostActivity.this).load(uri).fit().centerCrop().into(imageButton);
////                        }
////                    });
//
//                    }
//                });
                //Picasso.with(PostActivity.this).load(imageUri).fit().centerCrop().into(imageButton);
            }
        }
////    public class sss{
//        Bitmap ss;
//       public void set(Bitmap sf){
//            this.ss=sf;
//        }
//        public Bitmap get(){
//            return ss;
//        }
//    }
    }

