package com.medex.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

/**
 * Created by dhruv on 19-11-2016.
 */

public class Patient {
    JSONObject jsonObject;

    public Patient(JSONObject json) {
        this.jsonObject = json;
    }

    public String toStringMetadata() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.jsonObject.get("gender")+"\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (this.jsonObject.has("metadata")) {
            try {
                JSONObject metadata = (JSONObject) this.jsonObject.get("metadata");
                Iterator keys = metadata.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    sb.append(key + "   " + metadata.get(key ));
                    sb.append("\n");
                }
            } catch (Exception e) {
                System.out.println("toStringMetadata: " + e.getMessage());
            }
        }

        return sb.toString();
    }

    public String toStringAllergies() {
        StringBuilder sb = new StringBuilder();
        if (this.jsonObject.has("allergies")) {
            try {
                JSONArray allergies = (JSONArray) this.jsonObject.get("allergies");
                int length = allergies.length();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        sb.append(allergies.getString(i));
                        if(i <length-1)
                            sb.append(", ");
                    }
                }

            } catch (Exception e) {
                System.out.println("toStringAllergies: " + e.getMessage());
            }
        }
        return sb.toString();

    }

    public String toStringMedications() {
        StringBuilder sb = new StringBuilder();
        if (this.jsonObject.has("medications")) {
            try {
                JSONArray medications = (JSONArray) this.jsonObject.get("medications");
                int length = medications.length();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        sb.append(medications.getString(i));
                        if(i <length-1)
                            sb.append(", ");
                    }
                }
            } catch (Exception e) {

                System.out.println("toStringMedications: " + e.getMessage());

            }
        }
        return sb.toString();

    }
}