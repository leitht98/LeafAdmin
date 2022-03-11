package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addCovering = findViewById(R.id.addCoveringButton);
        Button addPesticide = findViewById(R.id.addPesticideButton);
        Button viewCoverings = findViewById(R.id.viewCoveringsButton);
        Button viewPesticides = findViewById(R.id.viewPesticidesButton);

        btnSignIn = findViewById(R.id.btnSignIn);
        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

        signOutButton = findViewById(R.id.signOutButton);
        //signOutButton.setVisibility(View.GONE);

        addCovering.setOnClickListener(v -> {
            if(isNetworkAvailable()){
                db.collection("coverings")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openCoveringActivity(dataString.toString());
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        addPesticide.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                db.collection("pesticides")
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
        viewCoverings.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                db.collection("coverings")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openViewCoveringsActivity(dataString.toString());
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        viewPesticides.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                db.collection("pesticides")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openViewPesticidesActivity(dataString.toString());
                            } else {Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        btnSignIn.setOnClickListener(v -> {
            if(isNetworkAvailable()){
                signIn();
                signOutButton.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.GONE);
            } else {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.revokeAccess();
            Toast.makeText(MainActivity.this, "Signed out.", Toast.LENGTH_SHORT).show();
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            //Toast.makeText(MainActivity.this, "Welcome back "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            currentUser.getDisplayName();
            signOutButton.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
        } catch (Exception e){
            //Toast.makeText(MainActivity.this, "Welcome, please sign in to continue", Toast.LENGTH_SHORT).show();
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    public void openCoveringActivity(String dataString){
        Intent intent = new Intent(this, AddCovering.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openPesticideActivity(String dataString){
        Intent intent = new Intent(this, AddPesticide.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openViewCoveringsActivity(String dataString){
        Intent intent = new Intent(this, ViewCoverings.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openViewPesticidesActivity(String dataString){
        Intent intent = new Intent(this, ViewPesticides.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void requestGoogleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                //This seems to work, but I like the old one. I just hate the error message
                .requestIdToken("794829267701-bba4q60202usjbbkk7hfldd3v24n60p6.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

                //This would be where you stored user data?
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                        .edit();
                editor.putString("username", account.getDisplayName());
                //So it does work, but maybe it's not being stored by this preference thing?
                System.out.println(account.getDisplayName());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(MainActivity.this,"Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information;
                        FirebaseUser user = mAuth.getCurrentUser();
                        //This could be where you call a new activity
                    } else {
                        //Toast.makeText(MainActivity.this,"failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}