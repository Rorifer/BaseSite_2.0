package com.engineeringforyou.basesite.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUtils {

    //TODO Перенести в репозиторий
    private static final String FIELD_TOKEN = "token";
    private static final String FIELD_DATE_CREATED = "dateCreate";
    private static final String DIRECTORY_USER = "users";
    private static final String DIRECTORY_SETTINGS = "settings";
    private static final String DOCUMENT_NOTIFICATION = "notification";




    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void updateToken(String token) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getPhoneNumber() != null) {

            if (token == null) token = FirebaseInstanceId.getInstance().getToken();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> newToken = new HashMap<>();
            newToken.put(FIELD_TOKEN, token);
            newToken.put(FIELD_DATE_CREATED, new Date());

            DocumentReference userDocumentNotif = firestore
                    .collection(DIRECTORY_USER)
                    .document(currentUser.getPhoneNumber())
                    .collection(DIRECTORY_SETTINGS)
                    .document(DOCUMENT_NOTIFICATION);

            userDocumentNotif.set(newToken);
        }
    }


    public static void deleteToken() {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getPhoneNumber() != null) {

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> newToken = new HashMap<>();
            newToken.put(FIELD_TOKEN, null);
            newToken.put(FIELD_DATE_CREATED, new Date());

            DocumentReference userDocumentNotif = firestore
                    .collection(DIRECTORY_USER)
                    .document(currentUser.getPhoneNumber())
                    .collection(DIRECTORY_SETTINGS)
                    .document(DOCUMENT_NOTIFICATION);

            userDocumentNotif.set(newToken);
        }
    }

}
