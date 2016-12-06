package com.medex.globals;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

//SINGLETON
public class LocalDataStore {

    private static LocalDataStore _instance;
    public static LinkedList<ExaminationSession> completedSessions;
    public static ExaminationSession currentSession;
    public static JSONArray patients;
    public static HashMap<String,String> examinations;
    public static JSONObject questionnaire;
    public static HashMap<String,ArrayList<String>> assessment_info;


    //Leave Empty Constructor for Singleton pattern

    private LocalDataStore(){}

    public synchronized static LocalDataStore getInstance(){
        //Only initialize if null, else return existing instance
        if(_instance == null){
            _instance = new LocalDataStore();
            completedSessions = new LinkedList<ExaminationSession>();
            currentSession = new ExaminationSession();
            patients = null;
        }
        return _instance;
    }


    //public LinkedList<ExaminationSession> completedSessions = new LinkedList<ExaminationSession>();
    //public static final LocalDataStore data = new LocalDataStore();
    //public static ExaminationSession currentSession;
}
