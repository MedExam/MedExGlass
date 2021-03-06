package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ParseUtil;
import com.medex.globals.ServicesEnum;
import com.medex.globals.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AssessmentActivity extends Activity {
    private static final String TAG = AssessmentActivity.class.getSimpleName();
    private CardScrollView mCardScroller;
    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;
    private int Number_of_question_answers = 0;
    private ArrayList<String> question_answer = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE ExaminationTypeActivity!");
        System.out.println("Track: " + this.getClass());

        localDataStore_instance = LocalDataStore.getInstance();
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        try {
            if(!LocalDataStore.getInstance().currentSession.assessment.isRunning) {
                ThreadUtil thread = new ThreadUtil(ServicesEnum.GET_QUESTIONNAIRE);
                thread.start();
                thread.join();
                LocalDataStore.getInstance().currentSession.assessment.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = 0;
        LocalDataStore localDataStore = LocalDataStore.getInstance();
        try {
            JSONObject q =  localDataStore.questionnaire;
            if(q.length() == 0){
                cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText("Assessment Completed").setFootnote("Tap to exit "));
                Number_of_question_answers = 0;
            }
            else {
                cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText((String) LocalDataStore.getInstance().questionnaire.get("question")).setFootnote("Swipe right to record observations"));
                question_answer.add((String) LocalDataStore.getInstance().questionnaire.get("question"));
                JSONObject answers = (JSONObject) LocalDataStore.getInstance().questionnaire.get("answers");
                Iterator keys = answers.keys();
                while (keys.hasNext()) {
                    String answer = (String) keys.next();
                    question_answer.add(answer);
                    cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                            .setText(answer).setFootnote("Tap to record. Swipe right for more"));
                }
                cards.add(i, new CardBuilder(this, CardBuilder.Layout.MENU)
                        .setText("Finish assessment").setFootnote("This will stop the assessment"));
                Number_of_question_answers = i ;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return cards;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mCardScroller != null) {
            mCardScroller.activate();
        }
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
                try {
                if(position == Number_of_question_answers ){

                    if(LocalDataStore.getInstance().currentSession.assessment.getQuestion_answers().size()>0) {
                        (new ParseUtil()).postPatientDetails(AssessmentActivity.this);
                        LocalDataStore.getInstance().currentSession.stopAssessment();
                    }
                    else
                        LocalDataStore.getInstance().currentSession.cancelAssessment();

                    finish();
                }
                else if(position == 0){
                        if(Number_of_question_answers == 0) {
                            LocalDataStore.getInstance().currentSession.stopAssessment();
                            (new ParseUtil()).postPatientDetails(AssessmentActivity.this);
                            finish();
                        }
                }
                else{
                    LocalDataStore.getInstance().currentSession.assessment.add(question_answer.get(0),question_answer.get(position));
                    try {
                        LocalDataStore.getInstance().questionnaire = LocalDataStore.getInstance().questionnaire.getJSONObject("answers").getJSONObject( question_answer.get(position) );

                        startActivity(new Intent(AssessmentActivity.this, AssessmentActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }


}
