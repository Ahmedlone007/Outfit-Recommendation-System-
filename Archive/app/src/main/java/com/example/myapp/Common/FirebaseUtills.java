package com.example.myapp.Common;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapp.Model.User;
import com.example.myapp.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtills {

    public static String CURRENT_USER_ID = "abc";
    public static User CURRENT_USER = null;
    public static String APP_NAME = "MyFireApp";
    //    public static String USERS = APP_NAME + "/users";
    public static String USERS = "users";


    public static void getUserById(Context context, String userId) {
        DatabaseReference allUsersRef = FirebaseDatabase.getInstance().getReference(USERS);

        DatabaseReference userRef = allUsersRef.child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found, you can now retrieve the User object
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        new PrefManager(context).saveUser(user);
                    }
                } else {
                    // User data not found
                    Log.d("UserProfileActivity", "User data not found for ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that may occur during the database operation
                Log.e("UserProfileActivity", "Error getting user data", databaseError.toException());
            }
        });
    }

}
