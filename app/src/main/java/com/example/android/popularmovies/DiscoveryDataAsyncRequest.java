package com.example.android.popularmovies;

import android.os.AsyncTask;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by kjs566 on 1/23/2017.
 */

public class DiscoveryDataAsyncRequest extends AsyncTask<Integer, Void, DiscoveryDataResponse>{

    @Override
    protected DiscoveryDataResponse doInBackground(Integer... params) {
        DiscoveryDataResponse result = null;
        int page = 1;
        if(params.length >= 1)
            page = params[0];

        URL url = NetworkUtils.buildDiscoverUrl(page);
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
