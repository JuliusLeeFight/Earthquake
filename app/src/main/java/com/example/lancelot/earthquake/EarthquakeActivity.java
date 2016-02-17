package com.example.lancelot.earthquake;

;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;


public class EarthquakeActivity extends Activity {

    static final private int MENU_PREFERENCES = Menu.FIRST+1;
    static final private int MENU_UPDATE = Menu.FIRST+2;
    private static final int SHOW_PREFERENCES = 1;

    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        updateFromPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_earthquake, menu);
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        return true;
    }

    private void updateFromPreferences(){
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int minMagIndex = sharedPreferences.getInt(PreferencesActivity.PREF_MIN_MAG_INDEX,0);
        if (minMagIndex<0)
            minMagIndex = 0;


        int freqIndex = sharedPreferences.getInt(PreferencesActivity.PREF_UPDATE_FREQ_INDEX,0);
        if (freqIndex<0)
            freqIndex = 0;

        autoUpdateChecked = sharedPreferences.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE,false);
        Resources resources = getResources();
        String[] minMagValue = resources.getStringArray(R.array.magnitude_options);
        String[] freqValues = resources.getStringArray(R.array.update_freq_options);

        minimumMagnitude = Integer.valueOf(minMagValue[minMagIndex]);
        updateFreq = Integer.valueOf(freqValues[freqIndex]);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case(MENU_PREFERENCES):{
                Intent intent = new Intent(this,PreferencesActivity.class);
                startActivityForResult(intent,SHOW_PREFERENCES);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCES){
            if (resultCode == RESULT_OK){
                updateFromPreferences();
                FragmentManager fm = getFragmentManager();
                final EarthquakeListFragment earthquakeList = (EarthquakeListFragment)fm.findFragmentById(R.id.EarthequakeListFragment);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        earthquakeList.refreshEarthquake();
                    }
                });

                t.start();
            }
        }
    }
}
