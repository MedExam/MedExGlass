package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.cards.CardAdapter;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ParseUtil;
import com.medex.globals.ServicesEnum;
import com.medex.globals.ThreadUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScoresActivity extends Activity {

	private static final int SPEECH_REQUEST = 0;
	private List<CardBuilder>  mCards;
	private CardScrollView mCardScrollView;
	private AudioManager mAudioManager;
	private final Handler mHandler = new Handler();
	private GestureDetector mGestureDector;
	JSONArray examinations;

	int position=0;

	private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
		@Override
		public boolean onGesture(Gesture gesture) {

			if (gesture == Gesture.SWIPE_RIGHT) {
				if(examinations.length() > position){
					position++;
					Bitmap bitmap = getImage();
					mCards.add(new CardBuilder(ScoresActivity.this , CardBuilder.Layout.CAPTION)
							.addImage(bitmap)
							.setText(""));
				}
				return true;
			} else {
				return false;
			}
		}
	};
	private CardScrollAdapter mAdapter;

	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		return mGestureDector.onMotionEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		setContentView(CardBuilder.Layout.CAPTION);
		mGestureDector = new GestureDetector(
				this).setBaseListener(mBaseListener);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		mCards = new ArrayList<CardBuilder>();
		try {
			examinations = LocalDataStore.getInstance().currentSession.getUser().getJSONArray("examinations");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(examinations != null && examinations.length() > 0) {
			Bitmap bitmap = getImage();
			mCards.add(new CardBuilder(this , CardBuilder.Layout.CAPTION)
					.addImage(bitmap)
					.setText(""));
		}
		else
			mCards.add(new CardBuilder(this , CardBuilder.Layout.TEXT)
					.setText("No gum scores found"));
		mCardScrollView = new CardScrollView(this);
		mAdapter = new NotesCardScrollAdapter();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
	}

	private List<CardBuilder> createCards(Context context) {

		ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
		if(examinations != null && examinations.length() > 0) {
			Bitmap bitmap = getImage();
			cards.add(0, new CardBuilder(this, CardBuilder.Layout.CAPTION)
					.addImage(bitmap)
					.setText(""));
		}
		position++;
		return cards;
	}

	private Bitmap getImage(){
//		try {
//			ThreadUtil thread = new ThreadUtil(ServicesEnum.GET_SCORE);
//			thread.start();
//			thread.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		Bitmap myBitmap = null;
		try {
			URL url = new URL(LocalDataStore.getInstance().examinations.get(examinations.getString(position)));
			myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap rotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

			myBitmap = Bitmap.createScaledBitmap(rotated, 700, 400, false);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		position++;
		return myBitmap;

	}

	private class NotesCardScrollAdapter extends CardScrollAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCards.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mCards.get(position);
		}

		@Override
		public int getPosition(Object arg0) {
			// TODO Auto-generated method stub
			return mCards.indexOf(arg0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return mCards.get(position).getView(convertView, parent);
		}

	}


}
