package com.engineeringforyou.basesite.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl;
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.Single;

public class FirebaseUtils {

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getPhoneCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? null : user.getPhoneNumber();
    }

    @Nullable
    public static String getIdCurrentUser() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static Single<String> getToken() {
        return Single.create(emitter ->
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnSuccessListener(instanceIdResult -> {
                            emitter.onSuccess(instanceIdResult.getToken());
                        })
                        .addOnFailureListener(emitter::onError)
        );
    }

    public static void updateToken(Context context) {
        Boolean isNotificationEnabled = new SettingsRepositoryImpl(context).getStatusNotification();
        if (isNotificationEnabled) new FirebaseRepositoryImpl().enableStatusNotification();
    }

    public static void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }
}
