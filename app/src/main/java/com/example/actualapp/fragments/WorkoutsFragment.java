package com.example.actualapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.actualapp.Firestore.ExerciseFirestore;
import com.example.actualapp.R;
import com.example.actualapp.activities.CategoryActivity;
import com.example.actualapp.databinding.FragmentWorkoutsBinding;

public class WorkoutsFragment extends Fragment {

    private FragmentWorkoutsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorkoutsViewModel workoutsViewModel =
                new ViewModelProvider(this).get(WorkoutsViewModel.class);

        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        String[] exerciseArray = ExerciseFirestore.getExerciseArray();

        CardView[] but = new CardView[9];
        for(int i=0;i<9;i++)
        {
            int id;
            id = root.getResources().getIdentifier("cardView"+exerciseArray[i], "id", requireActivity().getPackageName());
            but[i] = root.findViewById(id);
            int finalI = i;

            //Setting onClickListener with new Intent to change to their individual category page
            if (but[i] != null){
                but[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent myActivity = new Intent(getContext(), CategoryActivity.class);

                            //Sending the String of what category the user clicked to the next activity
                            myActivity.putExtra("category", exerciseArray[finalI]);
                            startActivity(myActivity);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                        } catch (Exception e) {
                            Log.d("Debug","Error: " +e);
                        }
                    }
                });
            }
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}