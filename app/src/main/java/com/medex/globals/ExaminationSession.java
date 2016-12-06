package com.medex.globals;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class ExaminationSession implements Cloneable{

    private static final String TAG = ExaminationSession.class.getSimpleName();

    private long startTimestamp;
    private long stopTimestamp;

    public void setNotes(LinkedList<JSONObject> notes) {
        this.notes = notes;
    }

    public void setImages(LinkedList<JSONObject> images) {
        this.images = images;
    }

    public void setVideos(LinkedList<JSONObject> videos) {
        this.videos = videos;
    }

    public void setCompletedAssessments(LinkedList<JSONObject> completedAssessments) {
        this.completedAssessments = completedAssessments;
    }

    private LinkedList<JSONObject> images = new LinkedList<JSONObject>();
    private LinkedList<JSONObject> videos = new LinkedList<JSONObject>();
    private LinkedList<JSONObject> notes = new LinkedList<JSONObject>();
    private boolean isRunning;
    private JSONObject user;
    public Assessment assessment;
    public LinkedList<JSONObject> completedAssessments = new LinkedList<JSONObject>();

    public ExaminationSession(){}
    public ExaminationSession clone() {
        ExaminationSession e = new ExaminationSession();
        e.setStartTimestamp(this.getStartTimestamp());
        e.setStopTimestamp(this.getStopTimestamp());
        e.images = (LinkedList<JSONObject>) this.images.clone();
        e.videos = (LinkedList<JSONObject>) this.videos.clone();
        e.notes = (LinkedList<JSONObject>) this.notes.clone();
        return e;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        System.out.println("Setting user"+user);
        this.user = user;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(long stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }
    public void start(){

        this.isRunning = true;
        assessment = new Assessment();
        this.setStartTimestamp((long) new Date().getTime());
        Log.d(TAG, "localDataStore_instance.currentSession.start() called completed. isRunning: " + this.isRunning);
    }

    public LinkedList<JSONObject> getImages() {
        return images;
    }

    public LinkedList<JSONObject> getNotes() {
        return notes;
    }

    public void stop()  {
        //debugging purposes - prints out all images path files stored for current session
        showCurrentImages();
        //set running to false
        this.isRunning = false;
        this.setStopTimestamp((long) new Date().getTime());
        //add Clone of current session to completedSessions.
        LocalDataStore.getInstance().completedSessions.add(this.clone());
        Log.d(TAG, "localDataStore_instance.currentSession.stop() called completed. isRunning: " + this.isRunning);
        //clear ALL DATA
        images.clear();

    }

    public void stopAssessment(){
        this.completedAssessments.add(0,this.assessment.toJSONObject());
        this.assessment = new Assessment();

    }
    public void cancelAssessment(){
        this.assessment = new Assessment();

    }



    public boolean isRunning(){
        return this.isRunning;
    }

    //store imagePath to images
    public void addImage(String filepath){
        Log.d(TAG, "addImage().... adding String image filepath: " + filepath);
        JSONObject json = new JSONObject();
        System.out.println("added ...");
        try {
            json.put("path",filepath);
            json.put("timestamp",new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            System.out.println("added");
            this.images.add(0,json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //store imagePath to images
    public void addNotes(String note){
        Log.d(TAG, "addNotes.... adding Notes " + note);
        JSONObject json = new JSONObject();
        try {
            json.put("text",note);
            json.put("timestamp",new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            this.notes.add(0,json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //debugging purposes - prints out all images path files stored for current session.
    public void showCurrentImages(){
        Iterator it = images.iterator();

        int i = 1;
        while(it.hasNext()){
            System.out.println("ImagePathStored number " + i +" is: ....." + it.next());
                    i++;
        }
    }

    public void addVideo(String filepath){
        JSONObject json = new JSONObject();
        try {
            json.put("path",filepath);
            json.put("timestamp",new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            this.images.add(0,json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
