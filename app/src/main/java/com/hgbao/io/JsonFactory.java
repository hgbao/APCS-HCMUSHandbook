package com.hgbao.io;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonFactory {

    /**
     * Read data from a url
     */
    private static String readTextJSon(String url){
        String json = "";
        try {
            URL u = new URL(url);
            InputStreamReader reader = new InputStreamReader(u.openStream(), "UTF-8");
            BufferedReader br = new BufferedReader(reader);

            String line = br.readLine();
            StringBuilder strBuilder = new StringBuilder();
            while (line != null){
                strBuilder.append(line);
                line = br.readLine();
            }

            br.close();
            reader.close();
            json = strBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject getJsonObjectFromURL(String url){
        String jsonData = readTextJSon(url);
        return parseJsonObject(jsonData);
    }

    public static JSONArray getJsonArrayFromURL(String url){
        String jsonData = readTextJSon(url);
        return parseJsonArray(url);
    }

    public static JSONObject parseJsonObject(String jsonData){
        try {
            JSONObject jsonObj = new JSONObject(jsonData);
            return jsonObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray parseJsonArray(String url){
        String jsonData = readTextJSon(url);
        try {
            JSONArray jsonArr = new JSONArray(jsonData);
            return jsonArr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
