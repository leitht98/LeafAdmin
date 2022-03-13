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
        //Assign display items
        coveringDataList = findViewById(R.id.dataList);
        back = findViewById(R.id.backButton);
        //Get data passed to activity as intent
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String dataStringTrim = dataString.substring(1,dataString.length()-1);
        String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
        //Store Coverings in ArrayList
        for(String i : coveringsData){
            String[] coveringValues = i.split(",");
            BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
            BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
            String name = coveringValues[2].split("=")[1].replace("}","");
            String id = coveringValues[3].split("=")[1];
            //Check that you haven't already added the Covering
            boolean newCovering = true;
            for (Covering j: coveringsObjectArray){
                if (j.getCoveringName().equals(name)) {
                    newCovering = false;
                    break;
                }
            }
            //If you haven't add a new Covering to the ArrayList
            if(newCovering) {coveringsObjectArray.add(new Covering(id, name,fen,rate));}
        }

        //String array for displaying Covering data
        String[] dataInput = new String[coveringsObjectArray.size()];
        int iterator = 0;
        for(Covering i: coveringsObjectArray){
            String coveringItem = "";
            coveringItem += "Name: "+i.getCoveringName();
            coveringItem += "\nID: "+i.getCoveringID();
            coveringItem += "\nUV Fen: "+i.getUVFen().toString();
            coveringItem += "\nUV Rate: "+i.getUVRate().toString();
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

        //Close the activity if the user wants to go back
        back.setOnClickListener(v -> this.finish());
    }
}