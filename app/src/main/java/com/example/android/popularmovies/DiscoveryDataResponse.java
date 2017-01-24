package com.example.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kjs566 on 1/23/2017.
 */

public class DiscoveryDataResponse extends JSONObject {
    public DiscoveryDataResponse(String json) throws JSONException {
        super(json);
    }

    public JSONArray getResults() throws JSONException {
        return getJSONArray("results");
    }

    public JSONObject getItem(int position) throws JSONException {
        return getResults().getJSONObject(position);
    }

    public int getPage() throws JSONException {
        return getInt("page");
    }

    public int getTotalResults() throws JSONException {
        return getInt("total_results");
    }

    public int getTotalPages() throws JSONException{
        return getInt("total_pages");
    }

    public int size(){
        int len = 0;
        try {
            len = getResults().length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return len;
    }
}
