package com.medex.globals;

import java.util.Date;
import java.util.LinkedList;

public class ExaminationSession implements Cloneable{

    public ExaminationSession clone() {
        ExaminationSession e = new ExaminationSession();
        e.setStartTimestamp(this.getStartTimestamp());
        e.setStopTimestamp(this.getStopTimestamp());
        e.images = (LinkedList<String>) this.images.clone();
        e.videos = (LinkedList<String>) this.videos.clone();
        return e;
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
        this.setStartTimestamp((long) new Date().getTime());
    }

    public void stop()  {
        this.isRunning = false;
        this.setStopTimestamp((long) new Date().getTime());
        LocalDataStore.data.completedSessions.add( this.clone());
    }

    public void addImage(String filepath){
        this.images.add(filepath);
    }

    public void addVideo(String filepath){
        this.videos.add(filepath);
    }

    private long startTimestamp;
    private long stopTimestamp;
    private LinkedList<String> images = new LinkedList<String>();
    private LinkedList<String> videos = new LinkedList<String>();
    public boolean isRunning = false;
}
