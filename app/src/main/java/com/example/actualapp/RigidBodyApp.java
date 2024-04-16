package com.example.actualapp;

import android.app.Application;

import com.example.actualapp.Firestore.FirestoreListener;
import com.example.actualapp.userRelated.User;

public class RigidBodyApp extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        User.getUserDoc().update("fcmToken", "");
        FirestoreListener.getInstanceListener().stopListener();
    }
}
