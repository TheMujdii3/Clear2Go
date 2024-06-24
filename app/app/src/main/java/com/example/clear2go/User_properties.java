package com.example.clear2go;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clear2go.databinding.ActivityUserPropertiesBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class User_properties extends AppCompatActivity {
    private String user_mail;
    private String user_name;
    private boolean isAdmin,isPilot,isCz;
    private FirebaseFirestore firestore;
    private ActivityUserPropertiesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the user's email from the intent
        user_mail = getIntent().getStringExtra("user");
        binding = ActivityUserPropertiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        firestore = FirebaseFirestore.getInstance();
        binding.uMail.setText(user_mail);
        user_name = String.valueOf(binding.uName.getText());
        isAdmin = binding.switch1.isChecked();
        isPilot = binding.switch2.isChecked();
        isCz = binding.switch3.isChecked();
        if(isAdmin)
        {
            firestore.collection("/Avioane/AT Bucuresti/Users/Admin").add(user_mail);
        }
        if(isPilot)
        {
            firestore.collection("/Avioane/AT Bucuresti/Users/Pilot").add(user_mail);
        }
        if(isCz)
        {
            firestore.collection("/Avioane/AT Bucuresti/Users/Cz").add(user_mail);
        }
        /*
        bun aici daca totul merge bine trebuie sa mai verific caracteristicile userlui in profile activity si sa las ueser sa acceseze activitatile assignate lui
        trebuie sa facem desktop apu(cpp hopefully)
        trebuie sa fac inegrarea cu spreadsheets which sucks a lot yeay
         */

    }
}