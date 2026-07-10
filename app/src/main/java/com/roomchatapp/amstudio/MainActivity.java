package com.roomchatapp.amstudio;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.Color;
import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navExplore, navMessage, navMe;
    private ImageView ivHome, ivExplore, ivMessage, ivMe;
    private TextView tvHome, tvExplore, tvMessage, tvMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false); // White status bar icons

        // Load default fragment
        loadFragment(new HomeFragment());
    }

    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessage = findViewById(R.id.nav_message);
        navMe = findViewById(R.id.nav_me);

        ivHome = findViewById(R.id.iv_nav_home);
        ivExplore = findViewById(R.id.iv_nav_explore);
        ivMessage = findViewById(R.id.iv_nav_message);
        ivMe = findViewById(R.id.iv_nav_me);

        tvHome = findViewById(R.id.tv_nav_home);
        tvExplore = findViewById(R.id.tv_nav_explore);
        tvMessage = findViewById(R.id.tv_nav_message);
        tvMe = findViewById(R.id.tv_nav_me);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavUI(0);
        });

        navExplore.setOnClickListener(v -> {
            loadFragment(new ExploreFragment());
            updateNavUI(1);
        });

        navMessage.setOnClickListener(v -> {
            loadFragment(new MessageFragment());
            updateNavUI(2);
        });

        navMe.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateNavUI(3);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void updateNavUI(int selectedIndex) {
        // Reset all to default color
        ivHome.setColorFilter(Color.parseColor("#88FFFFFF"));
        tvHome.setTextColor(Color.parseColor("#88FFFFFF"));
        ivExplore.setColorFilter(Color.parseColor("#88FFFFFF"));
        tvExplore.setTextColor(Color.parseColor("#88FFFFFF"));
        ivMessage.setColorFilter(Color.parseColor("#88FFFFFF"));
        tvMessage.setTextColor(Color.parseColor("#88FFFFFF"));
        ivMe.setColorFilter(Color.parseColor("#88FFFFFF"));
        tvMe.setTextColor(Color.parseColor("#88FFFFFF"));

        // Highlight selected
        switch (selectedIndex) {
            case 0:
                ivHome.setColorFilter(Color.parseColor("#40E0D0"));
                tvHome.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                ivExplore.setColorFilter(Color.parseColor("#40E0D0"));
                tvExplore.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 2:
                ivMessage.setColorFilter(Color.parseColor("#40E0D0"));
                tvMessage.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 3:
                ivMe.setColorFilter(Color.parseColor("#40E0D0"));
                tvMe.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }
}
