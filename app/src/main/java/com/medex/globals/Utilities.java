package com.medex.globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by dhruv on 02-12-2016.
 */

public class Utilities {

    public static LinkedList<JSONObject> jsonArrayToLinkedList(JSONArray jsonArray){
            LinkedList<JSONObject> list = new LinkedList<JSONObject>();
            for(int i=0; i<jsonArray.length(); i++)
                try {
                    list.add(jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return list;
    }
    public static JSONArray LinkedListTOJSONArray(LinkedList<JSONObject> list){
        JSONArray jarray = new JSONArray();

        for(JSONObject json: list)
            jarray.put(json);

        return jarray;
    }
}
