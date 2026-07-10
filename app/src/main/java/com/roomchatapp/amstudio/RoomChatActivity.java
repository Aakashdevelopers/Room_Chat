package com.roomchatapp.amstudio;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomLayoutConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomLayoutRowConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoTranslationText;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.constants.ZegoAudioConfigPreset;

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
        // --- BASIC CONFIGURATION ---
        ZegoUIKitPrebuiltLiveAudioRoomConfig config;
        if (isHost) {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.host();
        } else {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.audience();
        }

        // --- ROOM & SEAT CONFIG ---
        config.takeSeatIndexWhenJoining = isHost ? 0 : -1;
        config.turnOnMicrophoneWhenJoining = isHost;
        config.useSpeakerWhenJoining = true;
        config.closeSeatsWhenJoin = false; // Whether to lock all seats initially

        // --- LAYOUT CONFIG ---
        // Configure dynamic seat layout (e.g., 2 rows of 4 seats)
        config.layoutConfig.rowConfigs = Arrays.asList(
            new ZegoLiveAudioRoomLayoutRowConfig(),
            new ZegoLiveAudioRoomLayoutRowConfig()
        );

        // --- SEAT UI CONFIG ---
        config.seatConfig.showSoundWaveInAudioMode = true; // Visual audio level indicators

        // --- MENU BAR CONFIG ---
        config.bottomMenuBarConfig.showInRoomMessageButton = true;
        
        // Buttons available on the bottom menu bar
        List<ZegoMenuBarButtonName> buttons = new ArrayList<>(Arrays.asList(
            ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
            ZegoMenuBarButtonName.SWITCH_AUDIO_OUTPUT_BUTTON,
            ZegoMenuBarButtonName.SHOW_MEMBER_LIST_BUTTON,
            ZegoMenuBarButtonName.APPLY_TO_TAKE_SEAT_BUTTON,
            ZegoMenuBarButtonName.LEAVE_BUTTON
        ));
        
        config.bottomMenuBarConfig.hostButtons = buttons;
        config.bottomMenuBarConfig.audienceButtons = buttons;

        // --- TRANSLATION / TEXT CUSTOMIZATION (ZegoTranslationText) ---
        ZegoTranslationText translation = config.translationText;
        translation.memberListTitle = "Audience List";
        translation.host = "Room Host";
        translation.speaker = "Speaker";
        translation.takeSeatMenuDialogButton = "Take Seat";
        translation.leaveSeatMenuDialogButton = "Leave Seat";
        translation.inviteToTakeSeatMenuDialogButton = "Invite to Seat";
        translation.memberListAgreeButton = "Accept";
        translation.memberListDisagreeButton = "Decline";
        translation.sendRequestTakeSeatToast = "Request sent to host";

        // --- AUDIO CONFIG ---
        // Set high quality audio profile
        config.audioConfig = new ZegoAudioConfig(ZegoAudioConfigPreset.STANDARD_QUALITY);

        // --- CALLBACKS & LISTENERS ---
        config.removedFromRoomListener = () -> {
            Log.d(TAG, "removedFromRoomListener: I was removed from the room");
            finish();
        };

        // --- INITIALIZE FRAGMENT ---
        ZegoUIKitPrebuiltLiveAudioRoomFragment fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(
            appID, appSign, userID, userName, roomID, config);

        // --- CUSTOM BACKGROUND ---
        AudioRoomBackgroundView backgroundView = new AudioRoomBackgroundView(this);
        backgroundView.setRoomID(roomID);
        fragment.setBackgroundView(backgroundView);

        // --- ATTACH FRAGMENT ---
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();
        
        // --- LOGGING CORE EVENTS ---
        ZegoUIKit.addUserCountOrPropertyChangedListener(users -> {
            Log.d(TAG, "User count or property changed. Current count: " + users.size());
        });

        Log.d(TAG, "Live Audio Room Fragment loaded successfully.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The Prebuilt UI Kit cleans up ZegoEngine automatically.
    }
}
