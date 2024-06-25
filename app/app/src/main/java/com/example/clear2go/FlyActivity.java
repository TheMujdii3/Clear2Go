package com.example.clear2go;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.clear2go.databinding.ActivityFlyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FlyActivity extends AppCompatActivity implements LocationListener {
    private FirebaseAuth firebaseAuth;
    private static final int REQUEST_LOCATION_CODE = 1;
    private LocationManager locationManager;
    ActivityFlyBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseReference avionData;
    private DatabaseReference rq;
    String avion;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent start = getIntent();
        avion = start.getStringExtra("avion");
        binding = ActivityFlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        rq = mDatabase.getDatabase().getReference().child("Requests");
        avionData = mDatabase.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion);

        Map<String,Object> comanda = new HashMap<>();

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

                                                                                    } else {
                                                                                        binding.land.setActivated(false);
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

                                                                        binding.land.setActivated(false);
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
                                                        binding.lineIn.setBackgroundColor(-65536);

                                                        binding.Takeoff.setActivated(false);
                                                        binding.Takeoff.setBackgroundColor(-65536);

                                                        binding.land.setActivated(false);
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

                                        binding.lineIn.setActivated(false);
                                        binding.lineIn.setBackgroundColor(-65536);

                                        binding.Takeoff.setActivated(false);
                                        binding.Takeoff.setBackgroundColor(-65536);

                                        binding.land.setActivated(false);
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
                        binding.engOn.setBackgroundColor(-65536);

                        binding.taxi.setBackgroundColor(-65536);
                        binding.taxi.setActivated(false);

                        binding.lineIn.setActivated(false);
                        binding.lineIn.setBackgroundColor(-65536);

                        binding.Takeoff.setActivated(false);
                        binding.Takeoff.setBackgroundColor(-65536);

                        binding.land.setActivated(false);
                        binding.land.setBackgroundColor(-65536);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(FlyActivity.this);
                //locationManager.removeUpdates(super.this);
                locationManager=null;
                mDatabase=null;
                FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Pornire motor")
                        .setValue(false);
                Intent intent = new Intent(FlyActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                FlyActivity.this.finish();
                try {
                    FlyActivity.this.finalize();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                try {
                    super.finalize();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                FlyActivity.super.finishActivity(1);
                FlyActivity.super.finish();

            }
        });
        getOnBackPressedDispatcher().
                addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/" + avion + "/Pornire motor")
                                .setValue(false);
                        locationManager.removeUpdates(FlyActivity.this);
                        locationManager=null;
                        mDatabase=null;
                        Intent intent = new Intent(FlyActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        binding.scrollingText.setSelected(true);
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Mesaj aeronave").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.scrollingText.setText(dataSnapshot.getValue().toString());
                binding.scrollingText.setSelected(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        binding.alt.setText(String.valueOf((int)((int)location.getAltitude()*3.28084))+"ft");
        binding.speed.setText(String.valueOf((int)location.getSpeed()*3600/1000)+"km/h");
        avionData.child("lat").setValue(location.getLatitude());
        avionData.child("lng").setValue(location.getLongitude());
        avionData.child("heading").setValue(location.getBearing());
    }
}