package com.simplelilfe.focustodo.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.simplelilfe.focustodo.R;

/**
 * App Settings
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private OnFragmentInteractionListener mListener;

    /**
     * Shared Preferences Keys
     */
    public static final String PREF_SIGNED_IN = "signed_in";

    /**
     * SharedPreferences listener to update the Summary of the Preference
     */
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    Preference preference = findPreference(key);

                    switch (key) {
                        case PREF_SIGNED_IN:
                            String prefValue = prefs.getString(key, "");
                            if ("Not signed in".equalsIgnoreCase(prefValue)) {
                                preference.setSummary("Not signed in");
                                preference.setEnabled(false);
                            } else {
                                preference.setSummary(prefValue);
                                preference.setEnabled(true);
                                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                                    @Override
                                    public boolean onPreferenceClick(Preference preference) {
                                        signOutUser();
                                        return true;
                                    }
                                });
                            }
                            break;

                        default:
                            // TODO Handle later
                            break;
                    }
                }
            };

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Register the listener for preference value changes
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        if (getActivity() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Register the listener for preference value changes
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        if (getActivity() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Call the Listener to reflect the updated value for the key
        sharedPreferenceChangeListener.onSharedPreferenceChanged(getPreferenceManager().getSharedPreferences(), PREF_SIGNED_IN);
    }

    @Override
    public void onStop() {
        super.onStop();

        mListener = null;

        // Unregister the Listener
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void signOutUser() {
        mListener.onFragmentInteraction("sign_out");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String action);
    }
}
