package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteCovering extends AppCompatActivity {
    Button noButton, yesButton;
    TextView coveringData;
    String inputData;
    String coveringID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_covering);
        coveringData = findViewById(R.id.covering_data);
        noButton = findViewById(R.id.noButton);
        yesButton = findViewById(R.id.yesButton);

        Intent intent = getIntent();
        inputData = intent.getStringExtra("covering_data");
        String[] tempArrayToFindID = inputData.split("\n");
        coveringID = tempArrayToFindID[1].split(": ")[1];
        if(!isNetworkAvailable()){
            Toast.makeText(DeleteCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        coveringData.setText(inputData);

        yesButton.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("coverings").
                        document(coveringID)
                        .delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(DeleteCovering.this, "Covering has been deleted.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DeleteCovering.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    DeleteCovering.this.startActivity(i);
                }).addOnFailureListener(e -> Toast.makeText(DeleteCovering.this, "Failed to delete the covering", Toast.LENGTH_SHORT).show());
            } else{Toast.makeText(DeleteCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        noButton.setOnClickListener(v -> this.finish());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}