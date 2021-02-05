package com.example.chatapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;


public class ChatCustomAdapter extends ArrayAdapter<ChatMessage> {

    private List<ChatMessage> chatMessagesList;
    private Activity activity;

    public ChatCustomAdapter(Activity context, int resource, List<ChatMessage> chatMessageList) {
        super(context, resource);

        this.chatMessagesList = chatMessageList;
        this.activity = context;
    }



    @Override
    public int getViewTypeCount() {
        return 2;

    }

    private class ViewHolder {
        private TextView bubbleText;
        private ImageView imageViewInBubble;

        public ViewHolder(View view) {
            imageViewInBubble = view.findViewById(R.id.imageViewInBubble);
            bubbleText = view.findViewById(R.id.bubbleText);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        ChatMessage chatMessage = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if(viewType == 0){
            layoutResource = R.layout.mine_message_chat_item;
        }else{
            layoutResource = R.layout.your_message_chat_item;
        }

        if(convertView != null)
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }else{
            convertView = layoutInflater.inflate(
                    layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        boolean isText = chatMessage.getImageURL() == null;

        if(isText){
            viewHolder.bubbleText.setVisibility(View.VISIBLE);
            viewHolder.imageViewInBubble.setVisibility(View.GONE);
            viewHolder.bubbleText.setText(chatMessage.getText());
        } else
        {
            viewHolder.bubbleText.setVisibility(View.GONE);
            viewHolder.imageViewInBubble.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.imageViewInBubble.getContext())
                    .load(chatMessage.getImageURL())
                    .into(viewHolder.imageViewInBubble);
        }

        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        int flag;

        ChatMessage chatMessage = chatMessagesList.get(position);
        if(chatMessage.isMine())
        {
            flag = 0;
        }else{
            flag =1;
        }
        return  flag;
    }
}
