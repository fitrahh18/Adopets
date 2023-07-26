package com.example.adopets_fyp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editnames;
    private EditText editphones;
    FirebaseDatabase db;
    FirebaseUser user;
    private DatabaseReference reference;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        // Initialize the EditText views
        editnames = view.findViewById(R.id.editname);
        editphones = view.findViewById(R.id.editphone);

        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            reference = db.getReference().child("User").child("posts").child(userId).child("profile");
        }

        builder.setView(view)
                .setTitle("Update Details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel button clicked, do nothing
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Save button clicked, get the entered values
                        String name = editnames.getText().toString();
                        String phone = editphones.getText().toString();
                        // Create a HashMap to update the user's data
                        HashMap<String, Object> updateData = new HashMap<>();
                        updateData.put("username", name);
                        updateData.put("phone", phone);

                        // Update the user's data in the database
                        reference.updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Data updated successfully
                                    Toast.makeText(getContext(), "Details updated.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to update data
                                    Toast.makeText(getContext(), "Failed to update details.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        return builder.create();
    }
}



