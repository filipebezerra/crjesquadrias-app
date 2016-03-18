package br.com.libertsolutions.crs.app.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.application.RequestCodes;
import br.com.libertsolutions.crs.app.drawable.DrawableHelper;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.launchscreen.LaunchScreenActivity;
import br.com.libertsolutions.crs.app.login.LoginActivity;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import br.com.libertsolutions.crs.app.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivityCompat;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import br.com.libertsolutions.crs.app.step.WorkStepActivity;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkAdapter;
import br.com.libertsolutions.crs.app.work.WorkEntity;
import br.com.libertsolutions.crs.app.work.WorkService;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Tela principal, nesta são listadas as obras cadastradas no servidor.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity implements OnClickListener {

    private WorkAdapter mWorkAdapter;

    @Bind(R.id.list) RecyclerView mWorksView;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        showLaunchScreen();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        changeListLayout(getResources().getConfiguration());
        mWorksView.addItemDecoration(new GridDividerDecoration(this));
        mWorksView.setHasFixedSize(true);
        mWorksView.addOnItemTouchListener(
                new OnTouchListener(this, mWorksView, this));

        if (mToolbarAsActionBar != null) {
            final Drawable navigationIcon = DrawableHelper.withContext(this)
                    .withColor(R.color.white)
                    .withDrawable(R.drawable.ic_worker)
                    .tint()
                    .get();

            mToolbarAsActionBar.setNavigationIcon(navigationIcon);
        }
    }

    private void showLaunchScreen() {
        startActivityForResult(
                LaunchScreenActivity.getLauncherIntent(this),
                RequestCodes.LAUNCH_BRAND_SCREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: Redefinir fluxo das telas para: LaunchScreen -> Login -> Main
        //      para que fique claro e sem muito controle de fluxo no código
        switch (requestCode) {
            case RequestCodes.LAUNCH_BRAND_SCREEN:
                if (!SettingsHelper.isSettingsApplied(this)) {
                    showSettingsScreen();
                } else if (!LoginHelper.isUserLogged(this)) {
                    showLoginScreen();
                } else {
                    showUserLoggedInfo();
                }
                break;

            case RequestCodes.LAUNCH_SETTINGS_SCREEN:
                if (!SettingsHelper.isSettingsApplied(this)) {
                    finish();
                }

                if (!LoginHelper.isUserLogged(this)) {
                    showLoginScreen();
                }
                break;

            case RequestCodes.LAUNCH_LOGIN_SCREEN:
                if (!LoginHelper.isUserLogged(this)) {
                    finish();
                } else {
                    showUserLoggedInfo();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showUserLoggedInfo() {
        final User userLogged = LoginHelper.getUserLogged(this);
        if (userLogged != null) {
            FeedbackHelper
                    .snackbar(mRootView, String.format("Logado como %s.",
                            LoginHelper.formatCpf(userLogged.getName())), false);
        }

        final WorkService service = RetrofitHelper
                .createService(WorkService.class, this);

        if (service != null) {
            service.getAllRunning()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<Work>>() {
                        @Override
                        public void onCompleted() {
                            final int count = mWorkAdapter.getRunningWorksCount();
                            if (count == 0) {
                                setSubtitle(getString(R.string.no_work_running));
                            } else {
                                setSubtitle(getString(R.string.works_running,
                                        count));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title("Falha ao tentar carregar dados")
                                    .content(e.getMessage())
                                    .positiveText("OK")
                                    .show();
                        }

                        @Override
                        public void onNext(List<Work> works) {
                            mWorksView.setAdapter(mWorkAdapter =
                                    new WorkAdapter(MainActivity.this, WorkEntity.of(works)));
                        }
                    });
        }
    }

    private void showLoginScreen() {
        startActivityForResult(
                LoginActivity.getLauncherIntent(getApplicationContext()),
                RequestCodes.LAUNCH_LOGIN_SCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mWorkAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LoginHelper.logoutUser(this);
                finish();
                return true;

            case R.id.action_settings:
                showSettingsScreen();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettingsScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            startActivityForResult(
                    SettingsActivity.getLauncherIntent(getApplicationContext()),
                    RequestCodes.LAUNCH_SETTINGS_SCREEN);
        } else {
            startActivityForResult(
                    SettingsActivityCompat.getLauncherIntent(getApplicationContext()),
                    RequestCodes.LAUNCH_SETTINGS_SCREEN);
        }
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        final WorkEntity item = mWorkAdapter.getItem(position);

        if (item != null) {
            startActivity(WorkStepActivity.getLauncherIntent(this, item.getWorkId()));
        }
    }

    @Override
    public void onLongPress(View view, int position) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeListLayout(newConfig);
    }

    private void changeListLayout(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mWorksView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mWorksView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
