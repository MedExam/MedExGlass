package com.medex.globals;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by dhruv on 22-11-2016.
 */
public class Assessment implements Cloneable {
    ArrayList<Pair<String,String>> question_answers;
    public boolean isRunning = false;
    String type;
    String subType;
    String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

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

    public Assessment clone(){
        Assessment a = new Assessment();
        a.setQuestion_answers((ArrayList<Pair<String,String>>)this.getQuestion_answers().clone());
        a.setSubType(this.subType);
        a.setType(this.type);
        a.dateString = this.dateString;
        return a;
    }

    public JSONObject toJSONObject(){
        JSONObject json = new JSONObject();
        try {
            json.put("type",this.getType());
            json.put("subtype",this.getSubType());
            json.put("observations",this.toString());
            json.put("date",this.dateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public Assessment(JSONObject obj){
        try {
            this.setType(obj.getString("type"));
            this.setSubType(obj.getString("subtype"));
            String observations = obj.getString("observations");
            this.dateString = obj.getString("date");
            this.question_answers = new ArrayList<Pair<String, String>>();
            String[] qas = observations.split(";");
            for (int i = 0; i < qas.length; i++) {
                String[] t = qas[i].split("\\|");
                this.question_answers.add(new Pair<String, String>(t[0], t[1]));
            }
        }catch(Exception e){

        }


    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        Iterator iterator = this.getQuestion_answers().iterator();
        boolean first = true;
        while(iterator.hasNext()){
            if(first)
                first = false;
            else
                sb.append(";");
            Pair<String,String> qa = (Pair<String,String>) iterator.next();
            sb.append(qa.first + "|" + qa.second);
        }

        return sb.toString();

    }

    public void setQuestion_answers(ArrayList<Pair<String, String>> question_answers) {
        this.question_answers = question_answers;
    }


    public void add (String question, String answer)
    {
        this.question_answers.add(new Pair<String, String>(question,answer));
    }

    public void start(){
        this.isRunning = true;
    }

}
