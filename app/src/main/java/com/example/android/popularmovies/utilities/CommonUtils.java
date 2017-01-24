package com.example.android.popularmovies.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kjs566 on 1/23/2017.
 */

public class CommonUtils {
    public static String getFormattedCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(Calendar.getInstance().getTime());
    }
}
