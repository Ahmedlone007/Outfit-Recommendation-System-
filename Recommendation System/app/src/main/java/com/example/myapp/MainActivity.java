package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.myapp.Model.User;
import com.example.myapp.activites.LoginActivity;
import com.example.myapp.databinding.ActivityMainBinding;
import com.example.myapp.ui.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    View headerView;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        headerView = navigationView.getHeaderView(0);
        fetchUserData();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                // Handle logout click
                FirebaseAuth.getInstance().signOut();
                new PrefManager(MainActivity.this).saveUser(null);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

    }


    void fetchUserData(){
        currentUser = new PrefManager(this).getUser();
        new Handler().postDelayed(() -> {
            // Assume we fetched the user data successfully
            if (currentUser != null) {
                TextView navName = headerView.findViewById(R.id.nav_name);
                navName.setText(currentUser.getFirstName());
                TextView navEmail = headerView.findViewById(R.id.nav_email);
                navEmail.setText(currentUser.getEmail());
                CircleImageView nav_img = headerView.findViewById(R.id.nav_image);
                if(currentUser.getImageUrl() != null && !currentUser.getImageUrl().toString().equals("")){
                    Glide.with(this).load(currentUser.getImageUrl()).into(nav_img);
                }else {
                    if(currentUser.getGender().toString().toLowerCase().equals("male")){
                        Glide.with(this).load(R.drawable.male_user).into(nav_img);
                    }else {
                        Glide.with(this).load(R.drawable.female_user).into(nav_img);

                    }
                }
            }
        }, 2000); // Simulated delay of 2 seconds
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Forward the result to the fragment
        ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.nav_profile);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}