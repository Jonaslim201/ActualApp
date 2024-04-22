package com.example.actualapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.actualapp.Firestore.Firestore;
import com.example.actualapp.R;
import com.example.actualapp.databinding.FragmentProfileBinding;
import com.example.actualapp.userRelated.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText emailInput;
    EditText useridInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    public ProfileFragment() {}

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profilePic = root.findViewById(R.id.profile_image_view);
        usernameInput = root.findViewById(R.id.profile_username);
        emailInput = root.findViewById(R.id.profile_email);
        useridInput = root.findViewById(R.id.profile_userid);
        updateProfileBtn = root.findViewById(R.id.profile_update_btn);
        progressBar = root.findViewById(R.id.profile_progress_bar);
        logoutBtn = root.findViewById(R.id.logout_btn);

        final ImageView imageView = binding.profileImageView;
        profileModel.getImage().observe(getViewLifecycleOwner(), imageResource -> {
            if (imageResource instanceof Integer) {
                imageView.setImageResource((Integer) imageResource);
            } else {
            }
        });

        getUserdata();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener((v) -> {
            Firestore.logout();
        });

        return root;

    }

    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername. length()<3){
            usernameInput.setError("Username length must be at least 3");
            return;
        }
        User.setUsername(newUsername);
        setInProgress(true);
        updateToFirestore();
    }

    void updateToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(User.getId());

        // Update the username field
        userRef.update("username", User.getUsername())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful, hide progress and show success message if needed
                        setInProgress(false);
                        Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure, hide progress and show error message
                        setInProgress(false);
                        Toast.makeText(getContext(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    void getUserdata() {
        setInProgress(true);
        usernameInput.setText(User.getUsername());
        emailInput.setText(User.getEmail());
        useridInput.setText(User.getId());
        setInProgress(false);
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}