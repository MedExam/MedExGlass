package com.medex.globals;

import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dhruv on 22-11-2016.
 */
public class Assessment {
    ArrayList<Pair<String,String>> question_answers;
    public boolean isRunning = false;
    String type;
    String subType;

    public Assessment(){
        this.question_answers = new ArrayList<Pair<String, String>>();
    }
    public ArrayList<Pair<String, String>> getQuestion_answers() {
        return question_answers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setQuestion_answers(ArrayList<Pair<String, String>> question_answers) {
        this.question_answers = question_answers;
    }

    public void add (String question, String answer)
    {
    }

    public void start(){
        this.isRunning = true;
    }
}
