package com.medex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ParseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainNotesActivity extends Activity {

	private static final int SPEECH_REQUEST = 0;
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private AudioManager mAudioManager;
	private final Handler mHandler = new Handler();
	private GestureDetector mGestureDector;

	private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
		@Override
		public boolean onGesture(Gesture gesture) {

			if (gesture == Gesture.LONG_PRESS) {
				Log.d("adf", "tappp");
				mAudioManager.playSoundEffect(Sounds.TAP);
				openOptionsMenu();
				return true;
			} else {
				return false;
			}
		}
	};
	private NotesCardScrollAdapter mAdapter;

	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			ParseUtil util=new ParseUtil();
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			LocalDataStore.getInstance().currentSession.addNotes(spokenText);
			try {
				boolean flag=util.postPatientDetails(MainNotesActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Card card = new Card(this);
			card.setText(spokenText);
			mCards.add(card);
		}
		//super.onActivityResult(requestCode, resultCode, data);
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
		JSONArray notesarray = new JSONArray();
		Log.d("adf", "aaaa");
		setContentView(R.layout.activity_main_notes);
		mGestureDector = new GestureDetector(
				this).setBaseListener(mBaseListener);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		LinkedList<String> notes = LocalDataStore.getInstance().currentSession.getNotes();
		mCards = new ArrayList<Card>();
		for (String not : notes) {
			Card card = new Card(this);
			card.setText(not);
			card.setFootnote("Medical Notes");
			mCards.add(card);
		}
		mCardScrollView = new CardScrollView(this);
		mAdapter = new NotesCardScrollAdapter();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
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

	public void addNotes() {
		displaySpeechRecognizer();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_note:
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					addNotes();
				}
			});
			return true;
		default:
			return false;
		}
	}

}
