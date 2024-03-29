package com.example.lively;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpPage extends AppCompatActivity {
    EditText fullNameInput, userNameInput, emailInput, passwordInput;
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
        fullNameInput = findViewById(R.id.fullNameInput);
        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUp = findViewById(R.id.signUp);
        logIn = findViewById(R.id.goToLogin);

        logIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, LogInScreen.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String emaiL, password, fullname, username;
            fullname = String.valueOf(fullNameInput.getText());
            username = String.valueOf(userNameInput.getText());
            emaiL = String.valueOf(emailInput.getText());
            password = String.valueOf(passwordInput.getText());

            if(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(emaiL) || TextUtils.isEmpty(password)){
                Toast.makeText(SignUpPage.this,"All fields are required.", Toast.LENGTH_SHORT).show();
                //return;
            }

            mAuth.createUserWithEmailAndPassword(emaiL, password)
                    .addOnCompleteListener(task -> {
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
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/lively-fe328.appspot.com/o/pomergranate.jpg?alt=media&token=97256b11-0593-4735-90df-52e6e33ee957");

                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent =  new Intent(SignUpPage.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });


                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}