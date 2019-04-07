package com.example.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText disName;
    private ImageView ima;
    private ImageButton disImage;
    private Button finsButton;
    private static final int GALLERY_REQUEST=11;
    private Uri uri;
    private DatabaseReference mdatabase;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        disImage=findViewById(R.id.imageButton1);
        disName=findViewById(R.id.editText3);
        finsButton=findViewById(R.id.finishSetup);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference().child("Profile_Images");
        mProgressDialog=new ProgressDialog(this);
        //ima=findViewById(R.id.imageve);
        disImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });
        finsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();

            }
        });

    }

    private void startSetupAccount() {
        final String name=disName.getText().toString();
        final String userId=firebaseAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && uri != null)
        {
         //
            mProgressDialog.setMessage("Finishing Setup...");
            mProgressDialog.show();
            StorageReference filepath=mStorage.child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdatabase.child(userId).child("name").setValue(name);
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mProgressDialog.dismiss();
                            //Toast.makeText(SetupActivity.this,"added",Toast.LENGTH_LONG).show();
                            mdatabase.child(userId).child("image").setValue(uri.toString());
                            Intent mainI=new Intent(SetupActivity.this,MainActivity.class);
                            mainI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainI);
                        }
                    });

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK && data!=null)
        {
//            Uri uri=data.getData();
//            if (uri==null)
//            {
//                Toast.makeText(SetupActivity.this,"ooopss",Toast.LENGTH_LONG).show();
         //   }
            uri=data.getData();
          Picasso.with(SetupActivity.this).load(uri).fit().centerCrop().into(disImage);
           // Bitmap photo = (Bitmap) data.getExtras().get("data");
           // disImage.setImageBitmap(photo);
//            Bundle bundle=data.getExtras();
//
//            Bitmap bitmaps=(Bitmap)bundle.get("data");
//             ima.setImageBitmap(bitmaps);


            //ima.setImageURI(uri);

//            Uri imageUri=data.getData();
//            CropImage.activity(imageUri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);
//            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//            {
//            CropImage.ActivityResult result=CropImage.getActivityResult(data);
//            if(resultCode==RESULT_OK){
//                mImageUri=result.getUri();
//                disImage.setImageURI(mImageUri);
//
//            }
//            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                Toast.makeText(SetupActivity.this,"ooops",Toast.LENGTH_LONG).show();
//            }

            }

        }
    }

