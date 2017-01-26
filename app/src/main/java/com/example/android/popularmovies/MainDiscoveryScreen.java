package com.example.android.popularmovies;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class MainDiscoveryScreen extends AppCompatActivity {
    public static final String TAG = MainDiscoveryScreen.class.getName();

    public static final int columns = 4;

    RecyclerView discoveryRecyclerView;
    DiscoveryAdapter adapter;

    DiscoveryDataCache discoveryData;

    int columnWidth;
    int columnHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery_screen);

        Configuration configuration = getResources().getConfiguration();
        int screenWidth = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.

        columnWidth = screenWidth / columns;
        columnHeight = (int) Math.round(columnWidth * 1.5);

        discoveryRecyclerView = (RecyclerView) findViewById(R.id.rv_discovery_list);

        adapter = new DiscoveryAdapter();
        discoveryRecyclerView.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        discoveryRecyclerView.setLayoutManager(layoutManager);
        //new LinearLayoutManager(this)

        discoveryData = new DiscoveryDataCache();
        discoveryData.setDataChangedListener(adapter);

        discoveryData.loadNextPage();
    }


    class DiscoveryViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar loadingNextPageSpinner;
        public ImageView movieImageView;

        public DiscoveryViewHolder(View itemView) {
            super(itemView);

            loadingNextPageSpinner = (ProgressBar) itemView.findViewById(R.id.pb_loading_next_page);
            movieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_image);
        }
    }

    class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryViewHolder> implements DiscoveryDataCache.DataChangedListener{

        @Override
        public DiscoveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovery_item, parent, false);

            /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    columnWidth,
                    columnHeight);
            itemView.setLayoutParams(params);*/

            return new DiscoveryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DiscoveryViewHolder holder, int position) {
            Log.d(TAG, "onBindHolder: " + position);
            if(discoveryData.getCount() > position){
                try {
                    JSONObject item = discoveryData.getItem(position);
                    if(item != null) {
                        holder.loadingNextPageSpinner.setVisibility(View.GONE);

                        String posterUrl = item.getString("poster_path");
                        if(posterUrl != null && !"null".equals(posterUrl)){
                            Uri imageUri = NetworkUtils.buildImageUri(posterUrl);
                            Log.d(TAG, "Loading image " + imageUri.toString());
                            Picasso.with(MainDiscoveryScreen.this)
                                    .load(imageUri)
                                    //.resize(columnWidth, columnHeight)
                                    //.centerCrop()
                                    //.fit()
                                    .into(holder.movieImageView);
                        }else{
                            Picasso.with(MainDiscoveryScreen.this).cancelRequest(holder.movieImageView);
                        }
                    }else{
                        holder.loadingNextPageSpinner.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(discoveryData.getCount() >= 1 && discoveryData.getCount() - 1 == position){
                discoveryData.loadNextPage();
            }
        }

        @Override
        public int getItemCount() {
            if(discoveryData == null)
                return 0;
            else return discoveryData.getCount();
        }

        @Override
        public void onDataChanged() {
            discoveryRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }
}
