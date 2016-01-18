package br.com.libertsolutions.crs.app.drawable;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Classe utilitária para modificar {@link Drawable}s.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/01/2016
 * @since 0.1.0
 */
public class DrawableUtil {
    private DrawableUtil() {
        // no instances
    }

    public static Drawable tint(@NonNull Drawable drawable, @ColorInt int color) {
        drawable = drawable.mutate();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
