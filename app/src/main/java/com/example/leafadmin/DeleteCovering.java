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
        //Assign display items
        coveringData = findViewById(R.id.covering_data);
        noButton = findViewById(R.id.noButton);
        yesButton = findViewById(R.id.yesButton);

        //Get data passed to activity as intent
        Intent intent = getIntent();
        inputData = intent.getStringExtra("covering_data");
        String[] tempArrayToFindID = inputData.split("\n");
        coveringID = tempArrayToFindID[1].split(": ")[1];
        //Do not allow the activity to open if there is no network connection
        if(!isNetworkAvailable()){
            //Error message
            Toast.makeText(DeleteCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        //Display the covering to be deleted
        coveringData.setText(inputData);

        //If they want to delete the covering
        yesButton.setOnClickListener(v -> {
            //Check you're connected
            if(isNetworkAvailable()) {
                //Access database
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //Go to 'coverings' collection
                db.collection(getString(R.string.coverings_database_collection)).
                        //Get the selected covering
                        document(coveringID)
                        //Delete it
                        .delete().addOnSuccessListener(aVoid -> {
                    //Let the user know
                    Toast.makeText(DeleteCovering.this, "Covering has been deleted.", Toast.LENGTH_SHORT).show();
                    //Restart the app, this is to avoid the deleted covering still appearing in the list if you just go back
                    Intent i = new Intent(DeleteCovering.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    DeleteCovering.this.startActivity(i);
                //Error messages
                }).addOnFailureListener(e -> Toast.makeText(DeleteCovering.this, "Failed to delete the covering", Toast.LENGTH_SHORT).show());
            } else{Toast.makeText(DeleteCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        //If they choose not to delete the covering, go back to the previous activity
        noButton.setOnClickListener(v -> this.finish());
    }

    //Check if you're connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}