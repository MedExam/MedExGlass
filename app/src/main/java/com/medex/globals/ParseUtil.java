package com.medex.globals;


import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class ParseUtil extends ServicesUtil {

    private static HashMap<String, String> headers = new HashMap<String, String>() {{
        put("X-Parse-Application-Id", "0YH4GGiq68KLHbxNPQ2iiWFsJjIAVHpwT8qN6A7v");
        put("X-Parse-REST-API-Key", "tw9zucFAVL6mWEwyjwEaLXtfC81zdXujVvm51Co8");
        put("X-Parse-Master-Key", "tmsYPxgwrONpKgPMa2bl2DJXjAMPO4SLrKJoZuGyc");
    }};

    private static HttpsURLConnection addHeaders(HttpsURLConnection con) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.addRequestProperty(entry.getKey(), entry.getValue());
        }
        return con;
    }

    static String PATIENTS_SVC = "https://med-ex.herokuapp.com/parse/classes/Patients";
    public static JSONArray getPatients() throws IOException, JSONException {
        HttpsURLConnection con = getConnectionObject(ParseUtil.PATIENTS_SVC);
        con = addHeaders(con);
        String data = getJSON(con);
        JSONObject json = new JSONObject(data);
        System.out.println("Results - "+json.getJSONArray("results").toString());
        return json.getJSONArray("results");
    }

    static String ASSESSMENT_INFO_SVC = "https://med-ex.herokuapp.com/parse/classes/AssessmentInfo";

    public static JSONArray getAssessmentInfo() throws IOException, JSONException {
        HttpsURLConnection con = getConnectionObject(ParseUtil.ASSESSMENT_INFO_SVC);
        con = addHeaders(con);
        String data = getJSON(con);
        JSONObject json = new JSONObject(data);
        return json.getJSONArray("results");
    }

    static String QUESTIONNAIRE_SVC = "https://med-ex.herokuapp.com/parse/classes/Questionnaire?where=";
    public static JSONArray getQuestionnaire() throws IOException, JSONException {
        Assessment a = LocalDataStore.getInstance().currentSession.assessment;
        JSONObject json = new JSONObject();
        json.put("type", a.getType());
        json.put("subtype", a.getSubType());
        String url = ParseUtil.QUESTIONNAIRE_SVC + URLEncoder.encode(json.toString());
        HttpsURLConnection con = getConnectionObject(url);
        con = addHeaders(con);
        String data = getJSON(con);
        json = new JSONObject(data);
        return json.getJSONArray("results");
    }

    //static String PATIENTS_SVC_POST = "https://api.mlab.com/api/1/databases/heroku_v1w06qjt/collections/Patients?f=";
    static String PATIENTS_SVC_POST = "https://api.mlab.com/api/1/databases/heroku_v1w06qjt/collections/Patients?apiKey=";
    public boolean postPatientDetails(Context context) throws IOException, JSONException {
        JSONArray imagesarray = new JSONArray();
        JSONArray notesarray = new JSONArray();
        JSONObject jsonobject=new JSONObject();
        JSONObject updateobject=new JSONObject();
        LinkedList<String> images = LocalDataStore.getInstance().currentSession.getImages();
        LinkedList<String> notes = LocalDataStore.getInstance().currentSession.getNotes();
        for (String img : images) {
            System.out.println("Images- "+img);
            JSONObject image = new JSONObject();
            image.put("path", img);
            imagesarray.put(image);
        }

        for (String not : notes) {
            JSONObject note = new JSONObject();
            note.put("text", not);
            notesarray.put(note);
        }

        //Updating the JSON
        jsonobject.put("images",imagesarray);
        jsonobject.put("notes",notesarray);
        updateobject.put("$set",jsonobject);

        String name = LocalDataStore.getInstance().currentSession.getUser().getString("name");
        JSONObject json = new JSONObject();
        json.put("name", name);
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("name", 1);
        jsonobj.put("images", 1);
        jsonobj.put("notes", 1);
        String apikey="PsFa5SuAaCuE3mmkiOEGbL8GV-VmQcdI";
        /*String url =PATIENTS_SVC_POST+URLEncoder.encode(jsonobj.toString(),"UTF-8")+"&apiKey="+URLEncoder.encode(apikey,"UTF-8")
                +"&q="+URLEncoder.encode(json.toString(),"UTF-8");*/
        String url =PATIENTS_SVC_POST+URLEncoder.encode(apikey,"UTF-8")
                +"&q="+URLEncoder.encode(json.toString(),"UTF-8");
        System.out.println(url);
                // String url = ParseUtil.PATIENTS_SVC_POST +
/*
        String query1="{\"name\":1,\"images\":1,\"notes\":1}";
        String query2="PsFa5SuAaCuE3mmkiOEGbL8GV-VmQcdI";
*/
/*
        String url1 = ParseUtil.PATIENTS_SVC_POST + "{\"name\":\""+name+"\"}";
        String url = ParseUtil.PATIENTS_SVC_POST + URLEncoder.encode(query1,"UTF-8")+"&apiKey="+URLEncoder.encode(query2,"UTF-8") +
                "&q="+URLEncoder.encode(json.toString());
*/
        AsyncHttpClient httpclient = new AsyncHttpClient();
        StringEntity entity = new StringEntity(updateobject.toString());
        //System.out.println("Entity is"+entity.toString());
        entity.setContentType(new cz.msebera.android.httpclient.message.BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        System.out.println("Entity is"+entity.toString()+"Content - "+entity.getContent().toString());
        httpclient.put(context,url,entity,"application/json",new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("\nPosted Successfully\n");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("\nSorry Something Went Wrong while posting\n");
                System.out.println("Status Code-- "+statusCode+" \nError Message " + error.getMessage());

            }
        });
        return true;
    }
}
