package com.example.myapp.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.myapp.Model.User;
import com.google.firebase.database.annotations.NotNull;

public class MyUtill {
    public static User currentUser = null;
    public static String CAT_ID = "categoryId";
    public static String FOOD_ID = "FoodId";
    public static int CATEGORY_COVER_COLOR;
    private static final String USERPhone = null;
    private static final String USERPassword = null;
    private static final String USER_PASSWORD_PREFS = "USER_PASSWORD_PREFS";
    private static final String USER_PHONE_PREFS = "USER_PHONE_PREFS";
    public static boolean isUpdate = false;

    public static void toastMsg(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public static String getUserPhone(@NotNull Context context) {
        return context.getSharedPreferences(USER_PHONE_PREFS, 0).getString(USERPhone, null);
    }

    public static void setUserPhone(@NotNull Context context, String user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PHONE_PREFS, 0).edit();
        editor.putString(USERPhone, user);
        editor.apply();
    }

    public static String getUserPassword(@NotNull Context context) {
        return context.getSharedPreferences(USER_PASSWORD_PREFS, 0).getString(USERPassword, null);
    }

    public static void setUserPassword(@NotNull Context context, String userPassword) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PASSWORD_PREFS, 0).edit();
        editor.putString(USERPassword, userPassword);
        editor.apply();
    }

}
