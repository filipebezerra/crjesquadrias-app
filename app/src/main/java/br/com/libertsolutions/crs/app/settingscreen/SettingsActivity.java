package br.com.libertsolutions.crs.app.settingscreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;

/**
 * Application settings screen.
 *
 * @author Filipe Bezerra
 * @version 0.1.0
 * @since 0.1.0
 */
@SuppressLint("NewApi")
public class SettingsActivity extends BaseActivity {

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getFragmentManager().findFragmentById(R.id.root_view) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_view, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        private static Preference.OnPreferenceChangeListener sOnPreferenceChangeListener
                = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                if (preference instanceof EditTextPreference) {
                    if (!TextUtils.isEmpty(stringValue)) {
                        preference.setSummary(stringValue);
                    }
                }

                return true;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            bindPreferenceSummaryToValue(findPreference(
                    getString(R.string.server_url_pref_key)));
            bindPreferenceSummaryToValue(findPreference(
                    getString(R.string.server_auth_key_pref_key)));
        }

        private static void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(sOnPreferenceChangeListener);

            sOnPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }
}
