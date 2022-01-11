package com.example.budgetmanagement;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SpecialFunction {

    public static void hideKeyboard(View v) {
        try {
            v.clearFocus();
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            Log.d("hideKeyboard", "View name: " + v.getTag() + "Exception: ", e);
        }
    }
    //method: listen to keyboard key Enter and do click
}
