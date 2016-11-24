package com.medex.globals;

import android.util.Log;

import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class ExaminationSession implements Cloneable{

    private static final String TAG = ExaminationSession.class.getSimpleName();

    private long startTimestamp;
    private long stopTimestamp;
    private LinkedList<String> images = new LinkedList<String>();
    private LinkedList<String> videos = new LinkedList<String>();
    private boolean isRunning;
    private JSONObject user;
    public Assessment assessment;

    public ExaminationSession(){}

    public ExaminationSession clone() {
        ExaminationSession e = new ExaminationSession();
        e.setStartTimestamp(this.getStartTimestamp());
        e.setStopTimestamp(this.getStopTimestamp());
        e.images = (LinkedList<String>) this.images.clone();
        e.videos = (LinkedList<String>) this.videos.clone();
        return e;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
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

    public boolean isRunning(){
        return this.isRunning;
    }

    //store imagePath to images
    public void addImage(String filepath){
        Log.d(TAG, "addImage().... adding String image filepath: " + filepath);
        this.images.add(filepath);
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
        this.videos.add(filepath);
    }


}
