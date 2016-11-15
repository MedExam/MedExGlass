package com.medex.globals;

import java.util.LinkedList;

//SINGLETON
public class LocalDataStore {

    private static LocalDataStore _instance;
    public static LinkedList<ExaminationSession> completedSessions;
    public static ExaminationSession currentSession;

    //Leave Empty Constructor for Singleton pattern
    private LocalDataStore(){}

    public synchronized static LocalDataStore getInstance(){
        //Only initialize if null, else return existing instance
        if(_instance == null){
            _instance = new LocalDataStore();
            completedSessions = new LinkedList<ExaminationSession>();
            currentSession = new ExaminationSession();
        }
        return _instance;
    }

    //public LinkedList<ExaminationSession> completedSessions = new LinkedList<ExaminationSession>();
    //public static final LocalDataStore data = new LocalDataStore();
    //public static ExaminationSession currentSession;
}
