package com.example.actualapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.Firestore.FirestoreListener;
import com.example.actualapp.Firestore.FriendFirestore;
import com.example.actualapp.Firestore.FriendListChangeListener;
import com.example.actualapp.Firestore.FriendRequestChangeListener;
import com.example.actualapp.R;
import com.example.actualapp.databinding.FragmentFriendsBinding;
import com.example.actualapp.recyclerAdapters.FriendListAdapter;
import com.example.actualapp.recyclerAdapters.FriendRequestsAdapter;
import com.example.actualapp.userRelated.Friend;
import com.example.actualapp.userRelated.User;
import com.example.actualapp.userRelated.UserFriends;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FriendsFragment extends Fragment implements FriendRequestsAdapter.OnAcceptButtonClickListener, FriendRequestsAdapter.OnRejectButtonClickListener,
        FriendRequestChangeListener, FriendListChangeListener {

    private ArrayList<Friend> friendList = new ArrayList<>();
    private FriendListAdapter friendListAdapter;
    private ArrayList<Friend> friendRequests = new ArrayList<>();
    private FriendRequestsAdapter friendRequestsAdapter;
    private RecyclerView recyclerViewFriendList;
    private FragmentFriendsBinding binding;

    private TextInputEditText id;
    private Button addFriend;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FriendList", "onCreateView");
        FriendsViewModel friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewFriendList = root.findViewById(R.id.recyclerViewFriendList);
        friendListAdapter = new FriendListAdapter(friendList);
        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewFriendList.setAdapter(friendListAdapter);

        populateFriendList();

        //show friend requests dialog
        handleFriendRequests();

        addFriend = root.findViewById(R.id.addFriendButton);
        id = root.findViewById(R.id.friendId);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendFirestore.sendFriendRequest(Objects.requireNonNull(id.getText()).toString(), new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            id.setText("");
                        } else {
                            Toast.makeText(getContext(), "Id does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        setTextWatchers();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirestoreListener.setFriendRequestListener(this);
        FirestoreListener.setFriendListListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirestoreListener.unregisterFriendListListener(this);
        FirestoreListener.unregisterFriendRequestListener(this);
    }

    private void populateFriendList() {
        friendList.clear();

        friendList = UserFriends.getFriend();
        friendListAdapter.setFriendList(friendList);

        if (friendList.isEmpty()){
            recyclerViewFriendList.setVisibility(View.GONE);
        } else {
            Log.d("FriendList", friendList.toString());
            recyclerViewFriendList.setVisibility(View.VISIBLE);
            Collections.sort(friendList);
            friendListAdapter.notifyItemRangeChanged(0, friendList.size());
        }
    }

    private void handleFriendRequests() {
        //populate friend requests (to load from database instead)
        friendRequests = UserFriends.getReceivedFriendRequestsList();

        if (!friendRequests.isEmpty()){
            showFriendRequestsDialog(friendRequests);
        }
    }

    @Override
    public void onAcceptButtonClick(Friend friend) {
        friendList.add(friend);
        friendRequests.remove(friend);
        FriendFirestore.acceptFriendRequest(UserFriends.getReceivedFriendRequests().get(friend.getId()), new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success){
                    if (friendListAdapter != null) {
                        friendListAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add.", Toast.LENGTH_SHORT).show();
                }
            }
        }, friend.getId());
    }

    @Override
    public void onRejectButtonClick(Friend friend) {
        friendRequests.remove(friend);

        FriendFirestore.removeFriendRequest(UserFriends.getReceivedFriendRequests().get(friend.getId()), new FirestoreCallBack() {
            @Override
            public void onFirestoreResult(boolean success) {
                if (success){
                    if (friendRequestsAdapter != null) {
                        friendRequestsAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showFriendRequestsDialog(ArrayList<Friend> friendRequests) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_friend_requests);

        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        titleTextView.setText("Friend Requests");

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewFriendRequests);

        //Adapter for RecyclerView to display friend requests
        friendRequestsAdapter = new FriendRequestsAdapter(friendRequests);
        friendRequestsAdapter.setOnAcceptButtonClickListener(this);
        friendRequestsAdapter.setOnRejectButtonClickListener(this); //Set the reject button click listener
        recyclerView.setAdapter(friendRequestsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Close button
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setTextWatchers(){
        final TextWatcher commonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String idText = Objects.requireNonNull(id.getText()).toString().trim();

                boolean isValid = idText.length() > 0;
                boolean addingThemselves = idText.equals(User.getId());
                boolean alreadyAdded = UserFriends.getFriendDocuments().containsKey(idText);
                boolean alreadyRequested = UserFriends.getSentFriendRequests().containsKey(idText);

                if (addingThemselves || alreadyAdded || alreadyRequested) {
                    // Username and password are the same, disable the register button
                    addFriend.setEnabled(false);
                    // Show a message
                    Toast.makeText(getContext(), "Adding yourself or you have already added", Toast.LENGTH_SHORT).show();
                } else {
                    addFriend.setEnabled(isValid);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        id.addTextChangedListener(commonTextWatcher);
    }

    @Override
    public void onFriendRequestChanged() {
        Log.d("FriendRequests", "Friend request changed");
    }

    @Override
    public void onFriendListChange() {
        Log.d("FriendList", "Friend list changed");
    }
}
