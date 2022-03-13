package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCovering extends AppCompatActivity {
    private EditText enteredName, enteredUVFen, enteredUVRate;
    FirebaseFirestore db;
    String dataString;
    ArrayList<String> coveringNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_covering);

        //Get data passed to activity as intent
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        //Trim
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        //Split by "}{" to get individual coverings
        String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
        for(String i : coveringsData) {
            //Split each covering into its attributes
            String[] coveringValues = i.split(",");
            //Take the name
            String name = coveringValues[2].split("=")[1].replace("}", "");
            //Add it to the list of existing covering names
            coveringNames.add(name);
        }
        //Initialise database
        db = FirebaseFirestore.getInstance();
        //Assign display items
        Button submit = findViewById(R.id.submitButton);
        Button back = findViewById(R.id.backButton);
        enteredName = findViewById(R.id.enterName);
        enteredUVFen = findViewById(R.id.enterUVFen);
        enteredUVRate = findViewById(R.id.enterUVRate);
        //Make it so you can clear text boxes by tapping them
        enteredName.setOnClickListener(v -> enteredName.getText().clear());
        enteredUVFen.setOnClickListener(v -> enteredUVFen.getText().clear());
        enteredUVRate.setOnClickListener(v -> enteredUVRate.getText().clear());

        //Save covering button
        submit.setOnClickListener(v -> {
            //Check that you're connected
            if(isNetworkAvailable()) {
                //Check that the name isn't taken
                if (!coveringNames.contains(enteredName.getText().toString())) {
                    try {
                        //Convert number inputs to BigDecimals
                        BigDecimal bdUVFen = new BigDecimal(enteredUVFen.getText().toString());
                        BigDecimal bdUVRate = new BigDecimal(enteredUVRate.getText().toString());
                        //Send inputs to saveCovering()
                        saveCovering(enteredName.getText().toString(), bdUVFen, bdUVRate);
                        //Add the name to the list of existing names to prevent the same name being added if 'Submit' is hit again
                        coveringNames.add(enteredName.getText().toString());
                    //Error messages
                    } catch (Exception e) {Toast.makeText(AddCovering.this, "UV Fen and UV Rate must be numbers", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(AddCovering.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(AddCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        //Close this activity to go back
        back.setOnClickListener(v -> this.finish());
    }

    //Send Covering data to the database
    private void saveCovering(String name, BigDecimal uvFen, BigDecimal uvRate){
        //Map to store Covering data as key-value pair
        Map<String, Object> coveringData = new HashMap<>();
        //Add data
        coveringData.put("name",name);
        coveringData.put("UV Fen",uvFen.toString());
        coveringData.put("UV Rate",uvRate.toString());
        //Send map to the coverings collection in the database
        db.collection(getString(R.string.coverings_database_collection)).
                document().
                set(coveringData);
        //Success message
        Toast.makeText(AddCovering.this, "Covering saved.", Toast.LENGTH_SHORT).show();
    }

    //Check if you're connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}