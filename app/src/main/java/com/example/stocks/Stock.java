package com.example.stocks;

import java.util.ArrayList;

public class Stock {


    private String name;
    private String symbol;
    private double price;
    private double change;
    private double ratio;

    private String arrow;
    private static int counter = 1;

    ArrayList<String> Snames = new ArrayList<>();




    Stock(String name, String symbol, double price, double change, double ratio){

        this.name = name;
        this.symbol =symbol;
        this.price = price;
        this.change = change;
        this.ratio= ratio;

        counter++;

    }


    public String getName() {
        return name;
    }

    public double getPrice(){
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getChange() {
        return change;
    }

    public double getRatio() {
        return ratio;
    }

//    @Override
//    public String toString(){
//        return "hey you";
//    }
}
