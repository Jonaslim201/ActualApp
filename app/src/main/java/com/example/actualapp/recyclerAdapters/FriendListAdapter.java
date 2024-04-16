package com.example.actualapp.recyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.userRelated.Friend;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<Friend> friendList;

    // Constructor
    public FriendListAdapter(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView idTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            idTextView = itemView.findViewById(R.id.userIdTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friendList.get(position);

        String userID = "User ID: " + friend.getId();
        holder.usernameTextView.setText(friend.getUsername());
        holder.idTextView.setText(userID);

        holder.usernameTextView.setTextColor(holder.usernameTextView.getResources().getColor(R.color.matteWhite));
        holder.idTextView.setTextColor(holder.idTextView.getResources().getColor(R.color.matteWhite));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}

