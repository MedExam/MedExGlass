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
import java.util.List;

public class ExaminationActivity extends Activity {
    private static final String TAG = ExaminationActivity.class.getSimpleName();

    static final int START = 0;
    static final int STOP = 1;
    static final int WATCH_DEMO_VIDEO = 2;
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
        String footnote = new String();
        cards.add(START, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("STart a new examination session").setFootnote(footnote));
        cards.add(STOP, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Stop the current examination session").setFootnote(footnote));
        cards.add(WATCH_DEMO_VIDEO, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Get STarted").setFootnote("Tap to see demo"));
        return cards;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onresume");
        refreshCards();
        mCardScroller.activate();
    }

    private void refreshCards() {
        String footnote;
        for(int position =0; position< mAdapter.getCount();position++){
            switch (position){
                case START:
                    System.out.println("onresume START " + LocalDataStore.data.currentSession.isRunning);
                     footnote = new String();
                    if(LocalDataStore.data.currentSession.isRunning)
                        footnote = new String("A seesion is in progress. Stop to start new session.");
                    ((CardBuilder)(mAdapter.getItem(position))).setFootnote(footnote);
                    break;
                case STOP:
                    System.out.println("onresume STOP " + LocalDataStore.data.currentSession.isRunning);
                     footnote = new String();
                    if(!LocalDataStore.data.currentSession.isRunning)
                        footnote = new String("No active session");
                    else
                        footnote = new String();

                    ((CardBuilder)(mAdapter.getItem(position))).setFootnote(footnote);
                    break;
            }

        }
        mAdapter.notifyDataSetChanged();
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
                    case START:
                        if(!LocalDataStore.data.currentSession.isRunning) {
                            LocalDataStore.data.currentSession.start();
//                            refreshCards();
                        }
                        startActivity(new Intent(ExaminationActivity.this, MenuActivity.class));
                    case STOP:
                        if(LocalDataStore.data.currentSession.isRunning)
                            LocalDataStore.data.currentSession.stop();
                        break;
                    case WATCH_DEMO_VIDEO:
                        break;
                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't show anything");
                }

                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }
}
