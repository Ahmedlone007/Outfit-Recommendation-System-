package com.example.myapp.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapp.Common.MyUtill;
import com.example.myapp.Model.User;
import com.example.myapp.PrefManager;
import com.example.myapp.R;
import com.example.myapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private EditText etFirstName, etEmail, etPassword, etLastName;
    RadioGroup etGender, etHeight, etPhysique,etHairColor,etSkinTone;
    CircleImageView profile_image;
    ImageView profile_image_btn;
    Button btnUpdateProfile;
    User user;
    private final int PICK_IMAGE_REQUEST = 14;
    private Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    private FragmentProfileBinding binding;
    ProgressDialog progressDialog;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentProfileBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = new PrefManager(requireActivity()).getUser();
        etFirstName = root.findViewById(R.id.etFirstName);
        etLastName = root.findViewById(R.id.etLastName);
        etEmail = root.findViewById(R.id.etEmail);
        etPassword = root.findViewById(R.id.etPassword);
        etGender = root.findViewById(R.id.rgGender);
        etHairColor = root.findViewById(R.id.rgValue);
        etHeight = root.findViewById(R.id.rgHeight);
        etPhysique = root.findViewById(R.id.rgPhysique);
        etSkinTone = root.findViewById(R.id.rgHue);
        btnUpdateProfile = binding.btnUpdateProfile;
        profile_image_btn = binding.profileImageBtnPro;
        profile_image = binding.profileImage;

        populateViews();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    void populateViews() {
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());
        setRadioButton(etGender,user.getGender());
        setRadioButton(etSkinTone,user.getSkinTone());
        setRadioButton(etHairColor,user.getHairColor());
        setRadioButton(etHeight,user.getHeight());
        setRadioButton(etPhysique,user.getPhysique());

        if(user.getImageUrl() != null && !user.getImageUrl().toString().equals("")){
            Glide.with(requireContext()).load(user.getImageUrl()).into(profile_image);
        }else {
            if(user.getGender().toString().toLowerCase().equals("male")){
                Glide.with(requireContext()).load(R.drawable.male_user).into(profile_image);
            }else {
                Glide.with(requireContext()).load(R.drawable.female_user).into(profile_image);

            }
        }

        profile_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void chooseImage() {
        if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            // Request the permission
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        if (filePath != null) {
            final StorageReference ref = storageReference.child("myFireApp/profile/");
            ref.putFile(filePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                user.setImageUrl(downloadUrl.toString());
                                updateUser();
                                MyUtill.toastMsg(requireActivity(), "Updated");
                            } else {
                                MyUtill.toastMsg(requireActivity(), "Update failed: " + task.getException().getMessage());

                            }
                        }
                    });
        } else {
            updateUser();
        }

    }
    private String getSelectedOptionText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = root.findViewById(selectedRadioButtonId);
            return selectedRadioButton.getText().toString();
        }

        return "";
    }

    private void setRadioButton(RadioGroup radioGroup,String textToMatch) {
        // Iterate through each radio button in the radio group
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);

            // Check if the text of the radio button matches the given text
            if (radioButton.getText().toString().equals(textToMatch)) {
                // Set the radio button as checked
                radioButton.setChecked(true);
                return; // Exit the loop since we found the matching radio button
            }
        }
    }

    void updateUser() {
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String gender = getSelectedOptionText(etGender).trim();
        final String skinTone = getSelectedOptionText(etSkinTone).trim();
        final String hairColor = getSelectedOptionText(etHairColor).trim();
        final String height = getSelectedOptionText(etHeight).trim();
        final String physique = getSelectedOptionText(etPhysique).trim();

        user.setFirstName(firstName);
        user.setLastName(lastName);
//        user.setEmail(email);
//        user.setPassword(password);
        user.setGender(gender);
        user.setGender(gender);
        user.setSkinTone(skinTone);
        user.setHairColor(hairColor);
        user.setHeight(height);
        user.setPhysique(physique);

        final DatabaseReference table_user = FirebaseDatabase.getInstance().getReference("users");
        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getId()).exists()) {
                    progressDialog.dismiss();
                    table_user.child(user.getId()).setValue(user);
                    new PrefManager(requireActivity()).saveUser(user);
                    MyUtill.toastMsg(requireActivity(), "Updated successfully!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}