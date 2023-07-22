package com.example.adopets_fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        Message message = messages.get(position);

        ImageView userImageView = convertView.findViewById(R.id.userImageView);
        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        Picasso.get().load(message.getImageUrl()).into(userImageView);
        usernameTextView.setText(message.getUsername());
        messageTextView.setText(message.getText());

        return convertView;
    }
}

