package br.com.libertsolutions.crs.app.recyclerview;

import android.view.View;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 3.0.0, 29/01/2016
 * @since 3.0.0
 */
public interface OnClickListener {
    void onSingleTapUp(View view, int position);

    void onLongPress(View view, int position);
}
