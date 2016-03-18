package br.com.libertsolutions.crs.app.android.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.drawable.DrawableHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Abstração base de {@code Activity} para todas {@code Activity}es deste projeto.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 17/01/2016
 * @since 0.1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static final int NO_UP_INDICATOR = -1;
    protected static final int NO_MENU = -1;
    protected static final long INVALID_EXTRA_ID = -1;

    @Bind(R.id.root_view) protected ViewGroup mRootView;
    @Nullable @Bind(R.id.toolbar) protected Toolbar mToolbarAsActionBar;

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

            for (int i = 0; i <= menu.size() - 1; i++) {
                final MenuItem menuItem = menu.getItem(i);
                final Drawable icon = menuItem.getIcon();

                if (icon != null) {
                    DrawableHelper.withContext(this)
                            .withDrawable(icon)
                            .withColor(R.color.white)
                            .tint()
                            .applyTo(menuItem);
                }
            }

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
                actionBar.setHomeAsUpIndicator(
                        DrawableHelper.withContext(this)
                                .withDrawable(provideUpIndicatorResource())
                                .withColor(R.color.white)
                                .tint()
                                .get());
            }
        }
    }

    public void setSubtitle(@NonNull CharSequence subtitle) {
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