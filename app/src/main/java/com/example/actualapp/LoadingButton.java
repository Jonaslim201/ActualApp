package com.example.actualapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;


public class LoadingButton{

    private Context context;
    private Resources resources;
    private CircularProgressButton progressButton;


    public LoadingButton(CircularProgressButton progressButton, Context context, TextInputEditText username, TextInputEditText password){
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


    //Loads animation
    public void morphDoneAndRevert() {
        morphDoneAndRevert(defaultColor(context), defaultDoneImage(resources), 1000L);
    }


    //Starts animation
    private void morphDoneAndRevert(
            int fillColor,
            Bitmap bitmap,
            Long doneTime
    ) {
        progressButton.setProgressType(com.github.leandroborgesferreira.loadingbutton.animatedDrawables.ProgressType.INDETERMINATE);
        progressButton.startAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressButton.doneLoadingAnimation(fillColor, bitmap);
            }
        }, doneTime);
    }

    //Starts revert animation
    public void revert(Long revertTime){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressButton.revertAnimation();
            }
        }, revertTime);
    }
}

