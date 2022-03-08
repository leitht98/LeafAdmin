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

        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        String[] coveringsData = dataStringTrim.split("\\}\\{");
        for(String i : coveringsData) {
            String[] coveringValues = i.split(",");
            String name = coveringValues[2].split("=")[1].replace("}", "");
            coveringNames.add(name);
        }
        db = FirebaseFirestore.getInstance();
        Button submit = findViewById(R.id.submitButton);
        Button back = findViewById(R.id.backButton);
        enteredName = findViewById(R.id.enterName);
        enteredUVFen = findViewById(R.id.enterUVFen);
        enteredUVRate = findViewById(R.id.enterUVRate);
        submit.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                if (!coveringNames.contains(enteredName.getText().toString())) {
                    try {
                        BigDecimal bdUVFen = new BigDecimal(enteredUVFen.getText().toString());
                        BigDecimal bdUVRate = new BigDecimal(enteredUVRate.getText().toString());
                        saveCovering(enteredName.getText().toString(), bdUVFen, bdUVRate);
                        coveringNames.add(enteredName.getText().toString());
                    } catch (Exception e) {Toast.makeText(AddCovering.this, "UV Fen and UV Rate must be numbers", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(AddCovering.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(AddCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        back.setOnClickListener(v -> this.finish());
    }

    private void saveCovering(String name, BigDecimal uvFen, BigDecimal uvRate){
        Map<String, Object> coveringData = new HashMap<>();
        coveringData.put("name",name);
        coveringData.put("UV Fen",uvFen.toString());
        coveringData.put("UV Rate",uvRate.toString());
        db.collection("coverings").
                document().
                set(coveringData);
        Toast.makeText(AddCovering.this, "Covering saved.", Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}