package br.com.libertsolutions.crs.app.presentation.activity;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import br.com.libertsolutions.crs.app.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static final int NO_UP_INDICATOR = -1;
    protected static final int NO_MENU = -1;

    @BindView(R.id.root_view) protected ViewGroup mRootView;
    @Nullable @BindView(R.id.toolbar) protected Toolbar mToolbarAsActionBar;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(provideLayoutResource());
        ButterKnife.bind(this);
        setupToolbarAsActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (provideMenuResource() != NO_MENU) {
            getMenuInflater().inflate(provideMenuResource(), menu);

            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    private void setupToolbarAsActionBar() {
        if (mToolbarAsActionBar != null) {
            setSupportActionBar(mToolbarAsActionBar);

            final ActionBar actionBar = getSupportActionBar();

            if (actionBar != null
                    && provideUpIndicatorResource() != NO_UP_INDICATOR) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(provideUpIndicatorResource());
            }
        }
    }

    public void setSubtitle(@Nullable CharSequence subtitle) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    @LayoutRes
    protected abstract int provideLayoutResource();

    @DrawableRes
    protected int provideUpIndicatorResource() {
        return NO_UP_INDICATOR;
    }

    @MenuRes
    protected int provideMenuResource() {
        return NO_MENU;
    }
}