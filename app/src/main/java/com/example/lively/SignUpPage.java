package com.example.lively;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpPage extends AppCompatActivity {
    TextInputEditText fullName, userName, email, pswrd;
    Button signUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView logIn;
    DatabaseReference reference;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        fullName = findViewById(R.id.fullName);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        pswrd = findViewById(R.id.password);
        signUp = findViewById(R.id.signUp);
        logIn = findViewById(R.id.goToLogin);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, LogInScreen.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String emaiL, password, fullname, username;
                fullname = String.valueOf(fullName.getText());
                username = String.valueOf(userName.getText());
                emaiL = String.valueOf(email.getText());
                password = String.valueOf(pswrd.getText());

                if(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(emaiL) || TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpPage.this,"All fields are required.", Toast.LENGTH_SHORT).show();
                    //return;
                }

                mAuth.createUserWithEmailAndPassword(emaiL, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpPage.this, "Successfully created account.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    assert firebaseUser != null;
                                    String userid = firebaseUser.getUid();

                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("id", userid);
                                    hashMap.put("username", username.toLowerCase());
                                    hashMap.put("fullname", fullname);
                                    hashMap.put("bio", "");
                                    hashMap.put("imageurl", "gs://lively-fe328.appspot.com/pomergranate.jpg");

                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>(){
                                        public void onComplete(@NonNull Task<Void> task){
                                            if(task.isSuccessful()){
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent =  new Intent(SignUpPage.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });


                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpPage.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}