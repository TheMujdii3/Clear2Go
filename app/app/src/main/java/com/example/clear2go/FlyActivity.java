package com.example.clear2go;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.clear2go.databinding.ActivityFlyBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FlyActivity extends AppCompatActivity implements LocationListener {
    private FirebaseAuth firebaseAuth;
    private TextView speedTextView;
    private TextView altitudeTextView;
    private static final int REQUEST_LOCATION_CODE = 1;
    private
    LocationManager locationManager;
    private SensorManager sensorManager;
    private float currentSpeed = 0.0f;
    private double currentAltitude = 0.0;

    ActivityFlyBinding binding;
    private FusedLocationProviderClient locationClient;
    private DatabaseReference mDatabase;
    private DatabaseReference avionData;
    private DatabaseReference rq;
    public boolean motor = false, rulaj = false, aliniat = false, decolat = false, aterizat = false;
    Sensor pressureSensor;
    String avion;
    Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent start = getIntent();
        avion = start.getStringExtra("avion");
        binding = ActivityFlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Request location updates
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);

            }else {
                // Request the ACCESS_FINE_LOCATION permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        rq = mDatabase.getDatabase().getReference().child("Requests");
        avionData = mDatabase.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion);
        
        binding.engOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.child(avion).child("Pornire motor").setValue(false);
                binding.engOn.setBackgroundColor(Color.parseColor("#FFA500"));
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Pornire motor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue(boolean.class) == true) {
                        binding.engOn.setBackgroundColor(-16711936);
                        binding.engOn.setActivated(true);
                        motor = true;
                        binding.taxi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rq.child(avion).child("Rulaj").setValue(false);
                                binding.taxi.setBackgroundColor(Color.parseColor("#FFA500"));
                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Rulaj").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.getValue(boolean.class) == true) {
                                        binding.taxi.setBackgroundColor(-16711936);
                                        binding.taxi.setActivated(true);
                                        rulaj = true;
                                        binding.lineIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                rq.child(avion).child("Intrare si aliniere").setValue(false);
                                                binding.lineIn.setBackgroundColor(Color.parseColor("#FFA500"));
                                            }
                                        });
                                        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Intrare si aliniere").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.getValue(boolean.class) == true) {
                                                        binding.lineIn.setBackgroundColor(-16711936);
                                                        binding.lineIn.setActivated(true);
                                                        aliniat = true;
                                                        binding.Takeoff.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                rq.child(avion).child("Plecare").setValue(false);
                                                                binding.Takeoff.setBackgroundColor(Color.parseColor("#FFA500"));
                                                            }
                                                        });
                                                        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Plecare").addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    if (snapshot.getValue(boolean.class) == true) {
                                                                        binding.Takeoff.setBackgroundColor(-16711936);
                                                                        decolat = true;
                                                                        binding.Takeoff.setActivated(true);
                                                                        binding.land.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                //rq.child(avion).setValue("Aterizare "+avion);
                                                                                rq.child(avion).child("Aterizare").setValue(false);
                                                                                binding.land.setBackgroundColor(Color.parseColor("#FFA500"));
                                                                            }
                                                                        });
                                                                        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Aterizare").addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    if (snapshot.getValue(boolean.class) == true) {
                                                                                        binding.land.setBackgroundColor(-16711936);
                                                                                        binding.land.setActivated(true);
                                                                                        aterizat = true;
                                                                                    } else {
                                                                                        binding.land.setActivated(false);
                                                                                        aterizat = false;
                                                                                        binding.land.setBackgroundColor(-65536);
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    } else {
                                                                        binding.Takeoff.setActivated(false);
                                                                        binding.Takeoff.setBackgroundColor(-65536);
                                                                        decolat = false;

                                                                        binding.land.setActivated(false);
                                                                        aterizat = false;
                                                                        binding.land.setBackgroundColor(-65536);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    } else {
                                                        binding.lineIn.setActivated(false);
                                                        aliniat = false;
                                                        binding.lineIn.setBackgroundColor(-65536);

                                                        binding.Takeoff.setActivated(false);
                                                        binding.Takeoff.setBackgroundColor(-65536);
                                                        decolat = false;

                                                        binding.land.setActivated(false);
                                                        aterizat = false;
                                                        binding.land.setBackgroundColor(-65536);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        binding.taxi.setBackgroundColor(-65536);
                                        binding.taxi.setActivated(false);
                                        rulaj = false;

                                        binding.lineIn.setActivated(false);
                                        aliniat = false;
                                        binding.lineIn.setBackgroundColor(-65536);

                                        binding.Takeoff.setActivated(false);
                                        binding.Takeoff.setBackgroundColor(-65536);
                                        decolat = false;

                                        binding.land.setActivated(false);
                                        aterizat = false;
                                        binding.land.setBackgroundColor(-65536);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        binding.engOn.setActivated(false);
                        motor = false;
                        binding.engOn.setBackgroundColor(-65536);

                        binding.taxi.setBackgroundColor(-65536);
                        binding.taxi.setActivated(false);
                        rulaj = false;

                        binding.lineIn.setActivated(false);
                        aliniat = false;
                        binding.lineIn.setBackgroundColor(-65536);

                        binding.Takeoff.setActivated(false);
                        binding.Takeoff.setBackgroundColor(-65536);
                        decolat = false;

                        binding.land.setActivated(false);
                        aterizat = false;
                        binding.land.setBackgroundColor(-65536);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        //binding.alti.setText((int) location.getAltitude());
        //Toast.makeText(this, "alti updated", Toast.LENGTH_SHORT).show();
        binding.alt.setText(String.valueOf((int)location.getAltitude()));
        binding.speed.setText(String.valueOf((int)location.getSpeed()));
    }
}