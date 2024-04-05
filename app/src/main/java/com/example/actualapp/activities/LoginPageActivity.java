package com.example.actualapp.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.actualapp.Firestore.FirestoreCallBack;
import com.example.actualapp.Firestore.LoginFirestore;
import com.example.actualapp.LoadingButton;
import com.example.actualapp.R;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


//Login Page
public class LoginPageActivity extends AppCompatActivity{

    private CircularProgressButton loginButton;
    private TextInputEditText username;
    private TextInputEditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_activity);

        initializeButton();
        initializeTextWatchers();
    }

    //Initializes the login button
    private void initializeButton(){
        loginButton = findViewById(R.id.loginButton);
        final LoadingButton animationButton = new LoadingButton(loginButton, LoginPageActivity.this, username, password);

        //Disabling button before the user inputs
        loginButton.setEnabled(false);

        //Setting click listener
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Collects the username and password field and puts them in a HashMap
                Map<String, Object> user = new HashMap<>();
                user.put("username", Objects.requireNonNull(username.getText()).toString());
                user.put("password", Objects.requireNonNull(password.getText()).toString());

                //Button calls the morphDoneAndRevert method in the LoadingButton Class to check user credentials
                animationButton.morphDoneAndRevert();

                //Accesses Firestore class to login user
                LoginFirestore.loginUser(user, LoginPageActivity.this, new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success){
                            //If user inputs the correct credentials, enters the app.
                            startApp();
                        } else {
                            //If user inputs the wrong credentials cuz hes a dumbass then it reverts the button animation
                            Toast.makeText(LoginPageActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show();
                        }
                        animationButton.revert(3000L);
                    }
                });
            }
        });
    }

    //Starts the app
    public void startApp(){
        startActivity(new Intent(this, ExerciseCategoriesActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }


    //Initializes text watchers
    //Only enables the Login button when both fields have inputs
    private void initializeTextWatchers(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        final TextWatcher commonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                loginButton.setEnabled(username.getText().toString().trim().length() > 0
                        && password.getText().toString().trim().length() > 0 );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        username.addTextChangedListener(commonTextWatcher);
        password.addTextChangedListener(commonTextWatcher);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Animate the transition to teal color
                    animateToTeal(v);
                } else {
                    // Animate the transition back to the original color
                    animateToOriginalColor(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Animate the transition to teal color
                    animateToTeal(v);
                } else {
                    // Animate the transition back to the original color
                    animateToOriginalColor(v);
                }
            }
        });
    }

    //Animation for the text fields below
    private void animateToTeal(View view) {
        // Animate the background color to teal
        ObjectAnimator animator = ObjectAnimator.ofObject(view.getBackground(), "tint", new ArgbEvaluator(), Color.parseColor("#00000000"), Color.parseColor("#38304c"));
        animator.setDuration(300);
        animator.start();
    }

    private void animateToOriginalColor(View view) {
        // Animate the background color back to the original color
        ObjectAnimator animator = ObjectAnimator.ofObject(view.getBackground(), "tint", new ArgbEvaluator(), Color.parseColor("#38304c"), Color.parseColor("#00000000"));
        animator.setDuration(300);
        animator.start();
        password.setHintTextColor(getColor(R.color.matteWhite));
    }

    //Brings the user to the Register page with the onRegisterClick function declared in the XML element
    public void onRegisterClick(View v){
        startActivity(new Intent(this, RegisterPageActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }


}

