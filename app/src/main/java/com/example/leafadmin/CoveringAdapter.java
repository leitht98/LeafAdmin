package com.example.leafadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CoveringAdapter extends RecyclerView.Adapter<CoveringAdapter.ViewHolder>{
    final private String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        //Add buttons to each project in the database to 'Update' and 'Delete'
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            //Add listener to 'Update' button and call function to launch new activity
            Button editButton = view.findViewById(R.id.editButton);
            editButton.setOnClickListener(v -> {
                String coveringData = textView.getText().toString();
                openEditActivity(coveringData, view);
            });

            //Add listener to 'Delete' button and call function to launch new activity
            Button deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                String coveringData = textView.getText().toString();
                openDeleteActivity(coveringData, view);
            });
        }

        public TextView getTextView() {return textView;}

        public void openEditActivity(String coveringData, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, EditCovering.class);
            intent.putExtra("covering_data",coveringData);
            context.startActivity(intent);
        }

        public void openDeleteActivity(String coveringData, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, DeleteCovering.class);
            intent.putExtra("covering_data",coveringData);
            context.startActivity(intent);
        }
    }

    //Constructor
    //Ideally this will use an ArrayList<Covering> in the future
    public CoveringAdapter(String[] dataSet) {localDataSet = dataSet;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if(!localDataSet[position].equals("")) {viewHolder.getTextView().setText(localDataSet[position]);}
    }

    @Override
    public int getItemCount() {return localDataSet.length;}
}
