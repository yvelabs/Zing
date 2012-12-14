package com.yvelabs.zing;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class ZingSetting extends PreferenceActivity {
	
	private EditTextPreference maxrowNumbers;
	private CheckBoxPreference cumstomerLanguage;
	private ListPreference selectLanguage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.zing_setting);
		
		maxrowNumbers = (EditTextPreference) this.findPreference(getResources().getString(R.string.set_max_row_numbers_key));
		cumstomerLanguage = (CheckBoxPreference) this.findPreference(getResources().getString(R.string.customer_language_key));
		selectLanguage = (ListPreference) this.findPreference(getResources().getString(R.string.select_language_key));
		
		cumstomerLanguage.setDefaultValue(false);
		
		
		maxrowNumbers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				return true;
			}
		});
		
		cumstomerLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				return true;
			}
		});
		
		selectLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				return true;
			}
		});
	}

}
