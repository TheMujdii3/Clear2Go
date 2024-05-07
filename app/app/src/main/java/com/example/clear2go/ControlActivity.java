package com.example.clear2go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.clear2go.databinding.ActivityControlBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlActivity extends AppCompatActivity  {
    private DatabaseReference mData;
    private DatabaseReference requests;
    ActivityControlBinding binding;
    RecyclerView recyclerView;
    private HashMap<String, Request> requestsMap;
    private controlRecycleAdapter adapter;
    private ArrayList<Request> da;

    Button denyBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mData= FirebaseDatabase.getInstance().getReference();
        requests=mData.getDatabase().getReference().child("Requests");

        recyclerView = findViewById(R.id.recycler); // Assuming recycler view ID
        denyBt = findViewById(R.id.denyRq);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new controlRecycleAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        fetchRequests();

    }

    private void fetchRequests() {
        requests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Request> requests1 = new ArrayList<>();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot requestSnapshot2:requestSnapshot.getChildren()){
                        String requestValue = requestSnapshot2.getKey().toString();
                        String requestId = requestSnapshot.getKey();
                        // Assuming "requester" field doesn't exist in your data structure (modify if needed)
                        String requester = ""; // Modify to extract requester information
                        Request request = new Request(requestValue, requestId);
                        requests1.add(request);

                    }
                }

                // Update adapter with retrieved requests
                adapter.updateDataSet(requests1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }

        });





/*
private void fetchRequests() {
    requests.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            requestsMap.clear(); // Clear existing data before populating
            for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                String requestValueString = requestSnapshot.getValue().toString();

                // Split request value into individual items (assuming comma separation)
                String[] requestValues = requestValueString.split(",");

                for (String requestValue : requestValues) {
                    String requestId = requestSnapshot.getKey(); // Assuming key represents request ID
                    Request request = new Request(requestValue.trim().trim(), ""); // Modify if requester info exists

                    requestsMap.put(requestId + "_" + requestValue.trim(), request); // Create unique key with separator
                }
            }

            // Update adapter with retrieved requests (HashMap)
            adapter.updateDataSet(requestsMap);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle any errors
        }
    });

     */
    }
}