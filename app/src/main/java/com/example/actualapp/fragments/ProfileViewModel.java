package com.example.actualapp.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> mImage; // Change to MutableLiveData<Integer> for image resource ID

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mImage = new MutableLiveData<>();
        mText.setValue("This is profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Integer> getImage() {
        return mImage;
    }

    // Method to set the image resource ID
    public void setImageResource(int resourceId) {
        mImage.setValue(resourceId);
    }
}
