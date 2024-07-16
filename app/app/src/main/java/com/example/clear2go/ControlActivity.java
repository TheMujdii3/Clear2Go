package com.example.clear2go;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.analytics.*;
import com.example.clear2go.databinding.ActivityControlBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference mData;
    private FirebaseFirestore db;
    private DatabaseReference requests;
    private DatabaseReference positions;
    FirebaseAnalytics mFirebaseAnalytics;
    ActivityControlBinding binding;
    RecyclerView recyclerView;
    private controlRecycleAdapter adapter;
    GoogleMap myMap;
    Button denyBt;
    private Map<String, Marker> planeMarkers = new HashMap<>();
    private class obj{
        LatLng latLng;
        double heading;
        String plane;
        public obj(LatLng latLng,double heading,String plane) {
            this.latLng = latLng;this.plane=plane;this.heading=heading;
        }
    }
    private boolean isMapExpanded = false;
    private Map<String,obj> planesMap;
    private Map<String, GroundOverlay> planeOverlays = new HashMap<>();
    ConstraintLayout.LayoutParams mapParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mData= FirebaseDatabase.getInstance().getReference();
        requests=mData.getDatabase().getReference().child("Requests");
        positions=mData.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapParams= (ConstraintLayout.LayoutParams) binding.map.getLayoutParams();
        FirebaseAnalytics.getInstance(this);
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
                if (isMapExpanded) {
                    TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new ChangeBounds());
                    binding.map.setLayoutParams(mapParams);
                    binding.mapB.setActivated(false);
                } else {
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                    );
                    TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new ChangeBounds());
                    binding.map.setLayoutParams(layoutParams);
                    binding.mapB.setActivated(true);
                }
                isMapExpanded = !isMapExpanded;
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
                                                (Double) planeSnapshot.child("lng").getValue()),planeSnapshot.child("heading").getValue(Double.class),
                                        planeSnapshot.getKey())
                        );
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getOnBackPressedDispatcher().
                addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent intent = new Intent(ControlActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });

        objUpdateThread.start();
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
    private void updateObjectPositions1() {

        positions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot planeSnapshot : snapshot.getChildren()) {
                        String planeKey = planeSnapshot.getKey();
                        double lat = planeSnapshot.child("lat").getValue(Double.class);
                        double lng = planeSnapshot.child("lng").getValue(Double.class);
                        double heading = planeSnapshot.child("heading").getValue(Double.class);
                        LatLng newPosition = new LatLng(lat, lng);

                        obj object = planesMap.get(planeKey);
                        if (object != null) {
                            GroundOverlay overlay = planeOverlays.get(planeKey);
                            if (overlay != null) {
                                LatLng oldPosition = overlay.getPosition();
                                float oldRotation = overlay.getBearing();
                                if (!newPosition.equals(oldPosition) || oldRotation != (float) heading) {
                                    animateOverlay(overlay, newPosition, (float) heading, planeMarkers.get(planeKey));
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
    private void animateOverlay(final GroundOverlay overlay, final LatLng toPosition, final float toRotation,final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * overlay.getPosition().longitude;
                double lat = t * toPosition.latitude + (1 - t) * overlay.getPosition().latitude;
                float rotation = t * toRotation + (1 - t) * overlay.getBearing();
                overlay.setPosition(new LatLng(lat, lng));
                overlay.setBearing(rotation);
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
    private void animateMarker(final Marker marker, final LatLng toPosition,final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * marker.getPosition().longitude;
                double lat = t * toPosition.latitude + (1 - t) * marker.getPosition().latitude;
                float rotation = (t * toRotation  + (1 - t) * marker.getRotation());
                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
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


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.plane2, new BitmapFactory.Options() {{
            inSampleSize = 1;
        }});
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        for (obj plane : planesMap.values()) {
            LatLng location = new LatLng(plane.latLng.latitude, plane.latLng.longitude);
            GroundOverlay overlay = myMap.addGroundOverlay(new GroundOverlayOptions()
                    .position(location, 15000f, 15000f)
                    .image(icon)
                    .bearing((float) plane.heading)

            );
            planeOverlays.put(plane.plane, overlay);

            Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap2);
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.plane2), 25, 25, null);

            BitmapDescriptor icon2 = BitmapDescriptorFactory.fromBitmap(bitmap2);
            Marker marker1 = myMap.addMarker(new AdvancedMarkerOptions()
                    .position(location)
                    .flat(true)
                    .icon(icon2)
                    .visible(true)
                    .title(plane.plane.toString()));
            planeMarkers.put(plane.plane.toString(),marker1);
        }
        myMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                float targetWidth = 10000 * (float) Math.pow(2, 10 - cameraPosition.zoom);
                float targetHeight = 10000 * (float) Math.pow(2, 10 - cameraPosition.zoom);

                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                animator.setDuration(300);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        for (GroundOverlay overlay : planeOverlays.values()) {
                            float currentWidth = overlay.getWidth() + (targetWidth - overlay.getWidth()) * fraction;
                            float currentHeight = overlay.getHeight() + (targetHeight - overlay.getHeight()) * fraction;
                            overlay.setDimensions(currentWidth, currentHeight);
                        }
                    }
                });
                animator.start();
            }
        });


    }



}