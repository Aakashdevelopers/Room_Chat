package com.roomchatapps.amstudio;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navExplore, navMessage, navMe;
    private ImageView ivHome, ivExplore, ivMessage, ivMe;
    private TextView tvHome, tvExplore, tvMessage, tvMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        
        // Handle window insets
        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // We might not want padding on the whole content for EdgeToEdge
                // But we should ensure bottom navigation is not covered
                return insets;
            });
        }

        // Load default fragment if this is a fresh start
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            updateNavUI(0);
        }
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
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                loadFragment(new HomeFragment());
                updateNavUI(0);
            });
        }

        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                loadFragment(new ExploreFragment());
                updateNavUI(1);
            });
        }

        if (navMessage != null) {
            navMessage.setOnClickListener(v -> {
                loadFragment(new MessageFragment());
                updateNavUI(2);
            });
        }

        if (navMe != null) {
            navMe.setOnClickListener(v -> {
                loadFragment(new ProfileFragment());
                updateNavUI(3);
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void updateNavUI(int selectedIndex) {
        // Reset all to default color if they are not null
        if (ivHome != null) ivHome.setColorFilter(Color.parseColor("#88FFFFFF"));
        if (tvHome != null) tvHome.setTextColor(Color.parseColor("#88FFFFFF"));
        if (ivExplore != null) ivExplore.setColorFilter(Color.parseColor("#88FFFFFF"));
        if (tvExplore != null) tvExplore.setTextColor(Color.parseColor("#88FFFFFF"));
        if (ivMessage != null) ivMessage.setColorFilter(Color.parseColor("#88FFFFFF"));
        if (tvMessage != null) tvMessage.setTextColor(Color.parseColor("#88FFFFFF"));
        if (ivMe != null) ivMe.setColorFilter(Color.parseColor("#88FFFFFF"));
        if (tvMe != null) tvMe.setTextColor(Color.parseColor("#88FFFFFF"));

        // Highlight selected
        switch (selectedIndex) {
            case 0:
                if (ivHome != null) ivHome.setColorFilter(Color.parseColor("#40E0D0"));
                if (tvHome != null) tvHome.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                if (ivExplore != null) ivExplore.setColorFilter(Color.parseColor("#40E0D0"));
                if (tvExplore != null) tvExplore.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 2:
                if (ivMessage != null) ivMessage.setColorFilter(Color.parseColor("#40E0D0"));
                if (tvMessage != null) tvMessage.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 3:
                if (ivMe != null) ivMe.setColorFilter(Color.parseColor("#40E0D0"));
                if (tvMe != null) tvMe.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }
}
