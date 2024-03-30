package com.example.actualapp;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ExerciseRecyclerAdapter extends RecyclerView.Adapter<ExerciseRecyclerAdapter.ViewHolder>{

    int N;
    private ArrayList<Exercise> exercises;
    private Context activity;

    public ExerciseRecyclerAdapter(ArrayList<Exercise> exercises, Context activity){
        this.N = exercises.size();
        this.exercises = exercises;
        this.activity = activity;
    }

    public void setExercises(ArrayList<Exercise> exercises){
        this.exercises = exercises;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_exercise_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        Exercise currExercise = exercises.get(position);

        holder.exerciseName.setText(currExercise.getName());
        holder.exercisePriMuscleGroup.setText(currExercise.getPriMuscleGroups());

        holder.itemView.startAnimation(animation);
        holder.exerciseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(activity, ExerciseActivity.class);
                newActivity.putExtra("exerciseName", currExercise.getName());
                Toast.makeText(holder.itemView.getContext(), "button clicked.", Toast.LENGTH_SHORT).show();
                activity.startActivity(newActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView exerciseName;
        TextView exercisePriMuscleGroup;
        CardView exerciseCardView;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            exerciseName = itemView.findViewById(R.id.exerciseName);
            exercisePriMuscleGroup = itemView.findViewById(R.id.exercisePriMuscleGroups);
            exerciseCardView = itemView.findViewById(R.id.exerciseCardView);
        }
    }

}
