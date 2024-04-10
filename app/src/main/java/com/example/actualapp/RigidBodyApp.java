package com.example.actualapp;

import android.app.Application;

import com.example.actualapp.Firestore.FirestoreListener;

public class RigidBodyApp extends Application {

    public static void startListeners(){
        FirestoreListener.getInstanceListener().friendReqListener();
        FirestoreListener.getInstanceListener().friendsListener();
        FirestoreListener.getInstanceListener().leaderboardListener();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FirestoreListener.getInstanceListener().stopListener();
    }
}
