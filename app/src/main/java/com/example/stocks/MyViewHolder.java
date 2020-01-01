package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    TextView symbol;
    TextView name;
    TextView price;
    TextView ratio;
    TextView change;

    TextView Arrow;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        symbol = itemView.findViewById(R.id.id );
        price = itemView.findViewById(R.id.price);
        change = itemView.findViewById(R.id.change);
        ratio = itemView.findViewById(R.id.ratio);
        Arrow = itemView.findViewById(R.id.arrow);
    }
}
