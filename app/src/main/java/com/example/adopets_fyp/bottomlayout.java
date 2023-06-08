package com.example.adopets_fyp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class bottomlayout extends AppCompatActivity {

    String[] itemA = {"Cat", "Dog", "Hamster", "Others"};

    String[] itemB = {"Available" , "Not available"};
    String[] itemC = {"1-6 Months" , "7-12 Months", "1-3 Years", "4-6 Years", "7-9 Years", "10 Years Above"};
    String[] itemD = {"All" , "Kuala Lumpur", "Selangor", "Johor", "Kedah", "Terengganu","Kelantan","Pulau Pinang", "Pahang","Perlis","Perak","Negeri Sembilan","Sabah","Sarawak"};
    AutoCompleteTextView autoCompleteTextViewA;
    AutoCompleteTextView autoCompleteTextViewB;
    AutoCompleteTextView autoCompleteTextViewC;
    AutoCompleteTextView autoCompleteTextViewD;
    ArrayAdapter<String> adapterItemsA;

    ArrayAdapter<String> adapterItemsB;
    ArrayAdapter<String> adapterItemsC;
    ArrayAdapter<String> adapterItemsD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomlayout);

        autoCompleteTextViewA = findViewById(R.id.speciesa);
        autoCompleteTextViewB = findViewById(R.id.gendera);
        autoCompleteTextViewC = findViewById(R.id.agea);
        autoCompleteTextViewB = findViewById(R.id.locationa);

        adapterItemsA = new ArrayAdapter<String>(this, R.layout.itemlist, itemA);
        adapterItemsB = new ArrayAdapter<String>(this, R.layout.itemlist, itemB);
        adapterItemsC = new ArrayAdapter<String>(this, R.layout.itemlist, itemC);
        adapterItemsD = new ArrayAdapter<String>(this, R.layout.itemlist, itemD);

        autoCompleteTextViewA.setAdapter(adapterItemsA);

        autoCompleteTextViewA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemA = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(bottomlayout.this, "Item" + itemA, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextViewB.setAdapter(adapterItemsB);
        autoCompleteTextViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemB = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(bottomlayout.this, "Item" + itemB, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTextViewC.setAdapter(adapterItemsC);
        autoCompleteTextViewC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemC = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(bottomlayout.this, "Item" + itemC, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTextViewD.setAdapter(adapterItemsD);
        autoCompleteTextViewD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemD = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(bottomlayout.this, "Item" + itemD, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
