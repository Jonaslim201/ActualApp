package com.example.actualapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private ArrayList<Exercise> exercises;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the category name the user clicked from the Intent declared in ExerciseCategoriesActivity class
        Intent intent = getIntent();
        String category = intent.getStringExtra("categoryName");

        //Access Firestore to retrieve the list of exercises for the category chosen
        Firestore.getExercises(CategoryActivity.this, category, new ExerciseCallBack(){
            @Override
            public void onSuccessResult(ArrayList<Exercise> exercise) {
                setExercises(exercise);

                //Once all exercises are retrieved, start activity
                startActivity();
            }
        });

    }

    //Sets content view and initializes XML elements
    private void startActivity() {
        setContentView(R.layout.category_activity);
        Log.d("Document", exercises.toString());


        //RecyclerView bs
        RecyclerView view = findViewById(R.id.recyclerView);
        ExerciseRecyclerAdapter adapter = new ExerciseRecyclerAdapter(exercises, CategoryActivity.this);

        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter);


        //Initializing back and profile buttons
        ImageButton backButton = findViewById(R.id.arrowButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        //Setting onClick for backButton to return the list of categories
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setBackground(getDrawable(R.drawable.circular_button));
                startActivity(new Intent(CategoryActivity.this, ExerciseCategoriesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });


        //Setting onClick for Profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileButton.setBackground(getDrawable(R.drawable.circular_button));
            }
        });
    }

    //Setter method for the list of exercises
    public void setExercises(ArrayList<Exercise> exercises){
        this.exercises = exercises;
    }
}
