package com.roomchatapps.amstudio;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomchatapps.amstudio.databinding.FragmentMessageBinding;
import com.roomchatapps.amstudio.databinding.ItemMessageBinding;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTabs();
        setupRecyclerView();
    }

    private void setupTabs() {
        binding.tvMessageTab.setAlpha(1f);


        binding.tvMessageTab.setOnClickListener(v -> {
            binding.tvMessageTab.setAlpha(1f);

            binding.activeTabIndicator.animate().translationX(0).setDuration(200);
        });


    }

    private void setupRecyclerView() {
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<MessageItem> messages = new ArrayList<>();
        MessageAdapter adapter = new MessageAdapter(messages);
        binding.rvMessages.setAdapter(adapter);

        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (currentUid == null) return;

        com.google.firebase.database.DatabaseReference recentRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("RecentChats").child(currentUid);

        recentRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                messages.clear();
                for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()) {
                    com.roomchatapps.amstudio.models.ChatMessage chat = ds.getValue(com.roomchatapps.amstudio.models.ChatMessage.class);
                    if (chat != null) {
                        String otherUserId = chat.getSenderId().equals(currentUid) ? chat.getReceiverId() : chat.getSenderId();
                        String lastMsg = chat.getMessage();
                        long timestamp = chat.getTimestamp();
                        
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
                        String time = sdf.format(new java.util.Date(timestamp));
                        
                        // We add a placeholder MessageItem, details will be fetched in Adapter
                        messages.add(new MessageItem(otherUserId, "Loading...", lastMsg, time, timestamp, 0));
                    }
                }
                // Sort by timestamp descending (newest first)
                java.util.Collections.sort(messages, (m1, m2) -> Long.compare(m2.timestamp, m1.timestamp));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    static class MessageItem {
        String userId, username, message, time;
        long timestamp;
        int unreadCount;

        MessageItem(String userId, String username, String message, String time, long timestamp, int unreadCount) {
            this.userId = userId;
            this.username = username;
            this.message = message;
            this.time = time;
            this.timestamp = timestamp;
            this.unreadCount = unreadCount;
        }
    }

    static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private final List<MessageItem> items;

        MessageAdapter(List<MessageItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMessageBinding b = ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(b);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MessageItem item = items.get(position);

            // Reset UI for recycled view
            holder.binding.tvUsername.setText("Loading...");
            holder.binding.ivUserAvatar.setImageResource(R.drawable.common_default_avatar_ic);

            // Fetch User Details
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users").child(item.userId)
                    .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                String avatar = snapshot.child("avtar").getValue(String.class);
                                holder.binding.tvUsername.setText(name);
                                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                                        .load(avatar)
                                        .placeholder(R.drawable.common_default_avatar_ic)
                                        .into(holder.binding.ivUserAvatar);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                    });

            holder.binding.tvLastMsg.setText(item.message);
            holder.binding.tvMsgTime.setText(item.time);

            // Click listener
            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("receiverId", item.userId);
                intent.putExtra("receiverName", holder.binding.tvUsername.getText().toString());
                v.getContext().startActivity(intent);
            });

            // Unread count
            if (item.unreadCount > 0) {
                holder.binding.unreadBadge.setVisibility(View.VISIBLE);
                holder.binding.unreadBadge.setText(String.valueOf(item.unreadCount));
                holder.binding.tvLastMsg.setTextColor(Color.WHITE);
                holder.binding.tvLastMsg.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                holder.binding.unreadBadge.setVisibility(View.GONE);
                holder.binding.tvLastMsg.setTextColor(Color.parseColor("#99FFFFFF"));
                holder.binding.tvLastMsg.setTypeface(null, android.graphics.Typeface.NORMAL);
            }

            // Animation
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationX(50f);
            holder.itemView.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(400)
                    .setStartDelay(position * 50L)
                    .start();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemMessageBinding binding;
            ViewHolder(ItemMessageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
