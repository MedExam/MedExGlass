package com.medex;

import com.google.android.glass.content.Intents;
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
                    case RECORD_VIDEO:
                        Log.d(TAG, "Recording a video!");
//                        startActivity(new Intent(MenuActivity.this, MenuActivity.class));
                        break;
                    case TAKE_AN_IMAGE:
                        Log.d(TAG, "Taking an image!");
                        takePicture();
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

    private static final int TAKE_PICTURE_REQUEST_NUM = 1;
    private void takePicture(){
        Log.d(TAG, "takePicture() called.");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_NUM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TAKE_PICTURE_REQUEST_NUM && resultCode == RESULT_OK) {
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
            processPictureWhenReady(picturePath);
            Log.d(TAG, "onActivityResult called. PicturePath is: " + picturePath);
        }
            super.onActivityResult(requestCode, resultCode, data);

    }

    private void processPictureWhenReady(final String picturePath) {
        Log.d(TAG, "processPictureWhenReady() called.");
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            Log.d(TAG, "pictureFile exists. Ready to process.");
            Log.d(TAG, "onActivityResult called. PicturePath is: " + picturePath);
            // The picture is ready; process it - need to add to session images.
            LocalDataStore.getInstance().currentSession.addImage(picturePath);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).
            Log.d(TAG, "pictureFile doens't exists yet.");
            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }
}
