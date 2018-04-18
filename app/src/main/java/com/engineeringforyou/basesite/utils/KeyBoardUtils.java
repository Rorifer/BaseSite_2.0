package com.engineeringforyou.basesite.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtils {
    public static void hideKeyboard(Context context, View view) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                if (view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    public static void showKeyboard(Context context, View view) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
}
