package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.Assessment;
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

public class ViewAssessmentActivity extends Activity {
    private static final String TAG = ViewAssessmentActivity.class.getSimpleName();
    private CardScrollView mCardScroller;
    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;
    int position = -1;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE ExaminationTypeActivity!");
        System.out.println("Track: " + this.getClass());

        localDataStore_instance = LocalDataStore.getInstance();
        position = Integer.parseInt(getIntent().getStringExtra("position"));
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        Assessment assessment = new Assessment(LocalDataStore.getInstance().currentSession.completedAssessments.get(position));
        Iterator it = assessment.getQuestion_answers().iterator();
        int i = 0;
        while(it.hasNext()){
            Pair<String,String> q_a =  (Pair<String,String>)it.next();
            if(it.hasNext())
                cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText("Question:\n"+q_a.first+"\n\nObservation\n"+q_a.second).setFootnote("Swipe right for more observations"));
            else{
                cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText("Question:\n"+q_a.first+"\n\nObservation\n"+q_a.second).setFootnote(" Assessment Completed. Swipe down to exit"));
            }
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
//                try {
//                if(position == Number_of_question_answers ){
//                    LocalDataStore.getInstance().currentSession.stopAssessment();
//                    (new ParseUtil()).postPatientDetails(ViewAssessmentActivity.this);
//                    finish();
//                }
//                else if(position == 0){
//                        if(Number_of_question_answers == 0) {
//                            LocalDataStore.getInstance().currentSession.stopAssessment();
//                            (new ParseUtil()).postPatientDetails(ViewAssessmentActivity.this);
//                            finish();
//                        }
//                }
//                else{
//                    LocalDataStore.getInstance().currentSession.assessment.add(question_answer.get(0),question_answer.get(position));
//                    try {
//                        LocalDataStore.getInstance().questionnaire = LocalDataStore.getInstance().questionnaire.getJSONObject("answers").getJSONObject( question_answer.get(position) );
//
//                        startActivity(new Intent(ViewAssessmentActivity.this, ViewAssessmentActivity.class));
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }


}
