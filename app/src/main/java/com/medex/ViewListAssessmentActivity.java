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
import com.medex.globals.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ViewListAssessmentActivity extends Activity {
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
        int i = 0;
        try {

            LinkedList<JSONObject> assessments = LocalDataStore.getInstance().currentSession.completedAssessments;
            if(assessments.size() == 0)
                cards.add(0,new CardBuilder(this, CardBuilder.Layout.TEXT)
                        .setText("No recorded assessments"));

            else
                for(JSONObject assessment : assessments)
                {
                    cards.add(i++,new CardBuilder(this, CardBuilder.Layout.TEXT)
                            .setText(assessment.getString("type")+"\n"+assessment.getString("subtype")).setFootnote(assessment.getString("date")));

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
                Intent intent = new Intent(ViewListAssessmentActivity.this, ViewAssessmentActivity.class);
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }
}
