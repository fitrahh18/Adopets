package com.example.adopets_fyp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class pickPhoto extends AppCompatActivity {
    private EditText petname, petage, petbreeds;
    private Button submits;
    String[] item2 = {"Cat", "Dog", "Hamster", "Tortoise", "Rabbit", "Bird", "Guinea Pigs", "Hedgehog", "Lizard", "Ferret","Fish","Iguana", "Other"};

    String[] item3 = {"Available" , "Not available"};
    String[] item = {"Male" , "Female"};
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView1;
    AutoCompleteTextView autoCompleteTextView2;
    ArrayAdapter<String> adapterItems;

    ArrayAdapter<String> adapterItems1;
    ArrayAdapter<String> adapterItems2;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);

        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid= FirebaseAuth.getInstance().getUid();

        databaseReference = firebaseDatabase.getReference().child("Users");

        petname = findViewById(R.id.petname);
        petage = findViewById(R.id.petage);
        petbreeds = findViewById(R.id.petbreeds);
        autoCompleteTextView = findViewById(R.id.gender);
        autoCompleteTextView1 = findViewById(R.id.speciess);
        autoCompleteTextView2 = findViewById(R.id.status);

        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        adapterItems1 = new ArrayAdapter<String>(this, R.layout.list_item, item2);
        adapterItems2 = new ArrayAdapter<String>(this, R.layout.list_item, item3);
        submits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getname = petname.getText().toString();
                String getage = petage.getText().toString();
                String getbreed = petbreeds.getText().toString();
                System.out.println("test 1");
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("petname", getname);
                hashMap.put("petage", getage);
                hashMap.put("petbreeds", getbreed);

                assert uid != null;
                String postuid = uid+getname;

                databaseReference.child(uid)
                        .child("post")
                        .child(postuid)
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(pickPhoto.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                System.out.println("test 1");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(pickPhoto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                System.out.println("test 2");
                            }
                        });

                //upload image to database

            }
        });
        System.out.println("test 3");

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(pickPhoto.this, "Item" + item, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView1.setAdapter(adapterItems1);
        autoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item2 = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(pickPhoto.this, "Item" + item2, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView2.setAdapter(adapterItems2);
        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item3 = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(pickPhoto.this, "Item" + item3, Toast.LENGTH_SHORT).show();
            }
        });
    }

}