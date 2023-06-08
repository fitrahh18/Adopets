package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopets_fyp.databinding.ActivityPickPhotoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class pickPhoto extends AppCompatActivity {

    Button nexti;
    Button select;
    ImageView pickimage;
    ActivityPickPhotoBinding binding;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nexti=findViewById(R.id.next);
        pickimage=findViewById(R.id.pickimage);

        nexti.setOnClickListener(view -> {
            startActivity(new Intent(pickPhoto.this, PostFeed.class));
        });

        binding.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                selectImage();
            }
        });

       binding.uploadimagebtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               uploadImage();
           }
       });
    }

    private void uploadImage(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file...");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.pickimage.setImageURI(null);
                        Toast.makeText(pickPhoto.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(pickPhoto.this, "Upload fail", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void selectImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100 && data !=null && data.getData() != null)
        {
            imageUri=data.getData();
            binding.pickimage.setImageURI(imageUri);
        }
    }
}





