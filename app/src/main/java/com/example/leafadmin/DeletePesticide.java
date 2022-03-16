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

//Comments virtually identical to those of DeleteCovering
//I really think this could be one class...
public class DeletePesticide extends AppCompatActivity {
    Button noButton, yesButton;
    TextView pesticideData;
    String inputData;
    String pesticideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pesticide);

        pesticideData = findViewById(R.id.pesticide_data);
        noButton = findViewById(R.id.noButton);
        yesButton = findViewById(R.id.yesButton);

        Intent intent = getIntent();
        inputData = intent.getStringExtra("pesticide_data");
        String[] tempArrayToFindID = inputData.split("\n");
        //pesticideID = tempArrayToFindID[1].split(": ")[1];
        for(String i : tempArrayToFindID){
            if(i.contains("ID")){
                pesticideID = i.split(": ")[1];
            }
        }

        if(!isNetworkAvailable()){
            Toast.makeText(DeletePesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        pesticideData.setText(inputData);

        yesButton.setOnClickListener(v -> {
            if(isNetworkAvailable()){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(getString(R.string.pesticides_database_collection)).
                        document(pesticideID)
                        .delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(DeletePesticide.this, "Pesticide has been deleted.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DeletePesticide.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    DeletePesticide.this.startActivity(i);
                }).addOnFailureListener(e -> Toast.makeText(DeletePesticide.this, "Failed to delete the pesticide", Toast.LENGTH_SHORT).show());
            } else {Toast.makeText(DeletePesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        noButton.setOnClickListener(v -> this.finish());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}