package com.example.actualapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.R;
import com.example.actualapp.databinding.FragmentHomeBinding;
import com.example.actualapp.exerciseRelated.FriendWorkout;
import com.example.actualapp.recyclerAdapters.FeedAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static ArrayList<FriendWorkout> friendWorkouts = new ArrayList<>();
    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        feedRecyclerView = root.findViewById(R.id.recyclerViewFeed);
        feedAdapter = new FeedAdapter(friendWorkouts, requireContext());
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        feedRecyclerView.setAdapter(feedAdapter);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        if (friendWorkouts.size() == 0){
            feedRecyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            feedRecyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }

        return root;
    }

    public static void setFriendWorkouts(ArrayList<Object> feedWorkouts, FirestoreCallBack callBack) {
        Log.d("FriendWorkouts", "Setting friend workouts");
        CountDownLatch latch = new CountDownLatch(feedWorkouts.size());
        for (Object o: feedWorkouts){
            if (o instanceof Map){
                Map<String, Object> map = (Map<String, Object>) o;
                FriendWorkout friendWorkout = new FriendWorkout(map);
                friendWorkouts.add(friendWorkout);
            }

            latch.countDown();

            if (latch.getCount() == 0){
                callBack.onFirestoreResult(true);
            }
        }
    }

    public static ArrayList<FriendWorkout> getFriendWorkouts() {
        return friendWorkouts;
    }

    public static void initializeFriendWorkouts(){
        friendWorkouts = new ArrayList<>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}