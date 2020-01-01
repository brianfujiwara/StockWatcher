//Brian Obmalay Hw4
package com.example.stocks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener{

    private static final String website ="http://www.marketwatch.com/investing/stock/";


    private final ArrayList<Stock> StockList = new ArrayList<>();  // Main content is here
    private  ArrayList<Stock> tempList = new ArrayList<>();
    private RecyclerView recyclerView; // Layout's recyclerview

    private StockAdapter mAdapter; // Data to recyclerview adapter
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Stock Watch");


        recyclerView = findViewById(R.id.recycler);


        mAdapter = new StockAdapter(StockList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                Toast.makeText(getApplicationContext(), "refresh", Toast.LENGTH_SHORT).show();
                swiper.setRefreshing(false); // This stops the busy-circle
            }
        });

        loadFile();

        new AsyncLoader(this).execute();


    }

    public ArrayList<Stock> sortitOUt(){

            Collections.sort(StockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getSymbol().compareTo(o2.getSymbol());
                }


            });
            return StockList;
        }


    public void refresh(){

            if(doNetCheck()==true) {

                for (int i = 0; i < StockList.size(); i++) {
                    StockList.remove(i);
                    mAdapter.notifyItemRemoved(i);
                }

                tempList.clear();
                StockList.clear();
                mAdapter.notifyDataSetChanged();
                getData();

                for (Stock bo : tempList) {
                    String ght = bo.getSymbol();

                    addNew(ght);
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No NetWork Connection");
                builder.setMessage("Stocks cannot be updated without a network connection");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

    }

    private void getData() {


        try {
            InputStream is = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();



            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = jsonObject.getString("NAME");
                String symbol = jsonObject.getString("SYMBOL");
                Double price = jsonObject.getDouble("PRICE");
                Double change = jsonObject.getDouble("CHANGE");
                Double ratio = jsonObject.getDouble("RATIO");

                Stock p = new Stock(name, symbol, price, change, ratio);
                tempList.add(p);
            }




        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {


            return true;

        } else {

            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



            if (item.getItemId() == R.id.add) {

                if (doNetCheck()) {

                    Toast.makeText(this, "Add Stock", Toast.LENGTH_SHORT).show();


                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // Create an edittext and set it to be the builder's view
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                    // et.setInputType(InputType.TYPE_CLASS_TEXT);


                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);
                    
                    //builder.setIcon(R.drawable.icon1);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            String symbol = String.valueOf(et.getText());

                            ArrayList<String> brian = AsyncLoader.getCompany(symbol);

                            newdialog(brian);

                            //  tv1.setText(et.getText());
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();


                            //tv1.setText(R.string.no_way);
                        }
                    });

                    builder.setMessage("Please enter a stock symbol:");
                    builder.setTitle("Stock Selection");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("No NetWork Connection");
                    builder.setMessage("Stocks cannot be added without a network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }

                return true;

    }

    public void newdialog(ArrayList<String> brian){

        final CharSequence[] jojo = brian.toArray(new CharSequence[brian.size()]);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Make a Selection");


        builder.setItems(jojo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

               String jk = AsyncLoader.getSymbol((String) jojo[which]);
               // DataLoader.bn(jk);
                addNew(jk);

                    // addNew(jk);

                //new DataLoader(this).execute();

                    //DataLoader.bn(jojo[which]);
                    //tv2.setText(sArray[which]);

            }
        });


        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                //et1.setText("nevermind");
            }
        });

       AlertDialog dialog = builder.create();
         dialog.show();
    }

    public void duplicateS(Stock jk ){

        for(Stock d : StockList){
            if(d.equals(jk)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                /// String gh = String.format("Stock Symbol %s is already displayed", jk);

                builder.setTitle("Duplicate Stock");
                //builder.setMessage("kjkljlkjkljlk");
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }



    }


    public void addNew(String gh){


        new DataLoader(this).execute(gh);

    }

    @Override
    public void onClick(View v) {

        //int pos = recyclerView.getChildAdapterPosition(v);


        int pos = recyclerView.getChildLayoutPosition(v);
        Stock m = StockList.get(pos);
        String k = m.getSymbol();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(website+k));
        startActivity(i);
        Toast.makeText(v.getContext(), "this stock is " + m.toString(), Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onLongClick(View v) {


        pos = recyclerView.getChildLayoutPosition(v);

        Stock tk= StockList.get(pos);

        String symbol =tk.getSymbol();

        String bn = String.format("Delete Stock Symbol %s", symbol);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage(bn);
        builder.setIcon(R.drawable.baseline_delete_black_18dp);


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                remove();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();

                //finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();




        //return true;


        return false;
    }

    public void remove(){
        //int pos = recyclerView.getChildLayoutPosition(v);

        StockList.remove(pos);
        //recyclerView.removeViewAt(pos);
        mAdapter.notifyItemRemoved(pos);


    }

    private void loadFile() {


        try {
            InputStream is = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();



            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = jsonObject.getString("NAME");
                String symbol = jsonObject.getString("SYMBOL");
                Double price = jsonObject.getDouble("PRICE");
                Double change = jsonObject.getDouble("CHANGE");
                Double ratio = jsonObject.getDouble("RATIO");

                Stock p = new Stock(name, symbol, price, change, ratio);
                StockList.add(p);
            }

            //showProducts();


        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        try {
            saveProducts();
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            Toast.makeText(this, getString(R.string.not_saved), Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }


    private void saveProducts() throws IOException, JSONException {



        FileOutputStream fos = getApplicationContext().
                openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

        JSONArray jsonArray = new JSONArray();

        for (Stock p : StockList) {
            JSONObject prodJSON = new JSONObject();
            prodJSON.put("NAME", p.getName());
            prodJSON.put("SYMBOL", p.getSymbol());
            prodJSON.put("PRICE", p.getPrice());
            prodJSON.put("CHANGE", p.getChange());
            prodJSON.put("RATIO", p.getRatio());

            jsonArray.put(prodJSON);

        }

        String jsonText = jsonArray.toString();


        fos.write(jsonText.getBytes());
        fos.close();


    }

    public void updateData(ArrayList<Stock> cList) {
        //StockList.addAll(cList);

            StockList.add(cList.get(0));
            sortitOUt();
            mAdapter.notifyDataSetChanged();

            try {
                saveProducts();
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            } catch (IOException | JSONException e) {
                Toast.makeText(this, getString(R.string.not_saved), Toast.LENGTH_SHORT).show();
            }

    }


}



