package br.com.libertsolutions.crs.app.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.application.RequestCodes;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.login.LoginActivity;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.project.ProjectAdapter;
import br.com.libertsolutions.crs.app.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivityCompat;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import butterknife.ButterKnife;

/**
 * Application main screen.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity {

    private ProjectAdapter mProjectAdapter;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        if (!SettingsHelper.isAppliedOnFirstRun(this)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
                startActivityForResult(
                        SettingsActivity.getLauncherIntent(getApplicationContext()),
                        RequestCodes.LAUNCH_SETTINGS_SCREEN);
            else
                startActivityForResult(
                        SettingsActivityCompat.getLauncherIntent(getApplicationContext()),
                        RequestCodes.LAUNCH_SETTINGS_SCREEN);
        } else
            checkOutIfNeedToLogInUser();

        RecyclerView mProjectsView = ButterKnife.findById(this, R.id.list);
        mProjectsView.setLayoutManager(new LinearLayoutManager(this));
        mProjectsView.setHasFixedSize(true);
        mProjectsView.setAdapter(mProjectAdapter = new ProjectAdapter());
        mProjectsView.addItemDecoration(new DividerDecoration(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.LAUNCH_SETTINGS_SCREEN:
                checkOutIfNeedToLogInUser();
                break;

            case RequestCodes.LAUNCH_LOGIN_SCREEN:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkOutIfNeedToLogInUser() {
        if (!LoginHelper.isUserLogged(this)) {
            startActivityForResult(
                    LoginActivity.getLauncherIntent(getApplicationContext()),
                    RequestCodes.LAUNCH_LOGIN_SCREEN);
        }
    }
}
