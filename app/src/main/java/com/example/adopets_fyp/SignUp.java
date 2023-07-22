package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Button signupbtn;
    TextView loginin;
    EditText userEmail;
    EditText phonen;
    EditText passwordu;

    EditText userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userEmail = findViewById(R.id.userEmail);
        userName = findViewById(R.id.userName);
        phonen = findViewById(R.id.phone);
        passwordu = findViewById(R.id.passwordu);
        loginin = findViewById(R.id.loginin);
        signupbtn = findViewById(R.id.signupbtn);
        mAuth = FirebaseAuth.getInstance();


        signupbtn.setOnClickListener(view -> {
            createUser();
        });

        loginin.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, SignIn.class));
        });
    }

    private void createUser() {
        String email = userEmail.getText().toString();
        String password = passwordu.getText().toString();
        String username = userName.getText().toString();
        String phone = phonen.getText().toString();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Email cannot be empty");
            userEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordu.setError("Password cannot be empty");
            passwordu.requestFocus();
        } else if (TextUtils.isEmpty(username)) {
            userName.setError("Username cannot be empty");
            userName.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            phonen.setError("Phone number cannot be empty");
            phonen.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid != null) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = mDatabase.child("User").child("posts").child(mAuth.getCurrentUser().getUid()).child("profile");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("useremail", email);
                            hashMap.put("username", username);
                            hashMap.put("phone", phone); // Add the phone number to the hashMap

                            databaseReference.setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Data successfully stored
                                            Toast.makeText(SignUp.this, "Data stored", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors
                                        }
                                    });
                        }
                        Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                    } else {
                        Toast.makeText(SignUp.this, "Registration error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
