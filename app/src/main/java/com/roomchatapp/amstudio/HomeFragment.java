package com.roomchatapp.amstudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvRooms;
    private ProgressBar progressBar;
    private TextView tvNoRooms;
    private RoomAdapter roomAdapter;
    private List<RoomModel> roomList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRooms = view.findViewById(R.id.rvRooms);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoRooms = view.findViewById(R.id.tvNoRooms);

        setupImageSlider(view);
        setupRecyclerView();
        loadRoomsFromFirebase();
        setupClickListeners(view);

        return view;
    }

    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://i.ibb.co/Q7dp3r5h/IMG-20260705-WA0006.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/Z6hm47RW/IMG-20260705-WA0005.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void setupRecyclerView() {
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(getContext(), roomList);
        rvRooms.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvRooms.setAdapter(roomAdapter);
    }

    private void loadRoomsFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        RoomModel room = ds.getValue(RoomModel.class);
                        if (room != null) roomList.add(room);
                    }
                }
                progressBar.setVisibility(View.GONE);
                tvNoRooms.setVisibility(roomList.isEmpty() ? View.VISIBLE : View.GONE);
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.ivAdd).setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateRoomActivity.class)));
        view.findViewById(R.id.ivSearch).setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
    }
}
