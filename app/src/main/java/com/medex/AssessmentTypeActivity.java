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
import com.medex.globals.ServicesEnum;
import com.medex.globals.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class AssessmentTypeActivity extends Activity {
    private static final String TAG = AssessmentTypeActivity.class.getSimpleName();

    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;
    private ArrayList<String> assessmentTypes;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE ExaminationTypeActivity!");
        System.out.println("Track: " + this.getClass());
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();

        localDataStore_instance = LocalDataStore.getInstance();
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        try {
            if(LocalDataStore.getInstance().assessment_info == null){
                ThreadUtil thread = new ThreadUtil(ServicesEnum.GET_ASSESSMENTS);
                thread.start();
                thread.join();
            }
            Set<String> keys = LocalDataStore.getInstance().assessment_info.keySet();
            Iterator iter = keys.iterator();
            int i = 0;
            assessmentTypes = new ArrayList<String>();
            while(iter.hasNext()){
                String type= (String) iter.next();
                assessmentTypes.add(type);
                cards.add(i++,new CardBuilder(this,CardBuilder.Layout.TEXT)
                        .setText(type));}
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
                Intent intent = new Intent(AssessmentTypeActivity.this, AssessmentSubTypeActivity.class);
                intent.putExtra("type", assessmentTypes.get(position));
                startActivity(intent);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }
}
