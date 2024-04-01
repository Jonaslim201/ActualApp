package com.example.actualapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


//Register Page
public class RegisterPageActivity extends AppCompatActivity {

    private CircularProgressButton registerButton;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputEditText number;
    private TextInputEditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_activity);

        initializeButton();
        initializeTextWatchers();
    }

    //Initializes the register button
    private void initializeButton(){
        registerButton = findViewById(R.id.registerButton);
        final LoadingButton animationButton = new LoadingButton(registerButton, RegisterPageActivity.this, username, password);

        //Disabling button before the user inputs
        registerButton.setEnabled(false);

        //Setting click listener
        registerButton.setOnClickListener(new View.OnClickListener(){
            //Collects all the fields and puts them in a HashMap
            public void onClick(View v){
                Map<String, Object> user = new HashMap<>();
                user.put("username", Objects.requireNonNull(username.getText()).toString());
                user.put("email", Objects.requireNonNull(email.getText()).toString());
                user.put("number", Objects.requireNonNull(number.getText()).toString());
                user.put("password", Objects.requireNonNull(password.getText()).toString());

                //Button calls the morphDoneAndRevert method in the LoadingButton Class to check user credentials
                animationButton.morphDoneAndRevert();

                //Accesses Firestore class to register user
                Firestore.registerUser(user, RegisterPageActivity.this, new FirestoreCallBack() {
                    @Override
                    public void onFirestoreResult(boolean success) {
                        if (success) {
                            //If registration is successful and Firestore doesn't fk up, enters the app.
                            startApp();
                        } else {
                            //If registration fails ie. Username taken or Firestore fked up
                            Toast.makeText(RegisterPageActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, true);
            }
        });
    }


    //Initializes text watchers
    //Only enables the Login button when all fields have inputs
    public void initializeTextWatchers(){
        username = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        number = findViewById(R.id.registerMobile);
        password = findViewById(R.id.registerPassword);

        final TextWatcher commonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameText = Objects.requireNonNull(username.getText()).toString().trim();
                String emailText = Objects.requireNonNull(email.getText()).toString().trim();
                String passwordText = Objects.requireNonNull(password.getText()).toString().trim();
                String numberText = Objects.requireNonNull(number.getText()).toString().trim();

                boolean isValid = usernameText.length() > 0 && emailText.length() > 0 && passwordText.length() > 0 && numberText.length() > 0;
                boolean isUsernamePasswordSame = usernameText.equals(passwordText);

                if (isUsernamePasswordSame) {
                    // Username and password are the same, disable the register button
                    registerButton.setEnabled(false);
                    // Show a message
                    Toast.makeText(RegisterPageActivity.this, "Username cannot be the same as password", Toast.LENGTH_SHORT).show();
                } else {
                    registerButton.setEnabled(isValid);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        username.addTextChangedListener(commonTextWatcher);
        password.addTextChangedListener(commonTextWatcher);
        number.addTextChangedListener(commonTextWatcher);
        email.addTextChangedListener(commonTextWatcher);

    }

    //Brings user to Login page with the onLoginClick function declared in the XML element
    public void onLoginClick(View v){
        startActivity(new Intent(this, LoginPageActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

    //Starts app
    public void startApp(){
        Intent myActivity = new Intent(this, ExerciseCategoriesActivity.class);
        startActivity(myActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }
}
