package com.example.clear2go;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Marker> planeMarkers = new HashMap<>();
    private class obj{
        LatLng latLng;
        String plane;
        public obj(LatLng latLng,String plane) {
            this.latLng = latLng;this.plane=plane;
        }
    }
    private Map<String,obj> planesMap;
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

        planesMap=new HashMap<>();

        fetchRequests();
        binding.mapB.setActivated(true);
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
                if(snapshot.exists())
                    for(DataSnapshot planeSnapshot:snapshot.getChildren()) {
                        planesMap.put(planeSnapshot.getKey(),
                                new obj(
                                        new LatLng((Double) planeSnapshot.child("lat").getValue(),
                                                (Double) planeSnapshot.child("lng").getValue()),
                                        planeSnapshot.getKey())
                        );
                        //Log.d("pos changed", "onDataChange: s a schimbat poziti unui avion "+planeSnapshot.getKey()+planesMap.size());
                        /*planesMap.values().stream().filter(plane -> snapshot.getKey() == plane.plane.toString()).forEach(plane -> plane.latLng = new LatLng((Double) planeSnapshot.child("lat").getValue(),
                                (Double) planeSnapshot.child("lng")
                                        .getValue()));

                         */
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        objUpdateThread.start();
    }
    private void updateObjectPositions() {
        for (obj object : planesMap.values()) {
            Marker marker = planeMarkers.get(object.plane.toString());

            positions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        for (DataSnapshot planeSnapshot : snapshot.getChildren()) {
                            planesMap.put(planeSnapshot.getKey(),
                                    new obj(
                                            new LatLng((Double) planeSnapshot.child("lat").getValue(),
                                                    (Double) planeSnapshot.child("lng").getValue()),
                                            planeSnapshot.getKey())
                            );
                            if (planeSnapshot.getKey().toString() == object.plane.toString()) {
                                if (marker.getPosition() != (new LatLng((Double) planeSnapshot.child("lat").getValue(), (Double) planeSnapshot.child("lng").getValue()))) {
                                    marker.setPosition(new LatLng((Double) planeSnapshot.child("lat").getValue(),
                                            (Double) planeSnapshot.child("lng").getValue()));
                                }
                            }
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //}
        }

    }
    Thread ObjUpdate = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                updateObjectPositions();
                try {
                    Thread.sleep(250); // Update positions every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private void updateObjectPositions1() {
        positions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot planeSnapshot : snapshot.getChildren()) {
                        String planeKey = planeSnapshot.getKey();
                        double lat = planeSnapshot.child("lat").getValue(Double.class);
                        double lng = planeSnapshot.child("lng").getValue(Double.class);
                        LatLng newPosition = new LatLng(lat, lng);

                        obj object = planesMap.get(planeKey);
                        if (object != null) {
                            Marker marker = planeMarkers.get(planeKey);
                            if (marker != null) {
                                LatLng oldPosition = marker.getPosition();
                                if (!newPosition.equals(oldPosition)) {
                                   animateMarker(marker,newPosition);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }
    Thread objUpdateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                long startTime = System.currentTimeMillis();

                updateObjectPositions1();

                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = Math.max(0, 500 - elapsedTime); // Ensure actualization every half a second
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500; // Animation duration in milliseconds

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * marker.getPosition().longitude;
                double lat = t * toPosition.latitude + (1 - t) * marker.getPosition().latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later (60 frames per second)
                    handler.postDelayed(this, 16);
                }
            }
        });
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

                adapter.updateDataSet(requests1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.addCircle(new CircleOptions()
                .center(new LatLng(44.360977, 25.934749))
                .radius(6000)
        );
        myMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(44.360977, 25.934749), 10)
        );

        for (obj plane : planesMap.values()) {
            LatLng location = new LatLng(plane.latLng.latitude, plane.latLng.longitude);
            Marker marker = myMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(plane.plane.toString())
            );
            planeMarkers.put(plane.plane.toString(), marker);
            marker.setTag(planesMap.get(marker.getId()));
        }

    }



}



