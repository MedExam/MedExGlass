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

import java.util.ArrayList;
import java.util.List;

public class ExaminationTypeActivity extends Activity {
    private static final String TAG = ExaminationTypeActivity.class.getSimpleName();

    static final int EHR = 0;
    static final int ASSESSMENT = 1;
    static final int STOP = 2;
    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    private LocalDataStore localDataStore_instance;

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
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

        cards.add(EHR, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("General Examination").setFootnote(" Shows details and options to add images, notes"));
        cards.add(ASSESSMENT, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Assessment").setFootnote("A question/answer session to record observations"));
        cards.add(STOP, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Stop Examination ").setFootnote("Press this to end the appointment. Updates results in EHR"));
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
                    case EHR:
                        localDataStore_instance.currentSession.start();
                        startActivity(new Intent(ExaminationTypeActivity.this, MenuActivity.class));
                        break;

                    case ASSESSMENT:
                        if(!LocalDataStore.getInstance().currentSession.assessment.isRunning)
                            startActivity(new Intent(ExaminationTypeActivity.this, AssessmentView_StartActivity.class));
                        else
                            startActivity(new Intent(ExaminationTypeActivity.this, AssessmentActivity.class));
                        break;
                    case STOP:
                        localDataStore_instance.currentSession.stop();
                        finish();
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
