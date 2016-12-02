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

//    static final int START = 0;
//    static final int WATCH_DEMO_VIDEO = 1;
    static final int SET_PATIENT = 1;
    static final int SELECT_PATIENT = 0;
    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCREATE ExaminationActivity!");

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
        String footnote = new String();
//        if(!LocalDataStore.getInstance().currentSession.isRunning())
//            cards.add(START, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                    .setText("Start a new examination session").setFootnote(""));
//        else
//            cards.add(START, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                    .setText("Stop the current examination session").setFootnote(""));
//        cards.add(WATCH_DEMO_VIDEO, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("Get STarted").setFootnote("Tap to see demo"));

        cards.add(SELECT_PATIENT, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setIcon(R.drawable.ic_menu_allfriends)
                .setText("SELECT PATIENT").setFootnote(" Swipe for using voice input selection"));
        cards.add(SET_PATIENT, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("SET PATIENT").setFootnote("Using Voice Input."));
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
                switch (position) {
//                    case START:
//                            startActivity(new Intent(ExaminationActivity.this, UserActivity.class));
//                        break;
//
//                    case WATCH_DEMO_VIDEO:
//                        break;

                    case SET_PATIENT:
                        startActivity(new Intent(ExaminationActivity.this, VoiceInputActivity.class));
                        break;
                    case SELECT_PATIENT:
                        startActivity(new Intent(ExaminationActivity.this, UserActivity.class));
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
