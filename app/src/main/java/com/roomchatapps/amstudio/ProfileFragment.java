package com.roomchatapps.amstudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomchatapps.amstudio.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            loadUserData();
            loadFollowStats(uid);
        }

        setupClickListeners();
    }

    private void loadFollowStats(String uid) {
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("Follow").child(uid);
        
        // Followers count
        followRef.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding != null) {
                    binding.tvFollowCount.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Following count
        followRef.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding != null) {
                    binding.tvFansCount.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUserData() {
        if (userRef == null) return;

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || binding == null) return;

                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String uid = snapshot.child("uid").getValue(String.class);
                    String avatar = snapshot.child("avtar").getValue(String.class);
                    
                    Object coinsObj = snapshot.child("coins").getValue();
                    String coins = coinsObj != null ? String.valueOf(coinsObj) : "0";

                    binding.userName.setText(name != null ? name : "User");
                    binding.userId.setText("ID: " + (uid != null ? uid : "N/A"));
                    binding.tvCoins.setText(coins);

                    if (avatar != null && !avatar.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                                .load(avatar)
                                .placeholder(R.drawable.common_default_avatar_ic)
                                .error(R.drawable.common_default_avatar_ic)
                                .into(binding.profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners() {
        binding.layoutFollowers.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getActivity(), FollowListActivity.class);
                intent.putExtra("uid", user.getUid());
                intent.putExtra("type", "followers");
                startActivity(intent);
            }
        });

        binding.layoutFollowing.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getActivity(), FollowListActivity.class);
                intent.putExtra("uid", user.getUid());
                intent.putExtra("type", "following");
                startActivity(intent);
            }
        });

        binding.cardWallet.setOnClickListener(v -> showToast("Wallet"));
        binding.cardBuyCoin.setOnClickListener(v -> showToast("Top-up Coins"));
        binding.cardStore.setOnClickListener(v -> showToast("Store"));
        binding.cardHistory.setOnClickListener(v -> showToast("History"));
        
        binding.cardSetting.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        binding.headerLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.btnRefer.setOnClickListener(v -> showToast("Refer & Earn"));
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message + " feature coming soon!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
