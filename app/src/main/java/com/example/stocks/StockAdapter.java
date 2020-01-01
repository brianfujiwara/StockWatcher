package com.example.stocks;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder>  {

    private ArrayList<Stock> aList;
    private MainActivity mainActivity;


    StockAdapter(ArrayList<Stock> list, MainActivity mainActivity) {
        aList = list;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_view, parent, false);

            itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Stock stock = aList.get(position);




        Double ra = stock.getRatio() * 100;
        String ti = String.format("(%.2f%%)",ra);
        //String per = Double.toString(ra);
        //String jk = String.format("(%s%%)",per);

        holder.change.setText(String.valueOf(stock.getChange()));
        holder.name.setText(stock.getName());
        holder.symbol.setText(stock.getSymbol());
        holder.price.setText(String.valueOf(stock.getPrice()));
        holder.ratio.setText(ti);
        holder.Arrow.setText("▲");

        if ( stock.getChange()<0){
            holder.change.setTextColor(Color.parseColor("#FF0000"));
            holder.ratio.setTextColor(Color.parseColor("#FF0000"));
            holder.price.setTextColor(Color.parseColor("#FF0000"));
            holder.symbol.setTextColor(Color.parseColor("#FF0000"));
            holder.name.setTextColor(Color.parseColor("#FF0000"));
            holder.Arrow.setText("▼");
            holder.Arrow.setTextColor(Color.parseColor("#FF0000"));
        }


    }

    @Override
    public int getItemCount() {
        return aList.size();
    }
}
