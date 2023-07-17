package com.example.adopets_fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.adopets_fyp.R;
import com.example.adopets_fyp.SignIn;
import com.example.adopets_fyp.SignUp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button signinbt;
    Button signupbt;
    FirebaseAuth mAuth;
    Button view;
    private MeowBottomNavigation bottomNavigation;
    RelativeLayout nearby, home, profile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signinbt = findViewById(R.id.signinbt);
        signupbt = findViewById(R.id.signupbt);
        mAuth = FirebaseAuth.getInstance();
        bottomNavigation=findViewById(R.id.bottomNavigation);
        nearby=findViewById(R.id.nearbyy);
        home=findViewById(R.id.homee);
        profile=findViewById(R.id.profilee);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        signinbt.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SignIn.class));
        });

        signupbt.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        });



        bottomNavigation.show(2,true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.baseline_near_me_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.baseline_person_24));

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 1:
                        nearby.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        break;

                    case 2:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        break;

                    case 3:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                switch (model.getId()){

                    case 1:
                        nearby.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                switch (model.getId()){

                    case 2:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                switch (model.getId()){

                    case 3:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this,SignIn.class));
        }
    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });
    }

}