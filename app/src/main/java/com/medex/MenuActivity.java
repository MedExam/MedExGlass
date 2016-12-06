package com.medex;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.dto.Patient;
import com.medex.globals.LocalDataStore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import android.os.FileObserver;
import android.widget.TextView;

import org.json.JSONException;

public class MenuActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Index of api demo cards.
    // Visible for testing.
    static final int PERSONAL_DETAILS = 0;
    static final int ALLERGIES = 1;
//    static final int MEDICATIONS = 2;
    static final int TAKE_AN_IMAGE = 2;
    static final int TAKE_NOTES = 4;
    static final int SHOW_SCORE = 5;
    static final int SHOW_IMAGES = 3;
    //static final int SLIDER = 7;
    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mCardScroller = new CardScrollView(this);
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
    }
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        Patient patient = new Patient(LocalDataStore.getInstance().currentSession.getUser());
        try {
            if(LocalDataStore.getInstance().currentSession.getUser().getString("gender").equals("F"))
                cards.add(PERSONAL_DETAILS, new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                        .setText("Patient Details\n" + patient.toStringMetadata())
                        .setIcon(R.drawable.girl));
            else
                cards.add(PERSONAL_DETAILS, new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED)
                        .setText("Patient Details\n" + patient.toStringMetadata())
                        .setIcon(R.drawable.boy));


        } catch (Exception e) {
            e.printStackTrace();
        }
        cards.add(ALLERGIES, new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Allergies\n"+patient.toStringAllergies()+"\n"+"\nMedications\n"+patient.toStringMedications()));

        cards.add(TAKE_AN_IMAGE, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setText("Take image").setIcon(R.drawable.ic_menu_camera));
        cards.add(SHOW_IMAGES, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setIcon(R.drawable.ic_menu_camera)
                .setText("Show Images").setFootnote("Shows previously recorded images"));
        cards.add(TAKE_NOTES, new CardBuilder(this, CardBuilder.Layout.MENU)
                .setIcon(R.drawable.sym_action_chat)
                .setText("Show Notes").setFootnote("Long Tap to add new note"));
        cards.add(SHOW_SCORE, new CardBuilder(this, CardBuilder.Layout.MENU)
//                .setIcon(R.drawable.sym_action_chat)
                .setText("Show Gum Score").setFootnote("Shows the gum score images"));


//        cards.add(STREAM, new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("Stream Live Video"));


        return cards;
    }
    @Override
    protected void onResume() {
        super.onResume();
//        System.out.println( " onresume menu:"+LocalDataStore.data.currentSession.isRunning);
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
                    case TAKE_NOTES:
                        Log.d(TAG, "Taking Notes!");
                      startActivity(new Intent(MenuActivity.this, MainNotesActivity.class));
                        break;
                    case SHOW_IMAGES:
                        Log.d(TAG, "Taking an image!");
                        startActivity(new Intent(MenuActivity.this, ExaminationImagesActivity.class));
                        //takePicture();
                        break;
                    case TAKE_AN_IMAGE:
                        Log.d(TAG, "Taking an image!");
                        startActivity(new Intent(MenuActivity.this, CameraActivity.class));
                        //takePicture();
                        break;
                    case SHOW_SCORE:
                        Log.d(TAG, "Taking an image!");
                        startActivity(new Intent(MenuActivity.this, ScoresActivity.class));
                        //takePicture();
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
/*
    private static final int TAKE_PICTURE_REQUEST_NUM = 1;
    private void takePicture(){
        Log.d(TAG, "takePicture() called.");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_NUM);
    }*/

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TAKE_PICTURE_REQUEST_NUM && resultCode == RESULT_OK) {
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
            processPictureWhenReady(picturePath);
            Log.d(TAG, "onActivityResult called. PicturePath is: " + picturePath);
        }
            super.onActivityResult(requestCode, resultCode, data);

    }*/
}
