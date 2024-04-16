package com.example.actualapp.Firestore;

import com.google.firebase.firestore.DocumentReference;

import java.util.Comparator;

public class DocumentReferenceComparator implements Comparator<DocumentReference> {
    @Override
    public int compare(DocumentReference o1, DocumentReference o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
