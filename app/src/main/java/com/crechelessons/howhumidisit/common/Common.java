package com.crechelessons.howhumidisit.common;

import android.support.annotation.NonNull;

public class Common {

    //Replace the "API_KEY_HERE" with your own key after sign-up
    private static final String API_KEY = "API_KEY_HERE";
    private static final String API_LINK = "http://api.openweathermap.org/data/2.5/weather";

    @NonNull
    public static String apiRequest(double lat, double lng){
        return API_LINK+"?lat="+lat+"&lon="+lng+"&appid="+API_KEY;
    }

}
