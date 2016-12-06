package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AssessmentView_StartActivity extends Activity {
    private static final String TAG = AssessmentView_StartActivity.class.getSimpleName();

    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;
    static final int START_CONTINUE = 0;
    static final int VIEW = 1;

    private LocalDataStore localDataStore_instance;
    private String assessmentType;
    private ArrayList<String> assessmentSubTypes;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE AssessmentSubTypeActivity!");
        System.out.println("Track: " + this.getClass());
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();

    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        if(!LocalDataStore.getInstance().currentSession.assessment.isRunning)
            cards.add(START_CONTINUE, new CardBuilder(this, CardBuilder.Layout.MENU)
                    .setText("New assessment").setFootnote("Add a new assessment "));
        else
            cards.add(START_CONTINUE, new CardBuilder(this, CardBuilder.Layout.MENU)
                    .setText("Continue assessment").setFootnote("A current assessment is active. Stop to start a new assessment"));
        cards.add(VIEW, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Show Assessments").setFootnote("View past assessments"));
//        cards.add(STOP, new CardBuilder(this, CardBuilder.Layout.MENU)
//                .setText("Stop Examination ").setFootnote("Press this to end the appointment. Updates results in EHR"));
        return cards;
//        return cards;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCardScroller != null) {
            mCardScroller.activate();
        }
        refreshCards();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }


    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */


    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                switch (position) {

                    case START_CONTINUE:
                        if(!LocalDataStore.getInstance().currentSession.assessment.isRunning)
                            startActivity(new Intent(AssessmentView_StartActivity.this, AssessmentTypeActivity.class));
                        else
                            startActivity(new Intent(AssessmentView_StartActivity.this, AssessmentActivity.class));
                        break;
                    case VIEW:
                        startActivity(new Intent(AssessmentView_StartActivity.this, ViewListAssessmentActivity.class));
//                        localDataStore_instance.currentSession.stop();
                        finish();
                        break;

                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't show anything");
                }

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);


            }
        });
    }
private void refreshCards() {
    if(!LocalDataStore.getInstance().currentSession.assessment.isRunning)
        ((CardBuilder) ( mAdapter.getItem(START_CONTINUE)))
                .setText("New assessment").setFootnote("Add a new assessment ");
    else
        ((CardBuilder) ( mAdapter.getItem(START_CONTINUE)))
                .setText("Continue assessment").setFootnote("A current assessment is active. Stop to start a new assessment");
//        String footnote;
//        for(int position =0; position< mAdapter.getCount();position++){
//            switch (position){
//                case START:
//                    System.out.println("onresume START sessionRunning:" + localDataStore_instance.currentSession.isRunning());
//                     footnote = new String();
//                    if(localDataStore_instance.currentSession.isRunning())
//                        footnote = new String("A session is in progress. Stop to start new session.");
//                    ((CardBuilder)(mAdapter.getItem(position))).setFootnote(footnote);
//                    break;
//                case STOP:
//                    System.out.println("onresume STOP sessionRunning:" + localDataStore_instance.currentSession.isRunning());
//                     footnote = new String();
//                    if(!localDataStore_instance.currentSession.isRunning())
//                        footnote = new String("No active session");
//                    else
//                        footnote = new String();
//
//                    ((CardBuilder)(mAdapter.getItem(position))).setFootnote(footnote);
//                    break;
//            }
//
//        }
//        mAdapter.notifyDataSetChanged();
    }
}
