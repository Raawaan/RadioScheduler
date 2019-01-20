package com.example.rawan.radio

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class PreferencesHelperFragment:PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.shared_pref)
    }
}