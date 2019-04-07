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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register_act extends AppCompatActivity {

    private EditText name;
    private EditText email,password;
    private Button signup;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference mdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_act);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signup=findViewById(R.id.signup);
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String names=name.getText().toString();
        String emails=email.getText().toString();
        String passwords=password.getText().toString();
        if(!TextUtils.isEmpty(names) && !TextUtils.isEmpty(emails) && !TextUtils.isEmpty(passwords)
                ){
//&& Pattern.matches("\\z@iitbhilai.ac.in",emails)
            progressDialog.setMessage("Signing up ...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(emails,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){


                        String user_ID=mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db=mdatabase.child(user_ID);
                        current_user_db.child("name").setValue(names);
                        current_user_db.child("image").setValue("default");
                        progressDialog.dismiss();

                        Intent mainIntent=new Intent(Register_act.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(Register_act.this,
                                "wrong credential",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
