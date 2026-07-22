package com.roomchatapps.amstudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roomchatapps.amstudio.databinding.ActivityEditProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Uri imageUri;
    private String currentAvatarUrl;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    binding.profileImage.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        if (uid == null) {
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        loadUserData();
        setupClickListeners();
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String avatar = snapshot.child("avtar").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);

                    currentAvatarUrl = avatar;
                    binding.etName.setText(name);
                    binding.etBio.setText(bio);

                    if (avatar != null && !avatar.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(avatar)
                                .placeholder(R.drawable.common_default_avatar_ic)
                                .into(binding.profileImage);
                    }

                    if ("Male".equalsIgnoreCase(gender)) {
                        binding.rbMale.setChecked(true);
                    } else if ("Female".equalsIgnoreCase(gender)) {
                        binding.rbFemale.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnEditAvatar.setOnClickListener(v -> mGetContent.launch("image/*"));

        binding.btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = binding.etName.getText().toString().trim();
        String bio = binding.etBio.getText().toString().trim();
        String gender = binding.rbMale.isChecked() ? "Male" : (binding.rbFemale.isChecked() ? "Female" : "");

        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            return;
        }

        binding.btnSave.setEnabled(false);
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

        if (imageUri != null) {
            uploadImageAndSave(name, bio, gender);
        } else {
            updateDatabase(name, bio, gender, currentAvatarUrl);
        }
    }

    private void uploadImageAndSave(String name, String bio, String gender) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + mAuth.getUid());
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> 
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateDatabase(name, bio, gender, uri.toString());
            })
        ).addOnFailureListener(e -> {
            binding.btnSave.setEnabled(true);
            Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDatabase(String name, String bio, String gender, String avatarUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        updates.put("gender", gender);
        if (avatarUrl != null) {
            updates.put("avtar", avatarUrl);
        }

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            binding.btnSave.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
