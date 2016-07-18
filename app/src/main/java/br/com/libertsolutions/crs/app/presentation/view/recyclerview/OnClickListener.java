package br.com.libertsolutions.crs.app.presentation.view.recyclerview;

import android.view.View;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface OnClickListener {
    void onSingleTapUp(View view, int position);

    void onLongPress(View view, int position);
}
