package com.roomchatapps.amstudio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomchatapps.amstudio.R;
import com.roomchatapps.amstudio.databinding.ItemFollowBinding;
import com.roomchatapps.amstudio.models.User;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private List<User> userList;
    private String currentUid;

    public FollowAdapter(List<User> userList) {
        this.userList = userList;
        this.currentUid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowBinding binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.tvName.setText(user.getUserName());
        holder.binding.tvUid.setText("ID: " + user.getUserId());

        Glide.with(holder.itemView.getContext())
                .load(user.getUserIcon())
                .placeholder(R.drawable.common_default_avatar_ic)
                .error(R.drawable.common_default_avatar_ic)
                .into(holder.binding.ivAvatar);

        if (currentUid != null && !currentUid.equals(user.getUserId())) {
            holder.binding.btnAction.setVisibility(View.VISIBLE);
            checkFollowStatus(user.getUserId(), holder.binding.btnAction);
            
            holder.binding.btnAction.setOnClickListener(v -> toggleFollow(user.getUserId(), holder.binding.btnAction));

            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), com.roomchatapps.amstudio.ChatActivity.class);
                intent.putExtra("receiverId", user.getUserId());
                intent.putExtra("receiverName", user.getUserName());
                v.getContext().startActivity(intent);
            });
        } else {
            holder.binding.btnAction.setVisibility(View.GONE);
        }
    }

    private void checkFollowStatus(String targetUid, View btn) {
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("Follow")
                .child(currentUid).child("following").child(targetUid);
        
        followRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (btn instanceof android.widget.Button) {
                    android.widget.Button button = (android.widget.Button) btn;
                    if (snapshot.exists()) {
                        button.setText("Following");
                        button.setAlpha(0.6f);
                    } else {
                        button.setText("Follow");
                        button.setAlpha(1.0f);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void toggleFollow(String targetUid, View btn) {
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Follow")
                .child(currentUid).child("following").child(targetUid);
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Follow")
                .child(targetUid).child("followers").child(currentUid);

        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    followingRef.removeValue();
                    followersRef.removeValue();
                } else {
                    followingRef.setValue(true);
                    followersRef.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFollowBinding binding;

        public ViewHolder(ItemFollowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
