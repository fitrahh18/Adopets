package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class SecondMain extends AppCompatActivity {

    Button signOutBtn, mylocate;
    GoogleSignInClient gsc;
    private MeowBottomNavigation bottomNavigation;
    RelativeLayout nearby, home, profile;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        signOutBtn = findViewById(R.id.signout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        nearby = findViewById(R.id.nearbyy);
        home = findViewById(R.id.homee);
        profile = findViewById(R.id.profile);
        mylocate = findViewById(R.id.mylocation);


        signOutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(SecondMain.this, SignIn.class));
        });

        mylocate.setOnClickListener(view -> {
            startActivity(new Intent(SecondMain.this, MapsActivity.class));
        });


        bottomNavigation.show(2, true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_near_me_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_person_24));

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()) {

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
                switch (model.getId()) {

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
                switch (model.getId()) {

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
                switch (model.getId()) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                startActivity(new Intent(SecondMain.this, pickPhoto.class));
                break;
            case R.id.search:
                showDialog();
                break;

            default:
                ;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        /*final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomlayout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Post");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottomlayout, null);
        builder.setView(dialogView);

        //Spinner speacies
        AutoCompleteTextView spinner1 = dialogView.findViewById(R.id.speciesa);

        String[] items1 = getResources().getStringArray(R.array.spinner1_items);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items1);
        spinner1.setAdapter(adapter1);

        //Spinner gender
        AutoCompleteTextView spinner2 = dialogView.findViewById(R.id.gendera);

        String[] items2 = getResources().getStringArray(R.array.spinner2_items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items2);
        spinner2.setAdapter(adapter2);

        //Spinner age
        AutoCompleteTextView spinner3 = dialogView.findViewById(R.id.agea);

        String[] items3 = getResources().getStringArray(R.array.spinner3_items);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items3);
        spinner3.setAdapter(adapter3);

        //Spinner location
        AutoCompleteTextView spinner4 = dialogView.findViewById(R.id.locationa);

        String[] items4 = getResources().getStringArray(R.array.spinner4_items);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items4);
        spinner4.setAdapter(adapter4);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedValueSpeacies = spinner1.getText().toString();
                String selectedValueGender = spinner2.getText().toString();
                String selectedValueAge = spinner3.getText().toString();
                String selectedValueLocation = spinner4.getText().toString();

                //searching

                // Do something with the selected value
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP;
            window.setAttributes(layoutParams);
        }
/*// Apply slide animation to the dialog //animation x jadi hoho
        Animation slideAnimation = new TranslateAnimation(0, 0, dialogView.getHeight(), 100);
        slideAnimation.setDuration(400);
        dialogView.startAnimation(slideAnimation);*/

        dialog.show();


    }


    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(SecondMain.this, SignIn.class));
            }
        });
    }
}