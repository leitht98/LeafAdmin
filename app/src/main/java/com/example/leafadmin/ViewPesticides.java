package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import java.math.BigDecimal;
import java.util.ArrayList;

//Comments virtually identical to those of ViewCovering
public class ViewPesticides extends AppCompatActivity {
    String dataString;
    Button back;
    ArrayList<Pesticide> pesticidesObjectArray = new ArrayList<>();

    RecyclerView pesticideDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pesticides);

        pesticideDataList = findViewById(R.id.dataList);
        back = findViewById(R.id.backButton);
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");

        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
        for(String i : coveringsData){
            String[] pesticideValues = i.split(", ");
            //BigDecimal rp1 = new BigDecimal(pesticideValues[1].split("=")[1]);
            //BigDecimal rp2 = new BigDecimal(pesticideValues[2].split("=")[1].replace("}",""));
            //String name = pesticideValues[0].split("=")[1];
            //String id = pesticideValues[3].split("=")[1];
            String name = "error", id = "error";
            BigDecimal rvp = BigDecimal.valueOf(0), rt = BigDecimal.valueOf(0), mm = BigDecimal.valueOf(0);

            for (String j : pesticideValues){
                System.out.println("£££"+j.replace("}",""));
                String[] labelDataPair = j.replace("}","").split("=");
                switch (labelDataPair[0]){
                    case "name": name = labelDataPair[1]; break;
                    case "reference vapour pressure": rvp = new BigDecimal(labelDataPair[1]); break;
                    case "reference temperature": rt = new BigDecimal(labelDataPair[1]); break;
                    case "molar mass": mm = new BigDecimal(labelDataPair[1]); break;
                    case "id": id = labelDataPair[1]; break;
                    default: break;
                }
            }

            boolean newCovering = true;
            for (Pesticide j: pesticidesObjectArray){
                if (j.getName().equals(name)) {
                    newCovering = false;
                    break;
                }
            }
            if(newCovering) {pesticidesObjectArray.add(new Pesticide(id, name,rvp,rt,mm));}
        }
        String[] dataInput = new String[pesticidesObjectArray.size()];
        int iterator = 0;
        for(Pesticide i: pesticidesObjectArray){
            String coveringItem = "";
            coveringItem += "Name: "+i.getName();
            coveringItem += "\nID: "+i.getPesticideID();
            coveringItem += "\nreference vapour pressure: "+i.getReferenceVapourPressure().toString();
            coveringItem += "\nreference temperature: "+i.getReferenceTemp().toString();
            coveringItem += "\nmolar mass: "+i.getMolarMass().toString();
            //I really should have just copied it exactly and ignored all this Covering class crap
            dataInput[iterator] = coveringItem;
            iterator++;
        }

        PesticideAdapter pesticideAdapter = new PesticideAdapter(dataInput);
        //add the adapter to the list thing
        pesticideDataList.setAdapter(pesticideAdapter);
        //make a layout manager and add it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pesticideDataList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(pesticideDataList.getContext(), layoutManager.getOrientation());
        pesticideDataList.addItemDecoration(dividerItemDecoration);

        back.setOnClickListener(v -> this.finish());
    }
}