package com.medex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardBuilder;
import com.medex.globals.LocalDataStore;
import com.medex.globals.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VoiceInputActivity extends Activity {

    private final static int CODE_SPEECH = 0;
    private final static String PROMPT_TEXT = "Voice Input Test. Say something!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);
        getVoiceInput();
    }

    public void getVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, PROMPT_TEXT);
        startActivityForResult(intent, CODE_SPEECH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        JSONArray patients = LocalDataStore.getInstance().patients;
        if (requestCode == CODE_SPEECH && resultCode == RESULT_OK) {

            //get text from voice recognizer
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            System.out.println("SpokenText"+spokenText);
            Boolean found = false;
            for (int i = 0; i < patients.length(); i++) {
                try {
                    if ((((JSONObject) (patients.get(i))).getString("name")).contains(spokenText)) {
                        LocalDataStore.getInstance().currentSession.setUser((JSONObject) LocalDataStore.getInstance().patients.get(i));
                        System.out.println("Value of i"+i+patients.get(i));
                        LocalDataStore.getInstance().currentSession.start();
                        found = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (found){
                try {
                    JSONObject user = LocalDataStore.getInstance().currentSession.getUser();
                    JSONArray notes = user.getJSONArray("notes");
                    LocalDataStore.getInstance().currentSession.setNotes(Utilities.jsonArrayToLinkedList(notes));
                    JSONArray assessments = user.getJSONArray("assessments");
                    LocalDataStore.getInstance().currentSession.setCompletedAssessments(Utilities.jsonArrayToLinkedList(assessments));
                    JSONArray images = user.getJSONArray("images");
                    LocalDataStore.getInstance().currentSession.setImages(Utilities.jsonArrayToLinkedList(images));

                    startActivity(new Intent(VoiceInputActivity.this, ExaminationTypeActivity.class));
                    finish();
                }
                catch(Exception e){
                    System.out.println("\n User Not Found \n");
                    startActivity(new Intent(VoiceInputActivity.this, MessageActivity.class));
                    finish();
                }
                }
            }
            else{
                System.out.println("\n User Not Found \n");
                startActivity(new Intent(VoiceInputActivity.this, MessageActivity.class));
                finish();
            }

        }
    }
}
    /*
			for(int i = 0; i < patients.length(); i++)
			{
				try{
                    if((((JSONObject)(patients.get(i))).getString("name")).equals(spokenText)){
                    LocalDataStore.getInstance().currentSession.setUser((JSONObject) LocalDataStore.getInstance().patients.get(i));
                        LocalDataStore.getInstance().currentSession.start();
                        startActivity(new Intent(VoiceInputActivity.this, ExaminationTypeActivity.class));
					}
                else {
                        System.out.println("\n User Not Found \n");
                cards.add(0, new CardBuilder(this, CardBuilder.Layout.TEXT).setText("User Not Found").setFootnote(""));
                        //Thread.sleep(10000);
                        //startActivity(new Intent(VoiceInputActivity.this, UserActivity.class));
                }
                }
				catch(Exception e){
					e.printStackTrace();
				}*/
		//	}


