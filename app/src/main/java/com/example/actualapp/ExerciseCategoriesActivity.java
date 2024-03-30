package com.example.actualapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ExerciseCategoriesActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_categories);

        String[] idArray = {
            "Abs", "Back", "Biceps", "Calf", "Chest", "Legs", "Forearms", "Legs", "Shoulders", "Triceps"
        };


        //Initialize each CardView and places them in an array
        CardView[] but = new CardView[9];
        for(int i=0;i<9;i++)
        {
            int id;
            id = getResources().getIdentifier("cardView"+idArray[i], "id", getPackageName());
            but[i] = (CardView) findViewById(id);
            int finalI = i;

            //Setting onClickListener with new Intent to change to their individual category page
            but[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent myActivity = new Intent(ExerciseCategoriesActivity.this, CategoryActivity.class);

                        //Sending the String of what category the user clicked to the next activity
                        myActivity.putExtra("categoryName", idArray[finalI]);

                        startActivity(myActivity);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                    } catch (Exception e) {
                        Log.d("Debug","Error: " +e);
                    }
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                    Toast.makeText(ExerciseCategoriesActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }
}
