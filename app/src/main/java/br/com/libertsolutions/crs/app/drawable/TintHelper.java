package br.com.libertsolutions.crs.app.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Classe utilitária para difinir cor de widgets com uma API fluída. Esta classe usa
 * {@link DrawableUtil} para delegar a tarefa final.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/01/2016
 * @since 0.1.0
 */
public class TintHelper {
    @NonNull private Context mContext;
    private Drawable mDrawable;
    private int mColor;

    public TintHelper(@NonNull Context context) {
        mContext = context;
    }

    public static TintHelper withContext(@NonNull Context context) {
        return new TintHelper(context);
    }

    public TintHelper withDrawable(@DrawableRes int drawableRes) {
        mDrawable = ContextCompat.getDrawable(mContext, drawableRes);
        return this;
    }

    public TintHelper withDrawable(@NonNull Drawable drawable) {
        mDrawable = drawable;
        return this;
    }

    public TintHelper withColor(@ColorRes int colorRes) {
        mColor = ContextCompat.getColor(mContext, colorRes);
        return this;
    }

    public Drawable tint() {
        if (mDrawable == null) {
            throw new NullPointerException("Especifique o ícone pelo método withDrawable()");
        }

        if (mColor == 0) {
            throw new IllegalStateException("Especifique a cor pelo método withColor()");
        }

        return DrawableUtil.tint(mDrawable, mColor);
    }
}
