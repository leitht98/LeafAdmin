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

//Comments virtually identical to those of EditCovering
public class EditPesticide extends AppCompatActivity {
    String inputData;
    String name, pesticideID, rvp, rt, mm;
    Button back, update;
    EditText currentName, currentRVP, currentRT, currentMM;
    FirebaseFirestore db;

    ArrayList<String> pesticideNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pesticide);

        db = FirebaseFirestore.getInstance();
        if(isNetworkAvailable()) {
            db.collection(getString(R.string.pesticides_database_collection))
                    .get()
                    .addOnCompleteListener(task -> {
                        StringBuilder dataString = new StringBuilder();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                dataString.append(document.getData());
                                //System.out.println(".................."+document.getData());
                                String[] tempDataArray = document.getData().toString().split(",");
                                //System.out.println(",,,,,,,,,,"+tempDataArray[0].split("=")[1]);
                                //pesticideNames.add(tempDataArray[0].split("=")[1]);
                                for(String i : tempDataArray){
                                    if(i.contains("name")){
                                        pesticideNames.add(i.split("=")[1]);
                                    }
                                }
                            }
                        } else {Toast.makeText(EditPesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
                    });
        } else{
            Toast.makeText(EditPesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        currentName = findViewById(R.id.enterNewName);
        currentRVP = findViewById(R.id.enterNewRVP);
        currentRT = findViewById(R.id.enterNewRT);
        currentMM = findViewById(R.id.enterNewMM);

        currentName.setOnClickListener(v -> currentName.getText().clear());
        currentRVP.setOnClickListener(v -> currentRVP.getText().clear());
        currentRT.setOnClickListener(v -> currentRT.getText().clear());
        currentMM.setOnClickListener(v -> currentMM.getText().clear());

        Intent intent = getIntent();
        inputData = intent.getStringExtra("pesticide_data");
        String[] tempPesticideDataArray = inputData.split("\n");
        //name = tempPesticideDataArray[0].split(": ")[1];
        //pesticideID = tempPesticideDataArray[1].split(": ")[1];
        //rp1 = tempPesticideDataArray[2].split(": ")[1];
        //rp2 = tempPesticideDataArray[3].split(": ")[1];
        for (String i : tempPesticideDataArray){
            String[] labelDataPair = i.split(": ");
            //System.out.println("$$$"+i);
            switch (labelDataPair[0]){
                case "Name": name = labelDataPair[1]; break;
                case "ID": pesticideID = labelDataPair[1]; break;
                case "reference vapour pressure": rvp = labelDataPair[1]; break;
                case "reference temperature": rt = labelDataPair[1]; break;
                case "molar mass": mm = labelDataPair[1]; break;
                default: break;
            }
        }

        currentName.setText(name);
        currentRVP.setText(rvp);
        currentRT.setText(rt);
        currentMM.setText(mm);

        update = findViewById(R.id.updateButton);
        back = findViewById(R.id.backButton);

        update.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                if (!pesticideNames.contains(currentName.getText().toString()) || name.equals(currentName.getText().toString())) {
                    try {
                        new BigDecimal(currentRVP.getText().toString());
                        new BigDecimal(currentRT.getText().toString());
                        new BigDecimal(currentMM.getText().toString());
                        updatePesticide();
                    } catch (Exception e) {Toast.makeText(EditPesticide.this, "Regression parameters must be numbers.", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(EditPesticide.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(EditPesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        back.setOnClickListener(v -> this.finish());
    }

    public void updatePesticide(){
        Map<String, Object> pesticideDataMap = new HashMap<>();
        pesticideDataMap.put("name",currentName.getText().toString());
        pesticideDataMap.put("reference vapour pressure", currentRVP.getText().toString());
        pesticideDataMap.put("reference temperature", currentRT.getText().toString());
        pesticideDataMap.put("molar mass", currentMM.getText().toString());
        db.collection("pesticides").
                document(pesticideID).
                set(pesticideDataMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditPesticide.this, "Pesticide updated.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditPesticide.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            EditPesticide.this.startActivity(i);
        }).addOnFailureListener(e -> Toast.makeText(EditPesticide.this, "Failed to update the pesticide", Toast.LENGTH_SHORT).show());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}