package com.example.actualapp;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

public class holdDocument {

    private DocumentSnapshot userDocument;
    private boolean documentFound;
    holdDocument(){
    }

    public void setFoundDocument(DocumentSnapshot foundDocument) {
        documentFound = true;
        Log.d("Debug", "set to true");
        this.userDocument = foundDocument;
    }

    public DocumentSnapshot getFoundDocument() {
        return userDocument;
    }

    public boolean isDocumentFound() {
        return documentFound;
    }
}
