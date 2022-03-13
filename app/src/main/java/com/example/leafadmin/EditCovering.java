package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditCovering extends AppCompatActivity {
    String inputData;
    String name, coveringID, uvFen, uvRate;
    Button back, update;
    EditText currentName, currentUVFen, currentUVRate;
    FirebaseFirestore db;

    ArrayList<String> coveringNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_covering);

        //Access database
        db = FirebaseFirestore.getInstance();

        //If you're connected
        if(isNetworkAvailable()) {
            //Access the 'coverings' collection
            db.collection(getString(R.string.coverings_database_collection))
                    .get()
                    .addOnCompleteListener(task -> {
                        StringBuilder dataString = new StringBuilder();
                        if (task.isSuccessful()) {
                            //Store all covering names in an ArrayList
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                dataString.append(document.getData());
                                String[] tempDataArray = document.getData().toString().split(",");
                                coveringNames.add(tempDataArray[2].split("=")[1].replace("}",""));
                            }
                        //If you're not connected, send error message
                        } else {Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
                    });
        //Do not allow the activity to open if there is no network connection
        } else{
            Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        //Assign display items
        currentName = findViewById(R.id.enterNewName);
        currentUVFen = findViewById(R.id.enterNewUVFen);
        currentUVRate = findViewById(R.id.enterNewUVRate);
        //Make it so you can clear text boxes by tapping them
        currentName.setOnClickListener(v -> currentName.getText().clear());
        currentUVFen.setOnClickListener(v -> currentUVFen.getText().clear());
        currentUVRate.setOnClickListener(v -> currentUVRate.getText().clear());

        //Get data passed to activity as intent
        Intent intent = getIntent();
        inputData = intent.getStringExtra("covering_data");
        String[] tempPesticideDataArray = inputData.split("\n");
        //Store data as Strings
        name = tempPesticideDataArray[0].split(": ")[1];
        coveringID = tempPesticideDataArray[1].split(": ")[1];
        uvFen = tempPesticideDataArray[2].split(": ")[1];
        uvRate = tempPesticideDataArray[3].split(": ")[1];

        //Fill EditTexts with the existing name and values
        currentName.setText(name);
        currentUVFen.setText(uvFen);
        currentUVRate.setText(uvRate);

        //Buttons
        update = findViewById(R.id.updateButton);
        back = findViewById(R.id.backButton);

        //If the user decides to edit the covering
        update.setOnClickListener(v -> {
            //Check you're connected
            if(isNetworkAvailable()) {
                //Check the name isn't already in the list, unless it hasn't been changed in which case it's fine
                if (!coveringNames.contains(currentName.getText().toString()) || name.equals(currentName.getText().toString())) {
                    try {
                        //Check that the values are numbers - should be impossible for them not to be but just in case
                        new BigDecimal(currentUVFen.getText().toString());
                        new BigDecimal(currentUVRate.getText().toString());
                        //Store the new values
                        updateCovering();
                    //Error messages
                    } catch (Exception e) {Toast.makeText(EditCovering.this, "UV Fen and UV Rate must be numbers.", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(EditCovering.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        //If they choose not to, go back
        back.setOnClickListener(v -> this.finish());
    }

    public void updateCovering(){
        //Map to store Covering data as key-value pair
        Map<String, Object> coveringDataMap = new HashMap<>();
        //Add data
        coveringDataMap.put("name",currentName.getText().toString());
        coveringDataMap.put("UV Fen",currentUVFen.getText().toString());
        coveringDataMap.put("UV Rate",currentUVRate.getText().toString());
        //Send map to the coverings collection in the database, using the same ID so you overwrite it
        db.collection("coverings").
                document(coveringID).
                set(coveringDataMap).addOnSuccessListener(aVoid -> {
                    //Success message
                    Toast.makeText(EditCovering.this, "Covering updated.", Toast.LENGTH_SHORT).show();
                    //Restart the app, this is to avoid the unedited covering still appearing in the list if you just go back
                    Intent i = new Intent(EditCovering.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    EditCovering.this.startActivity(i);
                }).addOnFailureListener(e -> Toast.makeText(EditCovering.this, "Failed to update the covering", Toast.LENGTH_SHORT).show());
    }

    //Check if you're connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}