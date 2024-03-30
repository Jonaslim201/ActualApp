package com.example.actualapp;


//Callback for success or failure for Login and Register
public interface LoginAndRegisterCallBack {
    void onFirestoreResult(boolean success);
}