package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ParseUtil;
import com.medex.globals.ServicesEnum;
import com.medex.globals.ServicesUtil;
import com.medex.globals.ThreadUtil;
import com.medex.globals.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
    }
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        try {
            JSONArray patients;
            if(LocalDataStore.getInstance().patients == null){
                ThreadUtil thread = new ThreadUtil(ServicesEnum.GET_PATIENTS);
                thread.start();
                thread.join();
            }
            patients = LocalDataStore.getInstance().patients;

            for(int i = 0; i < patients.length(); i++)
            {
                if(((JSONObject)(patients.get(i))).has("gender") && ((JSONObject)(patients.get(i))).getString("gender").equals("F"))
                    cards.add(i,new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                            .setText(((JSONObject)(patients.get(i))).getString("name") + "\nFemale" )
                            .setFootnote(((JSONObject)(patients.get(i))).getString("phoneNumber"))
                            .setTimestamp(((JSONObject)(patients.get(i))).getString("emailId"))
                            .setIcon(R.drawable.girl));
                else

                    cards.add(i,new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                            .setText(((JSONObject)(patients.get(i))).getString("name") + "\nMale" )
                            .setFootnote(((JSONObject)(patients.get(i))).getString("phoneNumber"))
                            .setTimestamp(((JSONObject)(patients.get(i))).getString("emailId"))
                            .setIcon(R.drawable.boy));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        cards.add(RECORD_VIDEO, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("Record a video"));
//
//        cards.add(TAKE_AN_IMAGE, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("Take image"));
//
//        cards.add(STREAM, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("Stream Live Video"));
        return cards;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }
    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                LocalDataStore.getInstance().currentSession.start();
                try {
                    LocalDataStore.getInstance().currentSession.setUser((JSONObject) LocalDataStore.getInstance().patients.get(position));
                    JSONObject user=LocalDataStore.getInstance().currentSession.getUser();
                    JSONArray notes = user.getJSONArray("notes");
                    LocalDataStore.getInstance().currentSession.setNotes(Utilities.jsonArrayToLinkedList(notes));
                    JSONArray assessments = user.getJSONArray("assessments");
                    LocalDataStore.getInstance().currentSession.setCompletedAssessments(Utilities.jsonArrayToLinkedList(assessments));
                    JSONArray images = user.getJSONArray("images");
                    LocalDataStore.getInstance().currentSession.setImages(Utilities.jsonArrayToLinkedList(images));

                    startActivity(new Intent(UserActivity.this, ExaminationTypeActivity.class));
                    }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }
}
