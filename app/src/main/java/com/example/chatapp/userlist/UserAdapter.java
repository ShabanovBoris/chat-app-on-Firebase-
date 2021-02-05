package com.example.chatapp.userlist;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;


import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;
    private OnUserClickListener listener;
    private OnChangeAvatarListener onChangeAvatarListener;
    Context context;

    public UserAdapter(ArrayList<User> users,Context context ){
        this.users = users;
    }

    public interface OnChangeAvatarListener{
        void OnChange(int position); // TODO: 22.01.2021 dont need
    }

    public interface OnUserClickListener{
        void OnUserClick(int position);

    }

    public void setOnChangeAvatarListener(OnChangeAvatarListener onChangeAvatarListener) {
        this.onChangeAvatarListener = onChangeAvatarListener;
    }

    public void setOnUserClickListener(OnUserClickListener listener){
        this.listener = listener;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewAvatar;
        public TextView textViewName;


        public UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);

            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewName = itemView.findViewById(R.id.textViewUserName);

            imageViewAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onChangeAvatarListener != null){

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                        {
                            onChangeAvatarListener.OnChange(position);
                        }

                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                        {
                            listener.OnUserClick(position);
                        }
                    }
                }
            });
        }
    } {

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_item,parent,false);

        UserViewHolder viewHolder = new UserViewHolder(view,listener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User currentUser = users.get(position);


            holder.textViewName.setText(currentUser.getName());
            if(currentUser.isHasAvatar()){
                currentUser.setHasAvatar(true);




                Glide.with(holder.imageViewAvatar.getContext())
                        .load(currentUser.getAvatarUrl())
                        .into(holder.imageViewAvatar);
            }else {
            holder.imageViewAvatar.setImageResource(currentUser.getAvatarMockUpResourse());
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
