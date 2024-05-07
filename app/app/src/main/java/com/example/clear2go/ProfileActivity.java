package com.example.clear2go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clear2go.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference avioaneData;
    private static final String TAG="GOOGLE_SIGN_IN_TAG";
    private ActivityProfileBinding binding;
    String[] avioane ={"YR-5659","YR-PBJ","YR-5600","YR-5657"};
    String selectedPlane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        avioaneData = mDatabase.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane");
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        Spinner selectPlane = binding.spinner;
        avioaneData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get all airplanes as a HashMap
                Iterable<DataSnapshot> airplanes = snapshot.getChildren();

                // Create a new String array to store airplane codes
                List<String> updatedAvioane = new ArrayList<>();
                for (DataSnapshot airplaneSnapshot : airplanes) {
                    String airplaneCode = airplaneSnapshot.getKey();
                    updatedAvioane.add(airplaneCode);
                }

                // Update the adapter with the new data
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_item, updatedAvioane);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectPlane.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: database error");
            }
        });
        selectPlane.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlane = (String) parent.getItemAtPosition(position);
                // Do something with the selectedName
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection case (optional)
            }
        });


        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this,"merge avionu..."+selectPlane.getSelectedItem(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, FlyActivity.class);
                intent.putExtra("avion",selectedPlane);
                startActivity(intent);
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ControlActivity.class));
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.idk1) {
            firebaseAuth.signOut();
            checkUser();
        }

        return true;
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }


    }


}