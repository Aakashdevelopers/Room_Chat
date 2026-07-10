package com.roomchatapp.amstudio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.roomchatapp.amstudio.R;
import com.roomchatapp.amstudio.models.User;
import java.util.List;

public class SpeakerAdapter extends RecyclerView.Adapter<SpeakerAdapter.ViewHolder> {

    private List<User> speakerList;

    public SpeakerAdapter(List<User> speakerList) {
        this.speakerList = speakerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_speaker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = speakerList.get(position);
        holder.tvSpeakerName.setText(user.getUserName());
        
        Glide.with(holder.itemView.getContext())
                .load(user.getUserIcon())
                .placeholder(R.drawable.logo_placeholder)
                .into(holder.ivSpeakerProfile);

        // Mic Status
        if (user.isMicOn()) {
            holder.ivMicStatus.setImageResource(R.drawable.ic_launcher_foreground); // Replace with ic_mic_on
            holder.ivMicStatus.setVisibility(View.VISIBLE);
        } else {
            holder.ivMicStatus.setImageResource(R.drawable.ic_launcher_background); // Replace with ic_mic_off
            holder.ivMicStatus.setVisibility(View.VISIBLE);
        }

        // Speaking Detection Animation (Highlight stroke)
        if (user.isSpeaking()) {
            holder.ivSpeakerProfile.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.accent_primary));
            holder.ivSpeakerProfile.setStrokeWidth(4f);
        } else {
            holder.ivSpeakerProfile.setStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.glass_white));
            holder.ivSpeakerProfile.setStrokeWidth(2f);
        }
        
        // Host Badge (if you want to add one, or use a different stroke)
        if (user.isHost()) {
            // Optional: Special UI for host
        }
    }

    @Override
    public int getItemCount() {
        return speakerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView ivSpeakerProfile;
        ImageView ivMicStatus;
        TextView tvSpeakerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpeakerProfile = itemView.findViewById(R.id.ivSpeakerProfile);
            ivMicStatus = itemView.findViewById(R.id.ivMicStatus);
            tvSpeakerName = itemView.findViewById(R.id.tvSpeakerName);
        }
    }
}
