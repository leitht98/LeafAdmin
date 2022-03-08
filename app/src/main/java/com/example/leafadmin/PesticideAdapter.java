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

public class PesticideAdapter extends RecyclerView.Adapter<PesticideAdapter.ViewHolder>{
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
                String pesticideData = textView.getText().toString();
                openEditActivity(pesticideData, view);
            });

            //Add listener to 'Delete' button and call function to launch new activity
            Button deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                String pesticideData = textView.getText().toString();
                openDeleteActivity(pesticideData, view);
            });
        }

        public TextView getTextView() {return textView;}

        public void openEditActivity(String pesticideData, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, EditPesticide.class);
            intent.putExtra("pesticide_data",pesticideData);
            context.startActivity(intent);
        }

        public void openDeleteActivity(String pesticideData, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, DeletePesticide.class);
            intent.putExtra("pesticide_data",pesticideData);
            context.startActivity(intent);
        }
    }

    public PesticideAdapter(String[] dataSet) { localDataSet = dataSet; }

    @NonNull
    @Override
    public PesticideAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new PesticideAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesticideAdapter.ViewHolder viewHolder, final int position) {
        if(!localDataSet[position].equals("")) {
            viewHolder.getTextView().setText(localDataSet[position]);
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}
