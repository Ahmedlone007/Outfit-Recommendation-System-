package com.example.myapp.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.Common.MyUtill;
import com.example.myapp.MainActivity;
import com.example.myapp.Model.User;
import com.example.myapp.PrefManager;
import com.example.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFirstName, etEmail, etPassword, etLastName;
    RadioGroup etGender, etHeight, etPhysique,etHairColor,etSkinTone;
    private Button signupButton;
    private TextView loginRedirectText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        initViews();

    }

    void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etLastName = findViewById(R.id.etLastName);
        etGender = findViewById(R.id.rgGender);
        etHairColor = findViewById(R.id.rgValue);
        etHeight = findViewById(R.id.rgHeight);
        etPhysique = findViewById(R.id.rgPhysique);
        etSkinTone = findViewById(R.id.rgHue);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(view -> signUpUser());

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private String getSelectedOptionText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            return selectedRadioButton.getText().toString();
        }

        return "";
    }

    private void signUpUser() {
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String gender = getSelectedOptionText(etGender).trim();
        final String skinTone = getSelectedOptionText(etSkinTone).trim();
        final String hairColor = getSelectedOptionText(etHairColor).trim();
        final String height = getSelectedOptionText(etHeight).trim();
        final String physique = getSelectedOptionText(etPhysique).trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)||TextUtils.isEmpty(gender)||TextUtils.isEmpty(skinTone)||TextUtils.isEmpty(hairColor)) {
            // Handle input validation
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up...");
        progressDialog.show();


        User user = new User("abc", firstName,lastName,email,password,"",gender,skinTone,hairColor,height,physique);

        Log.d("yasir", ""+ user);
        checkAndRegisterUserFirebase(user);
    }

    private void checkAndRegisterUserFirebase(final User user) {


        // Checking if User already registered
        mDatabase.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already registered skip signup
                    progressDialog.dismiss();
                    MyUtill.toastMsg(SignUpActivity.this, "User already exists!");
                } else {
                    // User not registered proceed to signup
                    createFirebaseAuthUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                // Handle database error
                MyUtill.toastMsg(SignUpActivity.this, "Database Error");
            }
        });
    }

    private void createFirebaseAuthUser(final User user) {

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // User registered successfully in Firebase Authentication

                            String userId = mAuth.getCurrentUser().getUid();
                            user.setId(userId);

                            // Store user information in the Realtime Database
                            mDatabase.child(userId).setValue(user);
                            new PrefManager(SignUpActivity.this).saveUser(user);

                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Sign Up successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
