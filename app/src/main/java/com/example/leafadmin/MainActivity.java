package com.example.leafadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private Button addCovering, addPesticide, viewCoverings, viewPesticides;
    ArrayList<Covering> coveringsObjectArray = new ArrayList<>();
    ArrayList<Pesticide> pesticidesObjectArray = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCovering = findViewById(R.id.addCoveringButton);
        addPesticide = findViewById(R.id.addPesticideButton);
        viewCoverings = findViewById(R.id.viewCoveringsButton);
        viewPesticides = findViewById(R.id.viewPesticidesButton);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //This doesn't seem to work, let's try it another time.
        /*
        ExecutorService coveringsService = Executors.newSingleThreadExecutor();
        Future<ArrayList<Covering>> future = coveringsService.submit(new getCoveringsAsFuture());
        try{
            future.get();
            Toast.makeText(MainActivity.this, "!!!!!!!!!", Toast.LENGTH_SHORT).show();
            coveringsObjectArray.add(new Covering("a",BigDecimal.valueOf(3),BigDecimal.valueOf(4)));
            //Toast.makeText(MainActivity.this, coveringsObjectArray.size(), Toast.LENGTH_SHORT).show();
            for(Covering i: coveringsObjectArray){
                Toast.makeText(MainActivity.this, i.getCoveringName(), Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        addCovering.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "Add covering", Toast.LENGTH_SHORT).show();
            //ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            //if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                    //connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
            if(isNetworkAvailable()){
                db.collection("coverings")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                //Start new activity, saving the project with DataLog, which is the database view
                                //Copy this section to open the loading page
                                //System.out.println("@@@@@@@@@@@@@@@@@"+dataString);
                                openCoveringActivity(dataString.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            }
        });
        addPesticide.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "Add pesticide", Toast.LENGTH_SHORT).show();

            //ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(isNetworkAvailable()) {
                db.collection("pesticides")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openPesticideActivity(dataString.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            }

        });
        viewCoverings.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "View coverings", Toast.LENGTH_SHORT).show();
            //ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(isNetworkAvailable()) {
                db.collection("coverings")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openViewCoveringsActivity(dataString.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            }
        });
        viewPesticides.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "View pesticides", Toast.LENGTH_SHORT).show();
            //ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(isNetworkAvailable()) {
                db.collection("pesticides")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId()).append("}");
                                }
                                openViewPesticidesActivity(dataString.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openCoveringActivity(String dataString){
        Intent intent = new Intent(this, AddCovering.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openPesticideActivity(String dataString){
        Intent intent = new Intent(this, AddPesticide.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openViewCoveringsActivity(String dataString){
        Intent intent = new Intent(this, ViewCoverings.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    public void openViewPesticidesActivity(String dataString){
        Intent intent = new Intent(this, ViewPesticides.class);
        intent.putExtra("data_string", dataString);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /*
    class getCoveringsAsFuture implements Callable<ArrayList<Covering>>{

        @Override
        public ArrayList<Covering> call() throws Exception {
            ArrayList<Covering> tempCoveringArray = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
                db.collection("coverings")
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                }
                                String dataStringTrim = dataString.substring(1,dataString.length()-1);
                                String[] coveringsData = dataStringTrim.split("\\}\\{");
                                for(String i : coveringsData){
                                    //System.out.println("individual data: "+i);
                                    String[] coveringValues = i.split(",");
                                    System.out.println("UV Rate: "+coveringValues[0].split("=")[1]+"\nUV Fen: "+coveringValues[1].split("=")[1]+"\nName: "+coveringValues[2].split("=")[1]);
                                    BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
                                    BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
                                    String name = coveringValues[2].split("=")[1];
                                    //I'm not sure this works? Like might be useless.
                                    boolean newCovering = true;
                                    for (Covering j: coveringsObjectArray){
                                        if(j.getCoveringName().equals(name)){
                                            newCovering = false;
                                        }
                                    }
                                    if(newCovering) {
                                        coveringsObjectArray.add(new Covering(id,name,fen,rate));
                                    }
                                }
                            } else {
                                System.out.println("Fail at check 1");
                            }
                        });
            } else{
                System.out.println("Fail at check 2");
            }
            return tempCoveringArray;
        }
    }*/
}