package com.roomchatapps.amstudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.roomchatapps.amstudio.databinding.ActivitySettingsBinding;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setupClickListeners();
        calculateCacheSize();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        binding.btnAccountSafety.setOnClickListener(v -> showToast("Account & Safety"));
        binding.btnNotifications.setOnClickListener(v -> showToast("Notifications"));
        binding.btnPrivacy.setOnClickListener(v -> showToast("Privacy Settings"));
        
        binding.btnClearCache.setOnClickListener(v -> {
            clearCache();
            showToast("Cache cleared");
            binding.tvCacheSize.setText("0 MB");
        });

        binding.btnAbout.setOnClickListener(v -> showToast("About Voxora"));

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void calculateCacheSize() {
        long size = 0;
        size += getDirSize(getCacheDir());
        size += getDirSize(getExternalCacheDir());
        
        String sizeStr = formatSize(size);
        binding.tvCacheSize.setText(sizeStr);
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirSize(file);
                }
            }
        } else if (dir != null && dir.isFile()) {
            size += dir.length();
        }
        return size;
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 MB";
        double mb = size / (1024.0 * 1024.0);
        return String.format("%.1f MB", mb);
    }

    private void clearCache() {
        try {
            deleteDir(getCacheDir());
            deleteDir(getExternalCacheDir());
        } catch (Exception e) {}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
}
