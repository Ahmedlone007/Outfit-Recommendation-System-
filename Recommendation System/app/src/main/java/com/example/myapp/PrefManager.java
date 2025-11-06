package com.example.myapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapp.Model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "myapp";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String KEY_USER = "user_key";
    private static final String KEY_JSON_ARRAY = "jsonArray";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return false;
        //return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(KEY_USER, json);
        editor.apply();
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_USER, null);

        return gson.fromJson(json, User.class);
    }

    public void saveJsonArray( JSONArray jsonArray) {
        editor.putString(KEY_JSON_ARRAY, jsonArray.toString());
        editor.apply();
    }
    public JSONArray getJsonArray() {
        String jsonArrayString = pref.getString(KEY_JSON_ARRAY, "");

        // Convert the stored string back to a JSON array
        try {
            return new JSONArray(jsonArrayString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

}
