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
        String[] coveringsData = dataStringTrim.split("\\}\\{");
        for(String i : coveringsData){
            String[] pesticideValues = i.split(",");
            BigDecimal rp1 = new BigDecimal(pesticideValues[1].split("=")[1]);
            BigDecimal rp2 = new BigDecimal(pesticideValues[2].split("=")[1].replace("}",""));
            String name = pesticideValues[0].split("=")[1];
            String id = pesticideValues[3].split("=")[1];
            boolean newCovering = true;
            for (Pesticide j: pesticidesObjectArray){
                if (j.getName().equals(name)) {
                    newCovering = false;
                    break;
                }
            }
            if(newCovering) {pesticidesObjectArray.add(new Pesticide(id, name,rp1,rp2));}
        }
        String[] dataInput = new String[pesticidesObjectArray.size()];
        int iterator = 0;
        for(Pesticide i: pesticidesObjectArray){
            String coveringItem = "";
            coveringItem += "Name: "+i.getName();
            coveringItem += "\nID: "+i.getPesticideID();
            coveringItem += "\nRP1: "+i.getRParam1().toString();
            coveringItem += "\nRP2: "+i.getRParam2().toString();
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