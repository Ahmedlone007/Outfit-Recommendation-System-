package com.example.myapp.activites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.Common.FirebaseUtills;
import com.example.myapp.MainActivity;
import com.example.myapp.PrefManager;
import com.example.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {
    private static DatabaseReference mDatabase;
    public static int SPLASH_TIMER = 3000;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        PrefManager prefManager = new PrefManager(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    // Update the progress bar and display the current value
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 50 milliseconds to simulate loading time
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Once done, you can hide the ProgressBar or do something else
                handler.post(new Runnable() {
                    public void run() {
                        if (prefManager.isFirstTimeLaunch()) {
                            prefManager.setFirstTimeLaunch(false);
                            startActivity(new Intent(SplashActivity.this, IntroScreen.class));
                            finish();
                        } else if (currentUser != null) {
                            String userId = currentUser.getUid();
                            FirebaseUtills.getUserById(SplashActivity.this, userId);
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }

                    }
                });
            }
        }).start();
    }
}