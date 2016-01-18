package br.com.libertsolutions.crs.app.keyboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Keyboard utilities.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/01/2016
 * @since 0.1.0
 */
public class KeyboardUtil {
    private KeyboardUtil() {
        // no instances
    }

    public static void focusThenShowKeyboard(@NonNull final Context context,
            @NonNull final View view) {
        if (view.isShown() && view.isFocusable()) {
            if (view.requestFocus()) {
                showKeyboard(context, view);
            }
        }
    }

    public static void showKeyboard(@NonNull final Context context, final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view.findFocus(), 0);
            }
        }, 50);
    }

    public static void hideKeyboard(@NonNull Context context, View currentFocus) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }
}