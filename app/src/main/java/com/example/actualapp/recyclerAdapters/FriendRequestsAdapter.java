package com.example.actualapp.recyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.R;
import com.example.actualapp.userRelated.Friend;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private ArrayList<Friend> friendRequests;
    private OnAcceptButtonClickListener acceptButtonClickListener;
    private OnRejectButtonClickListener rejectButtonClickListener;

    // Constructor
    public FriendRequestsAdapter(ArrayList<Friend> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        Button acceptButton;
        Button rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            acceptButton = itemView.findViewById(R.id.accept_btn);
            rejectButton = itemView.findViewById(R.id.reject_btn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_friend_requests, parent, false); // Use your list item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friendRequests.get(position);
        holder.usernameTextView.setText(friend.getUsername());

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptButtonClickListener != null) {
                    acceptButtonClickListener.onAcceptButtonClick(friend);
                }
            }
        });
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rejectButtonClickListener != null) {
                    rejectButtonClickListener.onRejectButtonClick(friend);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public interface OnAcceptButtonClickListener {
        void onAcceptButtonClick(Friend friend);
    }

    public interface OnRejectButtonClickListener {
        void onRejectButtonClick(Friend friend);
    }

    public void setOnAcceptButtonClickListener(OnAcceptButtonClickListener listener) {
        this.acceptButtonClickListener = listener;
    }

    public void setOnRejectButtonClickListener(OnRejectButtonClickListener listener) {
        this.rejectButtonClickListener = listener;
    }
}

