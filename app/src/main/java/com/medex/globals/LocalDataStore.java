package com.medex.globals;

import java.util.LinkedList;


public class LocalDataStore {

    public LinkedList<ExaminationSession> completedSessions = new LinkedList<ExaminationSession>();
    public static final LocalDataStore data = new LocalDataStore();
    public static ExaminationSession currentSession;
}
