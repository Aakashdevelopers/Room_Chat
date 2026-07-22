package com.roomchatapps.amstudio;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment;

import java.util.Collections;
import java.util.Random;

/**
 * RoomChatActivity implements the ZEGOCLOUD Prebuilt Live Audio Room UI Kit.
 * This implementation is verified against the installed SDK version (2.6.1).
 */
public class RoomChatActivity extends AppCompatActivity {

    private static final String TAG = "RoomChatActivity";
    
    // ZEGOCLOUD Credentials
    private final long appID = 1617938068L;
    private final String appSign = "42ec36b953df78d6ff96fde5f706ecdb9ae8854fb9528c8a79ba742f58ff4548";

    private String roomID;
    private String userID;
    private String userName;
    private String roomNameLabel;
    private String roomImg;
    private boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        // 1. Retrieve data passed from the previous activity
        roomID = getIntent().getStringExtra("roomID");
        userName = getIntent().getStringExtra("username");
        userID = getIntent().getStringExtra("userID");
        isHost = getIntent().getBooleanExtra("host", false);
        roomNameLabel = getIntent().getStringExtra("room_name");
        roomImg = getIntent().getStringExtra("img");

        // Validation and fallbacks
        if (roomID == null || roomID.isEmpty()) roomID = "default_room";
        if (userID == null || userID.isEmpty()) userID = "user_" + System.currentTimeMillis();
        if (userName == null || userName.isEmpty()) userName = "User_" + new Random().nextInt(1000);

        // 2. Add the Prebuilt UI Fragment
        addFragment();

        // 3. Handle System Back Press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // The UI Kit handles room exit logic, we finish the activity.
                finish();
            }
        });
    }

    /**
     * Configures and attaches the ZegoUIKitPrebuiltLiveAudioRoomFragment.
     */
    private void addFragment() {
        ZegoUIKitPrebuiltLiveAudioRoomConfig config;
        if (isHost) {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.host();
        } else {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.audience();
        }

        ZegoUIKitPrebuiltLiveAudioRoomFragment fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(
                appID, appSign, userID, userName, roomID, config);

        // Setup custom background
        AudioRoomBackgroundView backgroundView = new AudioRoomBackgroundView(this);
        backgroundView.setRoomName(roomNameLabel != null ? roomNameLabel : "Room");
        backgroundView.setRoomID(roomID);
        backgroundView.setBackgroundImage(roomImg);
        fragment.setBackgroundView(backgroundView);

        // Add Gift Button
        addGiftButton(fragment);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow();
    }

    /**
     * Adds a custom gift button to the bottom menu bar.
     */
    private void addGiftButton(ZegoUIKitPrebuiltLiveAudioRoomFragment fragment) {
        int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        ImageView giftButton = new ImageView(this);
        giftButton.setLayoutParams(new ViewGroup.LayoutParams(sizeInPx, sizeInPx));
        giftButton.setImageResource(R.drawable.room_gift_ic);
        giftButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        giftButton.setPadding(18, 10, 10, 10);
        giftButton.setOnClickListener(v -> showGiftDialog());

        // This adds the button to all roles (Host, Speaker, Audience)
        fragment.addButtonToBottomMenuBar(Collections.singletonList(giftButton),
                com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomRole.HOST);
        fragment.addButtonToBottomMenuBar(Collections.singletonList(giftButton),
                com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomRole.SPEAKER);
        fragment.addButtonToBottomMenuBar(Collections.singletonList(giftButton),
                com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomRole.AUDIENCE);
    }

    /**
     * Displays the gift dialog.
     */
    private void showGiftDialog() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The Prebuilt UI Kit cleans up ZegoEngine automatically.
    }
}
