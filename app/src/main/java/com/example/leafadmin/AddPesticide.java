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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Comments virtually identical to those of AddCovering
//Really, this should be one activity with slightly different looks
//I think if you pass a couple extra Strings and do a couple .setTexts(), it can be one class
//But don't do this yet as they may diverge as you get more data for each thing
public class AddPesticide extends AppCompatActivity {
    private EditText enteredName, enteredRVP, enteredRT, enteredMM;
    FirebaseFirestore db;
    String dataString;
    ArrayList<String> pesticideNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pesticide);

        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        System.out.println("%%%"+dataString);
        //I don't think I need this??
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        //String dataStringTrim = dataString;
        String[] pesticidesData = dataStringTrim.split(getString(R.string.itemSplit));
        for(String i : pesticidesData) {
            String[] pesticideValues = i.split(",");
            //String name = coveringValues[0].split("=")[1].replace("}", "");
            //pesticideNames.add(name);
            for(String j : pesticideValues){
                if(j.contains("name")){
                    String name =j.split("=")[1].replace("}","");
                    pesticideNames.add(name);
                    //System.out.println("£££"+name);
                }
            }
        }

        db = FirebaseFirestore.getInstance();
        Button submit = findViewById(R.id.submitButton);
        Button back = findViewById(R.id.backButton);
        enteredName = findViewById(R.id.enterName);
        enteredRVP = findViewById(R.id.enterRVP);
        enteredRT = findViewById(R.id.enterRT);
        enteredMM = findViewById(R.id.enterMolarMass);
        enteredName.setOnClickListener(v -> enteredName.getText().clear());
        enteredRVP.setOnClickListener(v -> enteredRVP.getText().clear());
        enteredRT.setOnClickListener(v -> enteredRT.getText().clear());
        enteredMM.setOnClickListener(v -> enteredMM.getText().clear());

        submit.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                if (!pesticideNames.contains(enteredName.getText().toString())) {
                    try {
                        BigDecimal bdRVP = new BigDecimal(enteredRVP.getText().toString());
                        BigDecimal bdRT = new BigDecimal(enteredRT.getText().toString());
                        BigDecimal bdMM = new BigDecimal(enteredMM.getText().toString());
                        savePesticide(enteredName.getText().toString(), bdRVP, bdRT, bdMM);
                        pesticideNames.add(enteredName.getText().toString());
                    } catch (Exception e) {Toast.makeText(AddPesticide.this, "Regression Parameters must be numbers", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(AddPesticide.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else {Toast.makeText(AddPesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        back.setOnClickListener(v -> this.finish());
    }

    private void savePesticide(String name, BigDecimal rvp, BigDecimal rt, BigDecimal mm){
        Map<String, Object> pesticideData = new HashMap<>();
        pesticideData.put("name",name);
        pesticideData.put("reference vapour pressure",rvp.toString());
        pesticideData.put("reference temperature",rt.toString());
        pesticideData.put("molar mass",mm.toString());
        db.collection(getString(R.string.pesticides_database_collection)).
                document().
                set(pesticideData);
        Toast.makeText(AddPesticide.this, "Pesticide saved.", Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}