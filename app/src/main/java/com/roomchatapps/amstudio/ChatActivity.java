package com.roomchatapps.amstudio;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomchatapps.amstudio.adapters.ChatAdapter;
import com.roomchatapps.amstudio.databinding.ActivityChatBinding;
import com.roomchatapps.amstudio.models.ChatMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private String receiverId;
    private String senderId;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        receiverId = getIntent().getStringExtra("receiverId");
        String receiverName = getIntent().getStringExtra("receiverName");
        senderId = FirebaseAuth.getInstance().getUid();

        binding.tvChatUserName.setText(receiverName);
        binding.ivBack.setOnClickListener(v -> finish());

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChatMessages.setAdapter(chatAdapter);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        loadMessages();

        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatMessage chat = ds.getValue(ChatMessage.class);
                    if (chat != null && 
                        ((chat.getSenderId().equals(senderId) && chat.getReceiverId().equals(receiverId)) ||
                         (chat.getSenderId().equals(receiverId) && chat.getReceiverId().equals(senderId)))) {
                        chatMessages.add(chat);
                    }
                }
                Collections.sort(chatMessages, (c1, c2) -> Long.compare(c1.getTimestamp(), c2.getTimestamp()));
                chatAdapter.notifyDataSetChanged();
                if (chatMessages.size() > 0) {
                    binding.rvChatMessages.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String msg = binding.etMessage.getText().toString().trim();
        if (!msg.isEmpty()) {
            String msgId = chatRef.push().getKey();
            long time = System.currentTimeMillis();
            ChatMessage chatMessage = new ChatMessage(senderId, receiverId, msg, time);
            if (msgId != null) {
                chatRef.child(msgId).setValue(chatMessage);

                // Update RecentChats for sender
                DatabaseReference senderRecentRef = FirebaseDatabase.getInstance().getReference("RecentChats")
                        .child(senderId).child(receiverId);
                senderRecentRef.setValue(chatMessage);

                // Update RecentChats for receiver
                DatabaseReference receiverRecentRef = FirebaseDatabase.getInstance().getReference("RecentChats")
                        .child(receiverId).child(senderId);
                receiverRecentRef.setValue(chatMessage);

                binding.etMessage.setText("");
            }
        }
    }
}
