package com.example.stocks;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.RequiresApi;

public class AsyncLoader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "API_AsyncTask";

    private MainActivity mainActivity;

   // ArrayList<String> namesList = new ArrayList<>();
    private static HashMap<String, String> NameData = new HashMap<>();
    private static final String stockURL = "https://api.iextrading.com/1.0/ref-data/symbols";

    AsyncLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    //@RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<String> getCompany(String co){

       ArrayList<String> namesList = new ArrayList<>();

        for ( HashMap.Entry<String,String> entry : NameData.entrySet()){

            if (entry.toString().contains(co)){
               // namesList.add(entry.toString());
                namesList.add(entry.getValue() +"-"+ entry.getKey());
            }

        }


        return namesList;
    }

    public static String getSymbol(String ok){

        String[] val = ok.split("-");

        return val[0];
    }


    @Override
    protected void onPostExecute(String s) {

    }

    @Override
    protected String doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(stockURL).buildUpon();

        String urlToUse = buildURL.build().toString();


        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {

            return null;
        }

        parseJSON(sb.toString());


        return null;
    }


    private void parseJSON(String s) {

        try {
            //JSONObject jObjMain = new JSONObject(s);

            //String symbol =  jObjMain.get("Symbol").toString();
            //Log.d(TAG, "doInBackground: " + symbol);

            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jCountry = (JSONObject) jObjMain.get(i);
                String name = jCountry.getString("symbol");
                String symbol = jCountry.getString("name");
                NameData.put(symbol,name);


            }
            Log.d(TAG, "doInBackground: " + NameData);
        }
            catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

