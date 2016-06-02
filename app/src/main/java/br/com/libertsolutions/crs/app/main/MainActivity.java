package br.com.libertsolutions.crs.app.main;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.utils.drawable.DrawableHelper;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkAdapter;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Tela principal, nesta são listadas as obras cadastradas no servidor.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 01/06/2016
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity implements OnClickListener,
        SearchView.OnQueryTextListener {

    private WorkAdapter mWorkAdapter;

    private WorkDataService mWorkDataService;

    private Subscription mWorkDataSubscription;

    private User mUserLogged;

    @Bind(R.id.list) RecyclerView mWorksView;
    @Bind(R.id.empty_state) LinearLayout mEmptyStateView;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setupActionBar();
        setupRecyclerView();
        loadViewData();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        if (mToolbarAsActionBar != null) {
            final Drawable navigationIcon = DrawableHelper.withContext(this)
                    .withColor(R.color.white)
                    .withDrawable(R.drawable.ic_worker)
                    .tint()
                    .get();

            mToolbarAsActionBar.setNavigationIcon(navigationIcon);
        }
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        mWorksView.addItemDecoration(new GridDividerDecoration(this));
        mWorksView.setHasFixedSize(true);
        mWorksView.addOnItemTouchListener(new OnTouchListener(this, mWorksView, this));
    }

    private void loadViewData() {
        showUserLoggedInfo();

        final boolean isInitialDataImported = ConfigHelper.isInitialDataImported(this);

        if (!isInitialDataImported) {
            showEmptyState();
        } else {
            loadWorkData();
        }
    }

    private void showUserLoggedInfo() {
        if (mUserLogged == null) {
            mUserLogged = LoginHelper.getUserLogged(this);
        }

        if (mUserLogged != null) {
            FeedbackHelper
                    .snackbar(mRootView, String.format("Logado como %s.",
                            LoginHelper.formatCpf(mUserLogged.getName())), false);
        }
    }

    private void showEmptyState() {
        mWorksView.setVisibility(View.GONE);
        mEmptyStateView.setVisibility(View.VISIBLE);
        // TODO: enable BroadcastReceiver to network events
    }

    private void loadWorkData() {
        if (mWorkDataService == null) {
            mWorkDataService = new WorkRealmDataService(this);
        }

        mWorkDataSubscription = mWorkDataService.list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<Work>>() {
                            @Override
                            public void call(List<Work> list) {
                                showWorkData(list);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_loading_data_from_local, e);
                            }
                        }
                );
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        Crashlytics.logException(e);

        new MaterialDialog.Builder(MainActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void showWorkData(List<Work> list) {
        mWorksView.setAdapter(mWorkAdapter = new WorkAdapter(MainActivity.this, list));
        updateSubtitle();
    }

    private void updateSubtitle() {
        if (mWorkAdapter != null) {
            final int count = mWorkAdapter.getRunningWorksCount();
            if (count == 0) {
                setSubtitle(getString(R.string.no_work_running));
            } else {
                setSubtitle(getString(R.string.works_running,
                        count));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mWorkDataSubscription != null && mWorkDataSubscription.isUnsubscribed()) {
            mWorkDataSubscription.unsubscribe();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LoginHelper.logoutUser(this);
                finish();
                return true;

            case R.id.action_settings:
                NavigationHelper.navigateToSettingsScreen(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onSingleTapUp(View view, int position) {
        if (mWorkAdapter != null) {
            final Work item = mWorkAdapter.getItem(position);

            if (item != null) {
                NavigationHelper.navigateToFlowScreen(this, item.getWorkId());
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {}

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mWorkAdapter != null) {
            mWorkAdapter.getFilter().filter(newText);
            return true;
        } else {
            return false;
        }
    }
}
