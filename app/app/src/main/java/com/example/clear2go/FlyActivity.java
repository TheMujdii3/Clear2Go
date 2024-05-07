package com.example.clear2go;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clear2go.databinding.ActivityFlyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class FlyActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private FirebaseAuth firebaseAuth;
    private TextView speedTextView;
    private TextView altitudeTextView;
    private
    LocationManager locationManager;
    private SensorManager sensorManager;
    private float currentSpeed = 0.0f;
    private float currentAltitude = 0.0f;

    ActivityFlyBinding binding;

    private DatabaseReference mDatabase;
    private DatabaseReference avionData;
    private DatabaseReference rq;
    public boolean motor=false,rulaj=false,aliniat=false,decolat=false,aterizat=false;
    Sensor pressureSensor ;
    String avion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent start=getIntent();
        avion=start.getStringExtra("avion");
        binding=ActivityFlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor= sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        rq=mDatabase.getDatabase().getReference().child("Requests");
        avionData = mDatabase.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion);
        binding.engOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.child(avion).child("Pornire motor").setValue(false);
                binding.engOn.setBackgroundColor( Color.parseColor("#FFA500"));
            }
        });
        binding.taxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.child(avion).child("Rulaj").setValue(false);
                binding.taxi.setBackgroundColor(Color.parseColor("#FFA500"));
            }
        });
        binding.lineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.child(avion).child("Intrare si aliniere").setValue(false);
                binding.lineIn.setBackgroundColor( Color.parseColor("#FFA500"));
            }
        });
        binding.Takeoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rq.child(avion).child("Plecare").setValue(false);
                binding.Takeoff.setBackgroundColor(Color.parseColor("#FFA500"));
            }
        });
        binding.land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rq.child(avion).setValue("Aterizare "+avion);
                rq.child(avion).child("Aterizare").setValue(false);
                binding.land.setBackgroundColor(Color.parseColor("#FFA500"));
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion+"/Rulaj").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.getValue(boolean.class)==true) {
                        binding.taxi.setBackgroundColor(-16711936);
                        binding.taxi.setActivated(true);
                        rulaj=true;
                        //Toast.makeText(FlyActivity.this,"merge",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        binding.taxi.setBackgroundColor(-65536);
                        binding.taxi.setActivated(false);
                        rulaj=false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion+"/Pornire motor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.getValue(boolean.class)==true) {
                        binding.engOn.setBackgroundColor(-16711936);
                        binding.engOn.setActivated(true);
                        motor=true;
                    }
                    else {
                        binding.engOn.setActivated(false);
                        motor=false;
                        binding.engOn.setBackgroundColor(-65536);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion+"/Intrare si aliniere").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.getValue(boolean.class)==true) {
                        binding.lineIn.setBackgroundColor(-16711936);
                        //Toast.makeText(FlyActivity.this,"merge",Toast.LENGTH_SHORT).show();
                        binding.lineIn.setActivated(true);
                        aliniat=true;
                    }
                    else{
                        binding.lineIn.setActivated(false);
                        aliniat=false;
                        binding.lineIn.setBackgroundColor(-65536);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion+"/Plecare").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.getValue(boolean.class)==true ) {
                        binding.Takeoff.setBackgroundColor(-16711936);
                        decolat=true;
                        binding.Takeoff.setActivated(true);
                        //Toast.makeText(FlyActivity.this,"merge",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        binding.Takeoff.setActivated(false);
                        binding.Takeoff.setBackgroundColor(-65536);
                        decolat=false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+avion+"/Aterizare").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.getValue(boolean.class)==true ) {
                        binding.land.setBackgroundColor(-16711936);
                        binding.land.setActivated(true);
                        aterizat=true;
                        //Toast.makeText(FlyActivity.this,"merge",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        binding.land.setActivated(false);
                        aterizat=false;
                        binding.land.setBackgroundColor(-65536);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    double hpaToInHg(float value)
    {
        return (29.92*(value/1013.2));
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            binding.altM.setText(String.format("%.3f mbar", (29.92-hpaToInHg(values[0])*1000)));//values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}