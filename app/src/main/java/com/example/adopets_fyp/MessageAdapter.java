package com.example.adopets_fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messageList;
    private String senderUid; // Add this field to store the sender UID

    public MessageAdapter(Context context, List<Message> messageList, String senderUid) {
        super(context, 0, messageList);
        this.context = context;
        this.messageList = messageList;
        this.senderUid = senderUid;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // We have two types of views: sender and receiver
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // Return 0 for sender view, 1 for receiver view
        return message.getSenderUid().equals(senderUid) ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (viewType == 0) {
                convertView = inflater.inflate(R.layout.item_message_sender, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_message_receiver, parent, false);
            }
        }



        Message message = messageList.get(position);

        TextView tvMessageText = convertView.findViewById(R.id.tvMessageText);
        TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);

        tvMessageText.setText(message.getMessageText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
        tvTimestamp.setText(formattedDate);

        return convertView;
    }
}


