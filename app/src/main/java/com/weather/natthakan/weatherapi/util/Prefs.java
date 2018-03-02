package com.weather.natthakan.weatherapi.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by natthakan on 2/3/2018 AD.
 */

public class Prefs {
    SharedPreferences preferences;

    public Prefs(Activity activity){
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);

    }

    public void setLocation (String location){
        preferences.edit().putString("location", location).apply();
    }

   public String getLocation() {
        return preferences.getString("location", "Bangkok");
   }
}
