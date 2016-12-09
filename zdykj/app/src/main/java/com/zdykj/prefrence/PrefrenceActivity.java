package com.zdykj.prefrence;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.zdykj.R;


public class PrefrenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.pre_01);

        EditTextPreference checkbox = (EditTextPreference) findPreference("edit");


        checkbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Toast.makeText(PrefrenceActivity.this, "edit改变的值为" + newValue, Toast.LENGTH_LONG).show();

                return true;

            }
        });

        Preference preference0 = findPreference("preference0");

        preference0.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Toast.makeText(PrefrenceActivity.this, "value=", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

    }


}
