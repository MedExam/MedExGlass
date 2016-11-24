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
import com.medex.globals.Assessment;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ServicesEnum;
import com.medex.globals.ThreadUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AssessmentSubTypeActivity extends Activity {
    private static final String TAG = AssessmentSubTypeActivity.class.getSimpleName();

    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;
    private String assessmentType;
    private ArrayList<String> assessmentSubTypes;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE AssessmentSubTypeActivity!");
        System.out.println("Track: " + this.getClass());
        localDataStore_instance = LocalDataStore.getInstance();
        assessmentType = getIntent().getStringExtra("type");
        assessmentSubTypes =  localDataStore_instance.assessment_info.get(assessmentType);
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();

    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        Iterator iter = assessmentSubTypes.iterator();
        int i = 0;
        while(iter.hasNext()){
            cards.add(i++, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText( (String) iter.next()));
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
                String assessmentSubType = assessmentSubTypes.get(position);
                Intent intent = new Intent(AssessmentSubTypeActivity.this, AssessmentActivity.class);
                localDataStore_instance.currentSession.assessment.setType(assessmentType);
                localDataStore_instance.currentSession.assessment.setSubType(assessmentSubType);

                startActivity(intent);
                finish();
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);


            }
        });
    }
}
