package com.sp.auth.OktaOAuth2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
public class SimpleGet {
    public static void main(String[] args) {
        try {
            SimpleGet.call_me();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void call_me() throws Exception {
        String url = "https://dev-307684.oktapreview.com/oauth2/v1/keys?client_id=0oajl6fatuzs6g7eQ0h7";
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

        System.out.println("result after Reading JSON Response");
        System.out.println(myResponse.toMap());
    }
}