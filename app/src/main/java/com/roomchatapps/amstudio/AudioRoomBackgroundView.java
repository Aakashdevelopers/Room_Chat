package com.roomchatapps.amstudio;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AudioRoomBackgroundView extends FrameLayout {

    private TextView roomName;
    private TextView roomID;
    private ImageView backgroundImageView;

    public AudioRoomBackgroundView(@NonNull Context context) {
        super(context);
        initView();
    }

    public AudioRoomBackgroundView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AudioRoomBackgroundView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        backgroundImageView = new ImageView(getContext());
        backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(backgroundImageView, new FrameLayout.LayoutParams(-1, -1));

        // Background will be set via setBackgroundImage
        // setBackgroundColor(Color.parseColor("#FFC0CB")); // Removed pink default

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        
        roomName = new TextView(getContext());
        roomName.setMaxLines(1);
        roomName.setEllipsize(TruncateAt.END);
        roomName.setSingleLine(true);
        roomName.getPaint().setFakeBoldText(true);
        roomName.setTextColor(Color.parseColor("#ff1b1b1b")); // Dark text for light background
        roomName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        roomName.setMaxWidth(dp2px(200));
        linearLayout.addView(roomName, params);

        roomID = new TextView(getContext());
        roomID.setMaxLines(1);
        roomID.setEllipsize(TruncateAt.END);
        roomID.setSingleLine(true);
        roomID.setTextColor(Color.parseColor("#ff606060"));
        roomID.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        roomID.setMaxWidth(dp2px(120));
        linearLayout.addView(roomID, params);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        int marginStart = dp2px(16);
        int marginTop = dp2px(40); // Pushed down a bit
        layoutParams.setMargins(marginStart, marginTop, 0, 0);
        addView(linearLayout, layoutParams);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setRoomName(String roomName) {
        this.roomName.setText(roomName);
    }

    public void setRoomID(String roomID) {
        this.roomID.setText("ID: " + roomID);
    }

    public void setBackgroundImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            com.bumptech.glide.Glide.with(getContext())
                    .load(imageUrl)
                    .into(backgroundImageView);
        }
    }

    public void setBackgroundImage(int resId) {
        // Set background to the whole FrameLayout
        setBackgroundResource(resId);
        // Also set it to the ImageView if we want ScaleType.CENTER_CROP
        backgroundImageView.setImageResource(resId);
    }
}
