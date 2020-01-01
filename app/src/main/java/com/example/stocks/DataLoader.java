package com.example.stocks;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataLoader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "AsyncLoaderTask";
    private MainActivity mainActivity;

    private static final String web = "https://cloud.iexapis.com/stable/stock/";
    private static final String token  = "sk_123a2e3f973a43f7bcdec67825cf2cea";

    DataLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }




    public static void bn(String k){


    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);


        ArrayList<Stock> countryList = parseJSON(s);
        if (countryList != null)

            Log.d(TAG, "onPostExecute: " + countryList);
        mainActivity.updateData(countryList);
    }

    @Override
    protected String doInBackground(String... strings) {


        Uri.Builder buildURL = Uri.parse(web).buildUpon();


        buildURL.appendPath(strings[0]);
        buildURL.appendPath("quote");
        buildURL.appendQueryParameter("token", token);

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

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
        return sb.toString();
       // parseJSON(sb.toString());



    }


    private ArrayList<Stock> parseJSON(String s) {


        ArrayList<Stock> rawList = new ArrayList<>();
        try {
            //JSONArray jObjMain = new JSONArray(s);
            JSONObject jObjMain = new JSONObject(s);
            for (int i = 0; i < jObjMain.length(); i++) {
                //JSONObject jCountry = (JSONObject) jObjMain.get(i);
                String symbol = jObjMain.getString("symbol");
                String name = jObjMain.getString("companyName");
                Double price = jObjMain.getDouble("latestPrice");
                Double change = jObjMain.getDouble("change");
                Double ratio = jObjMain.getDouble("changePercent");


                rawList.add(
                        new Stock(name, symbol, price, change, ratio));


            }
            Log.d(TAG, "list: " + rawList);
            return rawList;

        }catch (Exception e) {


            e.printStackTrace();
        }
        return null;
    }
}
