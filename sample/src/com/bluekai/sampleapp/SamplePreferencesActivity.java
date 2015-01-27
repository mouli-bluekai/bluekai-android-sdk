package com.bluekai.sampleapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SamplePreferencesActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
