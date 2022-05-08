package com.project.caseManager.classes;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.caseManager.R;

import java.util.ArrayList;

//ListAdapter class is used to interact and inflate with RecyclerView item on DataActivty.
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<Patients> listdata;
    private int selectedPos = RecyclerView.NO_POSITION;
    public MyListAdapter(ArrayList<Patients> listdata) { //Constructor instantiating listdata.
        this.listdata = listdata;
    }


    @NonNull
    @Override
    public MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewdate) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.angel, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyListAdapter.ViewHolder viewHolder, int position) {
        viewHolder.itemView.setBackgroundColor(selectedPos == position ? Color.GRAY : Color.WHITE); //If item is selected, mark it with gray color, else unmark it(change colour to white)
        final Patients myListData = listdata.get(position);
        viewHolder.fullName.setText(""+myListData.fullname);
        viewHolder.address.setText(myListData.address);
        viewHolder.date.setText(myListData.date);
        viewHolder.itemView.setOnClickListener(view -> {
            notifyItemChanged(selectedPos);
            selectedPos = viewHolder.getLayoutPosition();
            notifyItemChanged(selectedPos);
            Log.i("Selected: ", ""+selectedPos);
        });
    }



    @Override
    public int getItemCount() {
        return listdata.size();
    } //get the size of the ArrayList.
    //Create the ViewHolder item containing a row's contents
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName;
        public TextView address;
        public TextView date;
        public LinearLayout linearl;
        public ViewHolder(View itemView) {
            super(itemView);
            this.fullName = (TextView) itemView.findViewById(R.id.fullName);
            this.address = (TextView) itemView.findViewById(R.id.address);
            this.date = (TextView) itemView.findViewById(R.id.date);
            linearl = (LinearLayout)itemView.findViewById(R.id.angie);
        }
    }

    //Get the current PositionIndex of the RecyclerView
    public int getSelectedPos() {
        //System.out.println(selectedPos);
        return selectedPos;
    }

    //Gets the selected row's Patient id
    public int getId(){
        //System.out.println("Id: " + listdata.get(selectedPos).id);
        return listdata.get(selectedPos).id;
    }


    //Removes a selected row from RecyclerView through ArrayList
    public void removeItem() {
        try{
            //System.out.println("ID: " + listdata.get(selectedPos));
            listdata.remove(selectedPos);
            notifyItemRemoved(selectedPos);
            selectedPos = RecyclerView.NO_POSITION;
        }catch(Exception e) {
            System.out.println("Inconsistency Detected!");
        }
    }

    //Returns the Patient ArrayList
    public ArrayList<Patients> getPatients(){
        return listdata;
    }

}
