package com.roomchatapp.amstudio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Home Activity displaying a list of chat rooms from Firebase Realtime Database.
 */
public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRooms;
    private ProgressBar progressBar;
    private TextView tvNoRooms;
    private LinearLayout profile_layout;
    private RoomAdapter roomAdapter;
    private List<RoomModel> roomList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set status bar color to black for a consistent look
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_home);

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        initViews();

        // Setup Image Slider (Banner)
        setupImageSlider();

        // Setup RecyclerView
        setupRecyclerView();

        // Load Rooms from Firebase
        loadRoomsFromFirebase();

        // Setup Click Listeners
        setupClickListeners();
    }

    private void initViews() {
        rvRooms = findViewById(R.id.rvRooms);
        progressBar = findViewById(R.id.progressBar);
        tvNoRooms = findViewById(R.id.tvNoRooms);
        profile_layout = findViewById(R.id.profile_layout);
        
        // Initialize Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        // Placeholder images for the slider
        slideModels.add(new SlideModel("https://i.ibb.co/Q7dp3r5h/IMG-20260705-WA0006.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/Z6hm47RW/IMG-20260705-WA0005.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void setupRecyclerView() {
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList);
        
        // Using GridLayoutManager with 2 columns
        rvRooms.setLayoutManager(new GridLayoutManager(this, 2));
        rvRooms.setAdapter(roomAdapter);
    }

    private void loadRoomsFromFirebase() {
        // Show ProgressBar while loading
        progressBar.setVisibility(View.VISIBLE);
        tvNoRooms.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        RoomModel room = ds.getValue(RoomModel.class);
                        if (room != null) {
                            roomList.add(room);
                        }
                    }
                    
                    if (roomList.isEmpty()) {
                        tvNoRooms.setVisibility(View.VISIBLE);
                    } else {
                        tvNoRooms.setVisibility(View.GONE);
                    }
                } else {
                    tvNoRooms.setVisibility(View.VISIBLE);
                }
                
                // Hide ProgressBar and refresh adapter
                progressBar.setVisibility(View.GONE);
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners() {
        // Create Room Button
        findViewById(R.id.ivAdd).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CreateRoomActivity.class));
        });

        // Search Button
        findViewById(R.id.ivSearch).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });

        // Profile / Me Section
        profile_layout.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);

            if (currentUser == null && account == null) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to Profile logic can be added here
                String name = (currentUser != null) ? currentUser.getDisplayName() : (account != null ? account.getDisplayName() : "User");
                Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
