package com.medex.globals;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ThreadUtil extends Thread{
    public ServicesEnum operation;
    public ThreadUtil(ServicesEnum service){
        this.operation = service;

    }
   

    public void run() {
        try {
            switch (this.operation) {
                case GET_PATIENTS:
                    LocalDataStore.getInstance().patients = ParseUtil.getPatients();
                    break;
                case GET_ASSESSMENTS:
                    JSONArray assessmentInfo = ParseUtil.getAssessmentInfo();
                    LocalDataStore.getInstance().assessment_info = new HashMap<String,ArrayList<String>>();
                    for(int i=0; i< assessmentInfo.length(); i++){
                        if(LocalDataStore.getInstance().assessment_info.containsKey(((JSONObject)(assessmentInfo.get(i))).get("type"))){
                            LocalDataStore.getInstance().assessment_info.get(((JSONObject)(assessmentInfo.get(i))).get("type"))
                                    .add((String)((JSONObject)(assessmentInfo.get(i))).get("subtype"));
                        }
                        else{
                            LocalDataStore.getInstance().assessment_info.put((String)((JSONObject)(assessmentInfo.get(i))).get("type"),new ArrayList<String>());
                            LocalDataStore.getInstance().assessment_info.get(((JSONObject)(assessmentInfo.get(i))).get("type"))
                                    .add((String)((JSONObject)(assessmentInfo.get(i))).get("subtype"));

                        }
                    }
                    break;
                case GET_QUESTIONNAIRE:
                    JSONObject questionnaire = (JSONObject) (ParseUtil.getQuestionnaire().get(0));
                    LocalDataStore.getInstance().questionnaire = (JSONObject) questionnaire.get("questions");
                    break;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }



}
