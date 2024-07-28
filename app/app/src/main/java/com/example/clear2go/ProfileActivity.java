package com.example.clear2go;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clear2go.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference avioaneData;
    private static final String TAG="GOOGLE_SIGN_IN_TAG";
    private ActivityProfileBinding binding;
    String selectedPlane;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        avioaneData = mDatabase.getDatabase().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane");
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        Spinner selectPlane = binding.spinner;
        selectPlane.setSaveEnabled(false);

        avioaneData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> airplanes = snapshot.getChildren();

                List<String> updatedAvioane = new ArrayList<>();

                for (DataSnapshot airplaneSnapshot : airplanes) {
                    String airplaneCode = airplaneSnapshot.getKey();
                    updatedAvioane.add(airplaneCode);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_item, updatedAvioane);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapter.notifyDataSetChanged();
                selectPlane.setAdapter(adapter);                    

                selectedPlane = (String) selectPlane.getSelectedItem();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: database error");
            }
        });



        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectPlane.getSelectedItem()!=null) {
                    Toast.makeText(ProfileActivity.this, selectPlane.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, FlyActivity.class);
                    intent.putExtra("avion", (String) selectPlane.getSelectedItem());
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ControlActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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
        /*
        if(item.getItemId()==R.id.User)
        {
            Intent intent = new Intent(ProfileActivity.this, User_properties.class);
            intent.putExtra("user", firebaseAuth.getCurrentUser());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

         */

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