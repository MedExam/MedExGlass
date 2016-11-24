package com.medex.globals;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import java.net.URLEncoder;

public class ParseUtil extends ServicesUtil{
    private static HashMap<String,String> headers =  new HashMap<String, String>() {{
        put("X-Parse-Application-Id","0YH4GGiq68KLHbxNPQ2iiWFsJjIAVHpwT8qN6A7v");
        put("X-Parse-REST-API-Key","tw9zucFAVL6mWEwyjwEaLXtfC81zdXujVvm51Co8");
        put("X-Parse-Master-Key","tmsYPxgwrONpKgPMa2bl2DJXjAMPO4SLrKJoZuGyc");
    }};
    private static HttpsURLConnection addHeaders(HttpsURLConnection con){
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            con.addRequestProperty(entry.getKey(),entry.getValue());
        }
        return con;
    }

    static String PATIENTS_SVC = "https://med-ex.herokuapp.com/parse/classes/Patients";
    public static JSONArray getPatients() throws IOException, JSONException {
        HttpsURLConnection con = getConnectionObject(ParseUtil.PATIENTS_SVC);
        con = addHeaders(con);
        String data = getJSON(con);
        JSONObject json = new JSONObject(data);
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
        json.put("type",a.getType() );
        json.put("subtype", a.getSubType());
        String url = ParseUtil.QUESTIONNAIRE_SVC + URLEncoder.encode(json.toString());
        HttpsURLConnection con = getConnectionObject(url);
        con = addHeaders(con);
        String data = getJSON(con);
        json = new JSONObject(data);
        return json.getJSONArray("results");
    }
}
