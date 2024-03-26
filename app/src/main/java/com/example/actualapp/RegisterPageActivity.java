package com.example.actualapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterPageActivity extends AppCompatActivity {

    CircularProgressButton registerButton;
    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText number;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_activity);

        initializeButton();
        initializeTextWatchers();
    }

    private void initializeButton(){
        registerButton = findViewById(R.id.registerButton);
        final LoadingButton animationButton = new LoadingButton(registerButton, RegisterPageActivity.this, username, password);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Map<String, Object> user = new HashMap<>();
                user.put("username", Objects.requireNonNull(username.getText()).toString());
                user.put("email", Objects.requireNonNull(email.getText()).toString());
                user.put("number", Objects.requireNonNull(number.getText()).toString());
                user.put("password", Objects.requireNonNull(password.getText()).toString());
                animationButton.morphDoneAndRevert(user, true);
            }
        });
    }

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
                registerButton.setEnabled(username.getText().toString().trim().length() > 0
                        && email.getText().toString().trim().length() > 0
                        && password.getText().toString().trim().length() > 0
                        && number.getText().toString().trim().length() > 0);

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

    public void onLoginClick(View v){
        startActivity(new Intent(this, LoginPageActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }
}
