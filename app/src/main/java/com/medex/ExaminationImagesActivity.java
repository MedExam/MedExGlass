package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import java.io.File;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExaminationImagesActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GestureDetector mGestureDetector;
    private CardScrollView mCardScroller;

    private View mView;
    private CardScrollAdapter mAdapter;
    private int imageNo = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAdapter = new CardAdapter(createCards(this));
        mGestureDetector = createGestureDetector(this);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);

        setContentView(mCardScroller);
//        set
        setCardScrollerListener();
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        String key = KeyEvent.keyCodeToString(keycode);
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // user tapped touchpad, do something
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.SWIPE_RIGHT) {
                    if(imageNo < LocalDataStore.getInstance().currentSession.getImages().size()){
                        File pictureFile = new File(LocalDataStore.getInstance().currentSession.getImages().get(imageNo++));
                        if (pictureFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

                            myBitmap = Bitmap.createScaledBitmap(myBitmap, 640, 360, false);
                            mCardScroller.addView( new CardBuilder(context, CardBuilder.Layout.CAPTION)
                                    .addImage(myBitmap)
                                    .setText("")
                                    .getView());
                        }
                    }

                    // do something on right (forward) swipe
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        LinkedList<String> images = LocalDataStore.getInstance().currentSession.getImages();
        int i = 0;
        if(images.size() == 0)
            cards.add(i, new CardBuilder(this, CardBuilder.Layout.MENU)
//                    .addImage(myBitmap)
                    .setText("No Images"));
        else{
//                File pictureFile = new File(images.get(imageNo++));
            for(String image: images) {
//                File pictureFile = new File(images.get(imageNo++));
                File pictureFile = new File(image);

                if (pictureFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

                    myBitmap = Bitmap.createScaledBitmap(myBitmap, 640, 360, false);
                    cards.add(i++, new CardBuilder(this, CardBuilder.Layout.CAPTION)
                            .addImage(myBitmap)
                            .setText(""));
                }
            }
        }


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
