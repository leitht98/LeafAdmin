package com.example.leafadmin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SignInButton btnSignIn;
    Button signOutButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Assign display items
        Button addCovering = findViewById(R.id.addCoveringButton);
        Button addPesticide = findViewById(R.id.addPesticideButton);
        Button viewCoverings = findViewById(R.id.viewCoveringsButton);
        Button viewPesticides = findViewById(R.id.viewPesticidesButton);
        btnSignIn = findViewById(R.id.btnSignIn);
        signOutButton = findViewById(R.id.signOutButton);
        //Sign in on launch
        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

        //Add covering
        addCovering.setOnClickListener(v -> {
            //Check you're connected
            if(isNetworkAvailable()){
                //Access coverings collection
                db.collection(getString(R.string.coverings_database_collection))
                        .get()
                        .addOnCompleteListener(task -> {
                            //Store all covering data as a String
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                //Start AddCovering activity, giving it the covering data
                                openCoveringActivity(dataString.toString());
                            //Error messages
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        //As with adding a covering
        addPesticide.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                db.collection(getString(R.string.pesticides_database_collection))
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openPesticideActivity(dataString.toString());
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        //View coverings
        viewCoverings.setOnClickListener(v -> {
            //Check you're connected
            if(isNetworkAvailable()) {
                //Access coverings collection
                db.collection(getString(R.string.coverings_database_collection))
                        .get()
                        .addOnCompleteListener(task -> {
                            //Store all covering data as a String
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                //Start ViewCoverings activity, giving it the covering data
                                openViewCoveringsActivity(dataString.toString());
                            //Error messages
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        //As with viewing coverings
        viewPesticides.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                db.collection(getString(R.string.pesticides_database_collection))
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openViewPesticidesActivity(dataString.toString());
                            //Error messages
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        //Sign in button
        btnSignIn.setOnClickListener(v -> {
            //Make sure you're connected
            if(isNetworkAvailable()){
                signIn();
                //Show sign out button and hide sign in
                signOutButton.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.GONE);
            //Error message
            } else {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        //Sign out
        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            //Stop the auto login
            mGoogleSignInClient.revokeAccess();
            //Message to let them know they've signed out
            Toast.makeText(MainActivity.this, "Signed out.", Toast.LENGTH_SHORT).show();
            //Hide sign out and show sign in
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        });

        //More sign in stuff
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                            //Success message
                            Toast.makeText(MainActivity.this,"Sign in successful.", Toast.LENGTH_SHORT).show();
                        //Failure message
                        } catch (ApiException e) {Toast.makeText(MainActivity.this,"Sign in failed.", Toast.LENGTH_SHORT).show();}
                    }
                });
    }

    //Check if the user is already signed in
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Toast.makeText(MainActivity.this, "Welcome back "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        //If they are,
        if (currentUser != null) {
            //Hide sign in button and show sign out
            signOutButton.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
        //If they aren't
        } else {
            //Show sign in button and hide sign out
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    //Start AddCovering activity
    public void openCoveringActivity(String dataString){
        Intent intent = new Intent(this, AddCovering.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    //Start AddPesticide activity
    public void openPesticideActivity(String dataString){
        Intent intent = new Intent(this, AddPesticide.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    //Start ViewCoverings activity
    public void openViewCoveringsActivity(String dataString){
        Intent intent = new Intent(this, ViewCoverings.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    //Start View Pesticides activity
    public void openViewPesticidesActivity(String dataString){
        Intent intent = new Intent(this, ViewPesticides.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    //Check if you're connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //Sign in stuff...
    private void requestGoogleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                //This seems to work, but I like the old one. I just hate the error message
                .requestIdToken(getString(R.string.id_token_for_gso))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential);
    }
}