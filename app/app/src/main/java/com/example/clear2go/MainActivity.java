package com.example.clear2go;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.clear2go.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private static final int RC_SIGN_IN  =100;
    private static final String TAG="GOOGLE_SIGN_IN_TAG";
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        checkuser();
        FirebaseApp.initializeApp(this);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("messages");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: begin google sigh in");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });

    }

    private void checkuser()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,ProfileActivity.class));
            Log.d(TAG, "checkuser: already signed in");
            finish();
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==RC_SIGN_IN)
        {
            Log.d(TAG, "onActivityResult: google sighin intent result");
            Task<GoogleSignInAccount> accountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAcc(account);
            }
            catch (Exception e)
            {
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }

    }

    private void firebaseAuthWithGoogleAcc(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAcc: begin firebase auth with google acc");
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: logged in");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        Log.d(TAG, "onSuccess: Email"+email);
                        Log.d(TAG, "onSuccess: uid"+uid);
                        if(authResult.getAdditionalUserInfo().isNewUser())
                        {
                            Log.d(TAG, "onSuccess: account created...\n"+email);
                            Toast.makeText(MainActivity.this,"Accout created...\n"+email,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "onSuccess: existing user");
                            Toast.makeText(MainActivity.this,"existing user...\n"+email,Toast.LENGTH_SHORT).show();

                        }
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: loggin failed");
                    }
                });
    }

    public void writeMessageToDatabase(DatabaseReference databaseRef, String message) {
        // Set a new value for the "messages" node in the database
        databaseRef.setValue(message);
    }
}