package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    Button loginbtn;
    TextView tosignup;
    EditText username;
    EditText passwordi;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tosignup = findViewById(R.id.tosignup);
        mAuth = FirebaseAuth.getInstance();
        loginbtn = findViewById(R.id.loginbtn);
        username = findViewById(R.id.username);
        passwordi = findViewById(R.id.passwordi);

        loginbtn.setOnClickListener(view -> {
            loginUser();
        });

        tosignup.setOnClickListener(view -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
        });


    }

    private void loginUser(){
        String email = username.getText().toString();
        String password = passwordi.getText().toString();

        if (TextUtils.isEmpty(email)){
            username.setError("Email cannot be empty");
            username.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            passwordi.setError("Password cannot be empty");
            passwordi.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignIn.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignIn.this, SecondMain.class));
                    }else{
                        Toast.makeText(SignIn.this, "Log in error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(SignIn.this,SecondMain.class);
        startActivity(intent);
    }
    }
