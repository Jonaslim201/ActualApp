package com.example.actualapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;


import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginPageActivity extends AppCompatActivity{

    CircularProgressButton loginButton;
    TextInputEditText username;
    TextInputEditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_activity);

        initializeButton();
        initializeTextWatchers();
    }

    private void initializeButton(){
        loginButton = findViewById(R.id.loginButton);
        final LoadingButton animationButton = new LoadingButton(loginButton, LoginPageActivity.this, username, password);
        loginButton.setEnabled(false);

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Map<String, Object> user = new HashMap<>();
                user.put("username", Objects.requireNonNull(username.getText()).toString());
                user.put("password", Objects.requireNonNull(password.getText()).toString());
                animationButton.morphDoneAndRevert(user, false);
            }
        });
    }

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
    }



    public void onRegisterClick(View v){
        startActivity(new Intent(this, RegisterPageActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public void startApp(){
        Log.d("Debug", "App started");
    }

}

