package com.example.clear2go;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clear2go.databinding.ActivityControlBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControlActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference mData;
    private DatabaseReference requests;
    private DatabaseReference positions;
    ActivityControlBinding binding;
    RecyclerView recyclerView;
    private HashMap<String, Request> requestsMap;
    private controlRecycleAdapter adapter;
    private ArrayList<Request> da;
    GoogleMap myMap;
    Button denyBt;
    List<objectOnMap> planes;
    ConstraintLayout.LayoutParams mapParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mData= FirebaseDatabase.getInstance().getReference();
        requests=mData.getDatabase().getReference().child("Requests");
        positions=mData.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapParams= (ConstraintLayout.LayoutParams) binding.map.getLayoutParams();

        recyclerView = findViewById(R.id.recycler);
        denyBt = findViewById(R.id.denyRq);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new controlRecycleAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        planes=new ArrayList<>();

        fetchRequests();
        binding.mapB.setActivated(false);
        binding.mapB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.mapB.isActivated()) {
                    binding.map.setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                    ));
                    binding.mapB.setActivated(false);
                }
                else {
                    binding.map.setLayoutParams(mapParams);
                    binding.mapB.setActivated(true);
                }
            }
        });

        positions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren())
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })

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
                        String requester = "";
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

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.addCircle(new CircleOptions().center(new LatLng(44.360977,25.934749)).radius(6000));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.360977,25.934749),10));



    }
}