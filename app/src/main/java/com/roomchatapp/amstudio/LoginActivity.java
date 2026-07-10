package com.roomchatapp.amstudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gsc;
    GoogleSignInClient gco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout llFacebook = findViewById(R.id.llFacebook);
        LinearLayout llGoogle = findViewById(R.id.llGoogle);
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        ImageView ivEmail = findViewById(R.id.ivEmail);

        gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        gco = GoogleSignIn.getClient(this, gsc);
        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToMainActivity(account);
        }

        TextView tvTerms = findViewById(R.id.tvTerms);
        TextView tvPrivacy = findViewById(R.id.tvPrivacy);

        llFacebook.setOnClickListener(v -> {
            // Placeholder for Facebook Login
            Toast.makeText(this, "Facebook login coming soon", Toast.LENGTH_SHORT).show();
        });

        llGoogle.setOnClickListener(v -> {
            signIn();
        });

        tvFeedback.setOnClickListener(v -> {
            // Handle Feedback
            Toast.makeText(this, "Feedback feature coming soon", Toast.LENGTH_SHORT).show();
        });

        tvTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show();
        });

        tvPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
        });

        ivEmail.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, EmailloginActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMainActivity(GoogleSignInAccount account) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (account != null) {
            intent.putExtra("user_name", account.getDisplayName());
        }
        startActivity(intent);
        finish();
    }

    void signIn(){
      Intent signInIntent = gco.getSignInIntent();
      startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getId() != null) {

                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(account.getId());

                    userRef.child("name").setValue(account.getDisplayName());
                    userRef.child("email").setValue(account.getEmail());
                    userRef.child("premium").setValue("no");
                    userRef.child("uid").setValue(account.getId());
                    if (account.getPhotoUrl() != null) {
                        userRef.child("avtar").setValue(account.getPhotoUrl().toString());
                    }
                }
                navigateToMainActivity(account);
            } catch (ApiException e){
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
