package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.DiscoveryDataResponse;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;


public class NetworkUtils {
    static final String API_KEY = "4296b3e8d8524f318a3d265d2f7825ec";
    static final String BASE_URL = "https://api.themoviedb.org/3/";

    static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String IMAGE_QUALITY = "w185";


    static final String DISCOVER_MOVIES_PATH = "discover/movie";
    static final String KEY_QUERY_PARAM = "api_key";
    static final String RELEASE_DATE_PARAM = "primary_release_date.gte";
    static final String SORT_PARAM = "sort_by";
    static final String SORT_BY = "primary_release_date.asc";
    static final String PAGE_PARAM = "page";

    final static String PARAM_QUERY = "q";

    final static String PARAM_SORT = "sort";
    final static String sortBy = "stars";

    public static Uri.Builder createUriBuilder(){
        Uri.Builder builder = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(KEY_QUERY_PARAM, API_KEY);
        return builder;
    }

    public static Uri buildImageUri(String imageUrlSuffix){
        Uri uri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                    .appendPath(IMAGE_QUALITY)
                    .appendEncodedPath(imageUrlSuffix)
                    .build();
        return uri;
    }

    public static URL buildDiscoverUrl(int page) {
        Uri builtUri = createUriBuilder()
                .appendEncodedPath(DISCOVER_MOVIES_PATH)
                .appendQueryParameter(RELEASE_DATE_PARAM, CommonUtils.getFormattedCurrentDate())
                .appendQueryParameter(SORT_PARAM, SORT_BY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static DiscoveryDataResponse getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, "Request: " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return new DiscoveryDataResponse(scanner.next());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}