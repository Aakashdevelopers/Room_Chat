package com.roomchatapp.amstudio;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateRoomActivity extends AppCompatActivity {
    Button btnCreate;
    EditText etRoomDescription;
    ImageView pickimg;
    ImageView ivRoomCover;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private String imgUrl = "";

    // Replace with your actual ImgBB API key
    private static final String IMGBB_API_KEY = "d909717479f29f4de1b6efc62ec33528";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_create_room);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        btnCreate = findViewById(R.id.btnCreate);
        etRoomDescription = findViewById(R.id.etRoomDescription);
        pickimg = findViewById(R.id.pickimg);
        ivRoomCover = findViewById(R.id.ivRoomCover);






        pickimg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnCreate.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open image file", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] bytes = getBytes(inputStream);
            inputStream.close();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", IMGBB_API_KEY)
                    .addFormDataPart("image", "image.jpg",
                            RequestBody.create(bytes, MediaType.parse("image/*")))
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(CreateRoomActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseData);
                            imgUrl = jsonObject.getJSONObject("data").getString("url");
                            runOnUiThread(() -> saveRoom());
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(CreateRoomActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(CreateRoomActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void saveRoom() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(CreateRoomActivity.this);

        if (account == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateRoomActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String userId = account.getId(); // Correct



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rooms");
        String roomId = ref.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("room_name", etRoomDescription.getText().toString().trim());
        map.put("img", imgUrl);
        map.put("uid", userId);
        map.put("roomId", roomId);

        ref.child(roomId).setValue(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Room Created Successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Start RoomChatActivity as Host
                    Intent intent = new Intent(CreateRoomActivity.this, RoomChatActivity.class);
                    intent.putExtra("roomID", roomId);
                    intent.putExtra("room_name", etRoomDescription.getText().toString().trim());
                    intent.putExtra("username", account.getDisplayName());
                    intent.putExtra("host", true); // Creator is the Host
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivRoomCover.setImageURI(imageUri);
        }
    }
}
