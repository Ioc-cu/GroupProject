package com.metropolitan.internetcall.classes;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.metropolitan.internetcall.R;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<Patients> listdata;
    private int selectedPos = RecyclerView.NO_POSITION;

    // RecyclerView recyclerView;
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
        viewHolder.itemView.setBackgroundColor(selectedPos == position ? Color.GRAY : Color.WHITE);

        final Patients myListData = listdata.get(position);
        viewHolder.fullName.setText(""+myListData.fullname);
        viewHolder.address.setText(myListData.address);
        viewHolder.date.setText(myListData.date);

        viewHolder.itemView.setOnClickListener(view -> {

            notifyItemChanged(selectedPos);
            selectedPos = viewHolder.getLayoutPosition();
            notifyItemChanged(selectedPos);
            Toast.makeText(view.getContext(),"click on item: "+myListData.fullname,Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


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

}
