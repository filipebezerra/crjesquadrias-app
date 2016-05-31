package br.com.libertsolutions.crs.app.utils.form;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/01/2016
 * @since 0.1.0
 */
public class FormUtil {
    /**
     * Mapeia todas view's com erro de validação de entrada de dados ou erro de regra de negócio
     * relacionado com dados presentes na View.
     *
     * As View's devem ser {@link TextView} ou {@link TextInputLayout}
     */
    private Set<View> mViewsWithErrors = new HashSet<>();

    /**
     * Remove o erro da View.
     *
     * @param errorView A view que contém o erro. Deve ser TextView ou TextInputLayout
     */
    public void removeError(@NonNull View errorView) {
        if (errorView instanceof TextView) {
            ((TextView) errorView).setText("");
            errorView.setVisibility(View.GONE);
        } else if (errorView instanceof TextInputLayout) {
            ((TextInputLayout) errorView).setError("");
            errorView.setEnabled(false);
        }
    }

    /**
     * Remove a View do mapeamento como também remove o erro visível nela.
     *
     * @param errorView A view que contém o erro. Deve ser TextView ou TextInputLayout
     */
    public void removeErrorInView(@NonNull View errorView) {
        if (mViewsWithErrors.remove(errorView)) {
            removeError(errorView);
        }
    }

    /**
     * Remove todas View's do mapeamento como também remove erro visível nela.
     */
    public void clearErrorsInViews() {
        for (Iterator<View> iterator = mViewsWithErrors.iterator(); iterator.hasNext();) {
            final View view = iterator.next();
            removeError(view);
            iterator.remove();
        }
    }

    /**
     * Adiciona a View no mapeamento e torna visível o erro.
     *
     * @param errorView A view que contém o erro. Deve ser TextView ou TextInputLayout
     * @param error A mensagem de erro
     */
    public void enableErrorInView(@NonNull View errorView, @NonNull String error) {
        if (mViewsWithErrors.add(errorView)) {
            if (errorView instanceof TextView) {
                errorView.setVisibility(View.VISIBLE);
                ((TextView) errorView).setText(error);
            } else if (errorView instanceof TextInputLayout) {
                errorView.setEnabled(true);
                ((TextInputLayout) errorView).setError(error);
            }
        }
    }

    /**
     * Auto identifica se a View deve visualizar o erro ou deve remover o erro baseado em
     * seu estado.
     *
     * @param errorView A view que contém o erro. Deve ser TextView ou TextInputLayout
     * @param error A mensagem de erro
     * @param editView A View que recebe entrada de dados
     * return {@code true} se foi habilitado ou  {@code false} se foi removido.
     */
    public boolean enableOrRemoveErrorInView(@NonNull View errorView, @NonNull String error,
                                             @NonNull EditText editView) {
        if (TextUtils.isEmpty(editView.getText())) {
            enableErrorInView(errorView, error);
            return true;
        } else {
            removeErrorInView(errorView);
            return false;
        }
    }

    public boolean enableOrRemoveErrorInView(@NonNull View errorView, @NonNull String error,
                                             boolean checkTrue) {
        if (!checkTrue) {
            enableErrorInView(errorView, error);
            return true;
        } else {
            removeErrorInView(errorView);
            return false;
        }
    }

    public boolean hasErrors() {
        return mViewsWithErrors.size() != 0;
    }
}