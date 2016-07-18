package br.com.libertsolutions.crs.app.loginscreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.login.LoginBody;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.login.UserService;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.form.FormUtil;
import br.com.libertsolutions.crs.app.utils.keyboard.KeyboardUtil;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import com.afollestad.materialdialogs.MaterialDialog;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Tela de login, nesta usuários do sistema deverão logar para ter acesso aos dados obtidos do
 * servidor.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class LoginActivity extends BaseActivity {
    private FormUtil mFormUtil = new FormUtil();

    private Subscription mSubscription;

    @BindView(R.id.cpf) TextInputEditText mCpfView;
    @BindView(R.id.cpf_helper) TextInputLayout mCpfHelper;
    @BindView(R.id.password) TextInputEditText mPasswordView;
    @BindView(R.id.password_helper) TextInputLayout mPasswordHelper;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_clear_24dp;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        if (LoginHelper.isUserLogged(this)) {
            NavigationHelper.navigateToMainScreen(this);
            finish();
        } else if (!SettingsHelper.isSettingsApplied(this)) {
            NavigationHelper.navigateToSettingsScreen(this);
        }
    }

    @OnEditorAction(R.id.password)
    boolean onPasswordEditorAction(int actionId) {
        if (actionId == getResources().getInteger(R.integer.entrar)) {
            doLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.button_login)
    void onButtonLoginClick() {
        doLogin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogin() {
        if (!mFormUtil.enableOrRemoveErrorInView(mCpfHelper,
                getString(R.string.hint_cpf_required), mCpfView)) {
            mFormUtil.enableOrRemoveErrorInView(mCpfHelper,
                    getString(R.string.hint_cpf_invalid),
                    mCpfView.getTag() != null || mCpfView.getText().length() == 11);
        }

        mFormUtil.enableOrRemoveErrorInView(mPasswordHelper,
                getString(R.string.hint_password_required), mPasswordView);

        if (!mFormUtil.hasErrors()) {
            final String cpf = mCpfView.getTag().toString();
            final String password = mPasswordView.getText().toString();

            if (NetworkUtil.isDeviceConnectedToInternet(this)) {
                final UserService service = ServiceGenerator.createService(UserService.class, this);

                if (service != null) {
                    final MaterialDialog progressDialog = new MaterialDialog
                            .Builder(LoginActivity.this)
                            .title(R.string.title_dialog_signing_in)
                            .content(R.string.msg_dialog_signing_in)
                            .progress(true, 0)
                            .cancelable(true)
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    unsubscribeAuthentication();
                                }
                            })
                            .canceledOnTouchOutside(false)
                            .show();

                    mSubscription = service.authenticateUser(LoginBody.of(cpf, password))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    new Action1<User>() {
                                        @Override
                                        public void call(User user) {
                                            progressDialog.dismiss();
                                            validateUser(user);
                                        }
                                    },

                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable e) {
                                            progressDialog.dismiss();
                                            showAuthenticationError(e);
                                        }
                                    }
                            );
                } else {
                    validateAppSettings();
                }
            } else {
                showNetworkError();
            }
        } else {
            showFormError();
        }
    }

    private void unsubscribeAuthentication() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private void validateUser(User user) {
        if (LoginHelper.isValidUser(user)) {
            LoginHelper.loginUser(LoginActivity.this, user);
            NavigationHelper.navigateToMainScreen(LoginActivity.this);
            finish();
        } else {
            new MaterialDialog.Builder(LoginActivity.this)
                    .title(R.string.title_dialog_invalid_credentials)
                    .content(R.string.msg_dialog_invalid_credentials)
                    .positiveText(R.string.text_dialog_button_ok)
                    .show();
        }
    }

    private void showAuthenticationError(Throwable e) {
        Timber.e(e, "Authentication error");

        new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.title_dialog_sign_in_failed)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void validateAppSettings() {
        if (!SettingsHelper.isSettingsApplied(this)) {
            FeedbackHelper.snackbar(mRootView, getString(R.string.msg_settings_not_applied), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            NavigationHelper.navigateToSettingsScreen(LoginActivity.this);
                        }
                    });
        }
    }

    private void showNetworkError() {
        FeedbackHelper
                .snackbar(mRootView, getString(R.string.msg_unable_to_sign_in_no_network),
                        true);
    }

    private void showFormError() {
        final View currentFocus = getCurrentFocus();
        FeedbackHelper.snackbar(mRootView, getString(R.string.msg_fix_errors_to_sign_in),
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

    @OnFocusChange(R.id.cpf)
    void onCpfViewFocusChange(boolean focused) {
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

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribeAuthentication();
    }
}
