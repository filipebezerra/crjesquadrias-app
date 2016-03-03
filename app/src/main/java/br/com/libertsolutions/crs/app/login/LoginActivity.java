package br.com.libertsolutions.crs.app.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.form.FormUtil;
import br.com.libertsolutions.crs.app.keyboard.KeyboardUtil;
import butterknife.Bind;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;

public class LoginActivity extends BaseActivity {
    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_clear_24dp;
    }

    @Override
    protected int provideMenuResource() {
        return R.menu.menu_login;
    }

    @Bind(R.id.root_view) protected FrameLayout mRootView;
    @Bind(R.id.cpf) protected EditText mCpfView;
    @Bind(R.id.cpf_helper) protected TextInputLayout mCpfHelper;
    @Bind(R.id.password) protected EditText mPasswordView;
    @Bind(R.id.password_helper) protected TextInputLayout mPasswordHelper;

    private FormUtil mFormUtil = new FormUtil();

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                doLogin();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnFocusChange(R.id.cpf)
    public void onCpfViewFocusChange(boolean focused) {
        if (focused) {
            final Object cpf = mCpfView.getTag();

            if (cpf != null) {
                mCpfView.setText(cpf.toString());
                mCpfView.selectAll();
            }
        } else {
            if (!TextUtils.isEmpty(mCpfView.getText())
                    && mCpfView.getText().toString().length() == 11) {
                final String cpf = mCpfView.getText().toString();
                mCpfView.setText(LoginHelper.formatCpf(cpf));
                mCpfView.setTag(cpf);
            }
        }
    }

    @OnEditorAction(R.id.password)
    public boolean onPasswordEditorAction(int actionId) {
        if (actionId == getResources().getInteger(R.integer.entrar)) {
            doLogin();
            return true;
        }
        return false;
    }

    private void doLogin() {
        if (!mFormUtil.enableOrRemoveErrorInView(mCpfHelper, "CPF deve ser informado",
                mCpfView)) {
            mFormUtil.enableOrRemoveErrorInView(mCpfHelper, "CPF deve conter 11 dígitos",
                    mCpfView.getTag() != null || mCpfView.getText().length() == 11);
        }

        mFormUtil.enableOrRemoveErrorInView(mPasswordHelper, "Senha deve ser informada",
                mPasswordView);

        if (!mFormUtil.hasErrors()) {
            LoginHelper.loginUser(this, mCpfView.getTag().toString());
            finish();
        } else {
            final View currentFocus = getCurrentFocus();

            FeedbackHelper.snackbar(mRootView, "Corrija os erros para continuar com login",
                    true, new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            KeyboardUtil.showKeyboard(LoginActivity.this, currentFocus);
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            KeyboardUtil.hideKeyboard(LoginActivity.this, currentFocus);
                        }
                    });
        }
    }
}
