package com.hidayatasep.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by hidayatasep43 on 6/28/2017.
 */

public class MyPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_screen);
    }

}
