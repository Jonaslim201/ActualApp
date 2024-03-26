package com.example.actualapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;


import java.util.Map;


public class LoadingButton{

    private Context context;
    private Resources resources;
    private CircularProgressButton progressButton;


    LoadingButton(CircularProgressButton progressButton, Context context, TextInputEditText username, TextInputEditText password){
        this.context = context;
        this.resources = context.getResources();
        this.progressButton = progressButton;
    }

    public View getButton(){
        return progressButton;
    }

    private int defaultColor(Context context) {
        return ContextCompat.getColor(context, R.color.darkTeal);
    }

    private Bitmap defaultDoneImage(Resources resources) {
        return BitmapFactory.decodeResource(resources, R.drawable.white_tick);
    }

    public void morphDoneAndRevert(final Map user, boolean registering) {
        morphDoneAndRevert(user, defaultColor(context), defaultDoneImage(resources), 1000L, 3000L, registering);
    }

    private boolean morphDoneAndRevert(
            Map user,
            int fillColor,
            Bitmap bitmap,
            Long doneTime,
            Long revertTime,
            boolean registering
    ) {
        progressButton.setProgressType(com.github.leandroborgesferreira.loadingbutton.animatedDrawables.ProgressType.INDETERMINATE);
        progressButton.startAnimation();
        final boolean[] successful = {false};
        Firestore db = new Firestore();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!registering){
                    db.loginUser(user, context, new LoginCallBack() {
                        @Override
                        public void onLoginResult(boolean success) {
                            if (success) {
                                Log.d("Debug", "logged in/registered");
                                successful[0] = true;
                            } else {
                                Log.d("Debug", "not logged in/not registered");
                            }
                        }
                    }, false);
                } else if (registering){
                    Log.d("register", "registering");
                    db.registerUser(user, context, new LoginCallBack(){
                        @Override
                        public void onLoginResult(boolean success) {
                            if (success) {
                                Log.d("Debug", "logged in/registered");
                                successful[0] = true;
                            } else {
                                Log.d("Debug", "not logged in/not registered");
                            }
                        }
                    }, true);
                }
                progressButton.doneLoadingAnimation(fillColor, bitmap);
            }
        }, doneTime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressButton.revertAnimation();
            }
        }, revertTime);
        return successful[0];
    }

}
