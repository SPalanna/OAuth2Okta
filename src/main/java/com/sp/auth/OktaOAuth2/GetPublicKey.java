package com.sp.auth.OktaOAuth2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
public class GetPublicKey {

    public static JSONArray getKeyFromServer(String keyURL, String clientID) throws Exception {
        String url = keyURL + "?client_id=" + clientID;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print in String
        System.out.println(response.toString());
        //Read JSON response and print
        //JSONObject myResponse = new JSONObject(response.toString());


        JSONObject myResponse = new JSONObject(response.toString());
        //String pageName = myResponse.getJSONObject("keys").getString("kid");

        JSONArray arr = myResponse.getJSONArray("keys");
        for (int i = 0; i < arr.length(); i++)
        {
            String key = arr.getJSONObject(i).getString("kid");
            System.out.println(key);
        }
        System.out.println(myResponse.toMap());
        return arr;
    }
}