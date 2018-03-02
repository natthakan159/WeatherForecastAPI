package com.weather.natthakan.weatherapi;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.squareup.picasso.Picasso;
import com.weather.natthakan.weatherapi.data.ForecastViewPagerAdapter;
import com.weather.natthakan.weatherapi.util.Prefs;
import com.weather.natthakan.weatherapi.model.Forecast;
import com.weather.natthakan.weatherapi.data.ForecastData;
import com.weather.natthakan.weatherapi.data.ForecastListAsyncResponse;



public class MainActivity extends AppCompatActivity {
    private ForecastViewPagerAdapter forecastViewPagerAdapter;
    private ViewPager viewPager;
    private TextView locationText;
    private TextView currentTempText, currentDate;
    private EditText userLocationText;
    private String userEnteredString;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText = findViewById(R.id.locationTextViewId);
        currentTempText = findViewById(R.id.currentTempId);
        currentDate = findViewById(R.id.currentDateId);

        icon = findViewById(R.id.weatherIcon);


        final Prefs prefs = new Prefs(this);
        String prefsLocation = prefs.getLocation();
        getWeather(prefsLocation);

        userLocationText = findViewById(R.id.locationNameId);

        userLocationText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (keycode == KeyEvent.KEYCODE_ENTER)) {

                    userEnteredString = userLocationText.getText().toString();
                    prefs.setLocation(userEnteredString);
                    getWeather(userEnteredString);

                    return true;
                }

                return false;
            }
        });

    }
    private String getImageUrl(String html) {

        String imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        // String htmlString = "<![CDATA[<img src=\"http://l.yimg.com/a/i/us/we/52/27.gif\"/> <BR /> <b>Current Conditions:</b> <BR />Mostly Cloudy <BR /> <BR /> <b>Forecast:</b> <BR /> Sat - Rain. High: 78Low: 74 <BR /> Sun - Mostly Cloudy. High: 78Low: 77 <BR /> Mon - Partly Cloudy. High: 78Low: 76 <BR /> Tue - Sunny. High: 78Low: 74 <BR /> Wed - Sunny. High: 80Low: 72 <BR /> <BR /> <a href=\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-1545228/\">Full Forecast at Yahoo! Weather</a> <BR /> <BR /> <BR /> ]]>";
        String imgSrc = null;

        Pattern p = Pattern.compile(imgRegex);
        Matcher m = p.matcher(html);

        while(m.find()) {
            imgSrc = m.group(1);

        }
        return imgSrc;
    }

    private void getWeather(String currentLocation) {



        forecastViewPagerAdapter = new ForecastViewPagerAdapter(getSupportFragmentManager(),
                getFragments(currentLocation));

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(forecastViewPagerAdapter);

    }

    private List<Fragment> getFragments(String locationString) {
        final List<Fragment> fragmentList = new ArrayList<>();

        new ForecastData().getForecast(new ForecastListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Forecast> forecastArrayList) {

                fragmentList.clear();
                String html = forecastArrayList.get(0).getDescriptionHTML();

                Picasso.with(getApplicationContext())
                        .load(getImageUrl(html))
                        .into(icon);

                locationText.setText(String.format("%s,\n%s", forecastArrayList.get(0).getCity(),
                        forecastArrayList.get(0).getRegion()));
                currentTempText.setText(String.format("%sF", forecastArrayList.get(0).getCurrentTemperature()));
                String[] date = forecastArrayList.get(0).getDate().split(" ");
                //Fri, 17 Nov 2017 03:00 PM PST
                String splitDate = "Today " + date[0] + " " + date[1] + " " +date[2] + " " + date[3];


                currentDate.setText(splitDate);


                for (int i = 0; i < forecastArrayList.size(); i++) {
                    Forecast forecast = forecastArrayList.get(i);
                    ForecastFragment forecastFragment =
                            ForecastFragment.newInstance(forecast);

                    fragmentList.add(forecastFragment);

                }
                forecastViewPagerAdapter.notifyDataSetChanged();


            }
        }, locationString);
        return fragmentList;
    }
}
