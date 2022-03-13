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
    private EditText enteredName, enteredRP1, enteredRP2;
    FirebaseFirestore db;
    String dataString;
    ArrayList<String> pesticideNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pesticide);

        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        String[] pesticidesData = dataStringTrim.split(getString(R.string.itemSplit));
        for(String i : pesticidesData) {
            String[] coveringValues = i.split(",");
            String name = coveringValues[0].split("=")[1].replace("}", "");
            pesticideNames.add(name);
        }

        db = FirebaseFirestore.getInstance();
        Button submit = findViewById(R.id.submitButton);
        Button back = findViewById(R.id.backButton);
        enteredName = findViewById(R.id.enterName);
        enteredRP1 = findViewById(R.id.enterRP1);
        enteredRP2 = findViewById(R.id.enterRP2);
        enteredName.setOnClickListener(v -> enteredName.getText().clear());
        enteredRP1.setOnClickListener(v -> enteredRP1.getText().clear());
        enteredRP2.setOnClickListener(v -> enteredRP2.getText().clear());

        submit.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                if (!pesticideNames.contains(enteredName.getText().toString())) {
                    try {
                        BigDecimal bdRP1 = new BigDecimal(enteredRP1.getText().toString());
                        BigDecimal bdRP2 = new BigDecimal(enteredRP2.getText().toString());
                        savePesticide(enteredName.getText().toString(), bdRP1, bdRP2);
                        pesticideNames.add(enteredName.getText().toString());
                    } catch (Exception e) {Toast.makeText(AddPesticide.this, "Regression Parameters must be numbers", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(AddPesticide.this, "That name is already taken, please use a different one.", Toast.LENGTH_SHORT).show();}
            } else {Toast.makeText(AddPesticide.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
        back.setOnClickListener(v -> this.finish());
    }

    private void savePesticide(String name, BigDecimal rp1, BigDecimal rp2){
        Map<String, Object> pesticideData = new HashMap<>();
        pesticideData.put("name",name);
        pesticideData.put("regression parameter 1",rp1.toString());
        pesticideData.put("regression parameter 2",rp2.toString());
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