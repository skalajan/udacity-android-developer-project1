package com.example.android.popularmovies;

import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kjs566 on 1/23/2017.
 */

public class DiscoveryDataCache {
    private static final String TAG = DiscoveryDataCache.class.getName();

    ArrayList<DiscoveryDataResponse> discoveryResponses;
    ArrayList<Integer> indexesFrom;
    ArrayList<Integer> indexesTo;

    int loadedCount = 0;

    boolean loadingNewData = false;
    private DataChangedListener dataChangedListener;

    DiscoveryDataCache(){
        discoveryResponses = new ArrayList<>();
    }

    private int recountItems(){
        loadedCount = 0;
        int from = 0;
        int to = 0;

        indexesFrom = new ArrayList<>(discoveryResponses.size());
        indexesTo = new ArrayList<>(discoveryResponses.size());

        for(int i = 0; i < discoveryResponses.size(); i++){
            int currentSize = discoveryResponses.get(i).size();
            loadedCount += currentSize;
            indexesFrom.add(from);
            indexesTo.add(from + currentSize -1);

            from += currentSize;
        }
        return loadedCount;
    }

    public int getCount(){
        if(loadingNewData)
            return loadedCount + 1;
        return loadedCount;
    }

    public JSONObject getItem(int position) throws JSONException {
        if(loadingNewData && position == getCount())
            return null;

        JSONObject item = null;
        for(int i = 0; i < discoveryResponses.size(); i++){
            if(indexesFrom.get(i) <= position && indexesTo.get(i) >= position) {
                item = discoveryResponses.get(i).getItem(position - indexesFrom.get(i));
                break;
            }
        }

        return item;
    }

    public void setDataChangedListener(DataChangedListener listener){
        this.dataChangedListener = listener;
    }

    public void loadNextPage(){
        if(!loadingNewData) {
            int nextPage = discoveryResponses.size() + 1;

            try {
                if (discoveryResponses.size() == 0 || (discoveryResponses.size() > 0 && discoveryResponses.get(0).getTotalPages() >= nextPage)) {
                    new DiscoveryRequest().execute(nextPage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onLoadingNewData(){

    }

    protected void dataReceived(DiscoveryDataResponse discoveryData){
        try {
            if(discoveryData.getPage() >= discoveryResponses.size()){
                Log.v(TAG, "New page added");
                discoveryResponses.add(discoveryData);
            }else{
                discoveryResponses.set(discoveryData.getPage(), discoveryData);
            }
            notifyDataChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void notifyDataChanged(){
        recountItems();
        Log.d(TAG, "Data changed, new count " + getCount());
        if(dataChangedListener != null){
            dataChangedListener.onDataChanged();
        }
    }

    class DiscoveryRequest extends DiscoveryDataAsyncRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingNewData = true;
            notifyDataChanged();
            onLoadingNewData();
        }

        @Override
        protected void onPostExecute(DiscoveryDataResponse jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject != null){
                dataReceived(jsonObject);
            }
            loadingNewData = false;
        }
    }

    public interface DataChangedListener{
        void onDataChanged();
    }
}
