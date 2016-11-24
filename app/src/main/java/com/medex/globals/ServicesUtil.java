package com.medex.globals;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ServicesUtil {
    public static HttpsURLConnection getConnectionObject(String urlStr) throws IOException {
        URL url ;
        url = new URL(urlStr);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        return con;
    }
    public  static String getJSON(HttpsURLConnection c ) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();
    }


}
