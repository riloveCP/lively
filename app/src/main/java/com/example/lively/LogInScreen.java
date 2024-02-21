package com.example.lively;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInScreen extends AppCompatActivity {
    TextView textView;
    Button LogIN_btn;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "name";

    @Override
    public void onStart() {
        super.onStart();
        Log.d(" Activity LifeCycle", "Activity started");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        LogIN_btn = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String name = sharedPreferences.getString(KEY_NAME,null);

        if (name != null){
            Intent intent = new Intent(LogInScreen.this, MainActivity.class);
            startActivity(intent);
        }

        LogIN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(usernameInput.getText());
                password = String.valueOf(passwordInput.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LogInScreen.this,"Enter email!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LogInScreen.this,"Enter password!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"LogIn successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LogInScreen.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(KEY_NAME, usernameInput.getText().toString());
//                editor.apply();
//                Intent intent = new Intent(LogInScreen.this, MainActivity.class);
//                startActivity(intent);
//                Toast.makeText(LogInScreen.this,"LogIn Successfully", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });

        textView = findViewById(R.id.createNew);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogInScreen.this, SignUpPage.class);
                startActivity(intent );
                Toast.makeText(LogInScreen.this,"Sign Up Page", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        Log.d(" Activity LifeCycle", "Activity created");
    }


    protected void onResume(){
        super.onResume();
        Log.d(" Activity LifeCycle", "Activity resumed");
    }
    protected void onPause(){
        super.onPause();
        Log.d(" Activity LifeCycle", "Activity paused");
    }
    protected void onStop(){
        super.onStop();
        Log.d(" Activity LifeCycle", "Activity Stopped");
    }
    protected void onDestroy(){
        super.onDestroy();
        Log.d(" Activity LifeCycle", "Activity Destroyed");
    }
}