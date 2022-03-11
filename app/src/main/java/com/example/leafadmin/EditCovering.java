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

        db = FirebaseFirestore.getInstance();

        if(isNetworkAvailable()) {
            db.collection("coverings")
                    .get()
                    .addOnCompleteListener(task -> {
                        StringBuilder dataString = new StringBuilder();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                dataString.append(document.getData());
                                String[] tempDataArray = document.getData().toString().split(",");
                                coveringNames.add(tempDataArray[2].split("=")[1].replace("}",""));
                            }
                        } else {Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
                    });
        } else{
            Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        currentName = findViewById(R.id.enterNewName);
        currentUVFen = findViewById(R.id.enterNewUVFen);
        currentUVRate = findViewById(R.id.enterNewUVRate);
        currentName.setOnClickListener(v -> currentName.getText().clear());
        currentUVFen.setOnClickListener(v -> currentUVFen.getText().clear());
        currentUVRate.setOnClickListener(v -> currentUVRate.getText().clear());

        Intent intent = getIntent();
        inputData = intent.getStringExtra("covering_data");
        String[] tempPesticideDataArray = inputData.split("\n");
        name = tempPesticideDataArray[0].split(": ")[1];
        coveringID = tempPesticideDataArray[1].split(": ")[1];
        uvFen = tempPesticideDataArray[2].split(": ")[1];
        uvRate = tempPesticideDataArray[3].split(": ")[1];

        currentName.setText(name);
        currentUVFen.setText(uvFen);
        currentUVRate.setText(uvRate);

        update = findViewById(R.id.updateButton);
        back = findViewById(R.id.backButton);

        update.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                if (!coveringNames.contains(currentName.getText().toString()) || name.equals(currentName.getText().toString())) {
                    try {
                        new BigDecimal(currentUVFen.getText().toString());
                        new BigDecimal(currentUVRate.getText().toString());
                        updateCovering();
                    } catch (Exception e) {Toast.makeText(EditCovering.this, "UV Fen and UV Rate must be numbers.", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(EditCovering.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(EditCovering.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        back.setOnClickListener(v -> this.finish());
    }

    public void updateCovering(){
        Map<String, Object> coveringDataMap = new HashMap<>();
        coveringDataMap.put("name",currentName.getText().toString());
        coveringDataMap.put("UV Fen",currentUVFen.getText().toString());
        coveringDataMap.put("UV Rate",currentUVRate.getText().toString());
        db.collection("coverings").
                document(coveringID).
                set(coveringDataMap).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditCovering.this, "Covering updated.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EditCovering.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    EditCovering.this.startActivity(i);
                }).addOnFailureListener(e -> Toast.makeText(EditCovering.this, "Failed to update the covering", Toast.LENGTH_SHORT).show());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}