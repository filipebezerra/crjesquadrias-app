package br.com.libertsolutions.crs.app.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.base.BaseActivity;

/**
 * Application settings screen.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
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

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}
