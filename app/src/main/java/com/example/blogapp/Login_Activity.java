package com.example.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Activity extends AppCompatActivity {

    private EditText email,password;
    private Button logIn,needAnew;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabasref;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        email=findViewById(R.id.logemail);
        password=findViewById(R.id.logpass);
        logIn=findViewById(R.id.login);
        needAnew=findViewById(R.id.newAcc);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        mDatabasref= FirebaseDatabase.getInstance().getReference().child("Users");
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkLog();
            }
        });

        needAnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login_Activity.this,Register_act.class);
                startActivity(intent);
            }
        });
    }
    public void checkLog()
    {
        String emailf=email.getText().toString().trim();
        String passf=password.getText().toString().trim();
        if(!TextUtils.isEmpty(emailf) && !TextUtils.isEmpty(passf))
        {
            progressDialog.setMessage("checking loging ...");
            progressDialog.show();
          mAuth.signInWithEmailAndPassword(emailf,passf).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task)
              {
                  if(task.isSuccessful())
                  {
                      progressDialog.dismiss();
                       checkUserExist();
                  }
                  else
                      {
                          progressDialog.dismiss();
                      Toast.makeText(Login_Activity.this,
                              "Error Login",Toast.LENGTH_LONG).show();
                  }
              }
          });
        }
    }

    private void checkUserExist()
    {
        final String user_Id=mAuth.getCurrentUser().getUid();
        mDatabasref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_Id)){
                    Intent mainIntent=new Intent(Login_Activity.this,
                            MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
                else
                    {
                        Intent mainIntent=new Intent(Login_Activity.this,
                               SetupActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
//                    Toast.makeText(Login_Activity.this,
//                            "You need to setup your account",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
