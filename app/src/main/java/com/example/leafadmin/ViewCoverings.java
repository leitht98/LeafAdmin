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

public class ViewCoverings extends AppCompatActivity {
    String dataString;
    Button back;
    ArrayList<Covering> coveringsObjectArray = new ArrayList<>();

    RecyclerView coveringDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_coverings);
        coveringDataList = findViewById(R.id.dataList);
        back = findViewById(R.id.backButton);
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        String[] coveringsData = dataStringTrim.split("\\}\\{");
        for(String i : coveringsData){
            String[] coveringValues = i.split(",");
            BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
            BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
            //Janky and took too long but it works! wooooooooooooooop. It's janky, but to stop them adding duplicates we could do this again.
            String name = coveringValues[2].split("=")[1].replace("}","");
            String id = coveringValues[3].split("=")[1];
            //I'm not sure this works? Like might be useless.
            boolean newCovering = true;
            for (Covering j: coveringsObjectArray){
                if (j.getCoveringName().equals(name)) {
                    newCovering = false;
                    break;
                }
            }
            if(newCovering) {coveringsObjectArray.add(new Covering(id, name,fen,rate));}
        }
        String[] dataInput = new String[coveringsObjectArray.size()];
        int iterator = 0;
        for(Covering i: coveringsObjectArray){
            String coveringItem = "";
            coveringItem += "Name: "+i.getCoveringName();
            coveringItem += "\nID: "+i.getCoveringID();
            coveringItem += "\nUV Fen: "+i.getUVFen().toString();
            coveringItem += "\nUV Rate: "+i.getUVRate().toString();
            //I really should have just copied it exactly and ignored all this Covering class crap
            dataInput[iterator] = coveringItem;
            iterator++;
        }

        CoveringAdapter coveringAdapter = new CoveringAdapter(dataInput);
        //add the adapter to the list thing
        coveringDataList.setAdapter(coveringAdapter);
        //make a layout manager and add it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        coveringDataList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(coveringDataList.getContext(), layoutManager.getOrientation());
        coveringDataList.addItemDecoration(dividerItemDecoration);

        back.setOnClickListener(v -> this.finish());
    }
}