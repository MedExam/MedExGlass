package com.medex;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Index of api demo cards.
    // Visible for testing.
    static final int RECORD_VIDEO = 0;
    static final int TAKE_AN_IMAGE = 1;
    static final int STREAM = 2;
    //static final int SLIDER = 7;
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
        cards.add(RECORD_VIDEO, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Record a video"));

        cards.add(TAKE_AN_IMAGE, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Take image"));

        cards.add(STREAM, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Stream Live Video"));


        return cards;
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println( " onresume menu:"+LocalDataStore.data.currentSession.isRunning);
        mCardScroller.activate();
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
                    case RECORD_VIDEO:
//                        startActivity(new Intent(MenuActivity.this, MenuActivity.class));
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
