package br.com.libertsolutions.crs.app.presentation.login;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.data.login.LoginDataHelper;
import br.com.libertsolutions.crs.app.data.login.UserService;
import br.com.libertsolutions.crs.app.domain.pojo.User;
import br.com.libertsolutions.crs.app.presentation.activity.BaseActivity;
import br.com.libertsolutions.crs.app.presentation.util.FormUtils;
import br.com.libertsolutions.crs.app.presentation.util.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import com.afollestad.materialdialogs.MaterialDialog;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static br.com.libertsolutions.crs.app.data.login.LoginDataHelper.isUserLogged;
import static br.com.libertsolutions.crs.app.data.login.LoginDataHelper.isValidUser;
import static br.com.libertsolutions.crs.app.data.login.LoginDataHelper.loginUser;
import static br.com.libertsolutions.crs.app.data.settings.SettingsDataHelper.isSettingsApplied;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static br.com.libertsolutions.crs.app.domain.pojo.LoginBody.newLoginBody;
import static br.com.libertsolutions.crs.app.presentation.util.FeedbackHelper.snackbar;
import static br.com.libertsolutions.crs.app.presentation.util.NavigationHelper.navigateToMainScreen;
import static br.com.libertsolutions.crs.app.presentation.util.NavigationHelper.navigateToSettingsScreen;
import static br.com.libertsolutions.crs.app.presentation.util.NetworkUtils.isDeviceConnectedToInternet;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class LoginActivity extends BaseActivity {

    private FormUtils formUtils = new FormUtils();

    private Subscription subscription;

    private UserService userService;

    @BindView(R.id.cpf) TextInputEditText inputLayoutCpf;
    @BindView(R.id.cpf_helper) TextInputLayout inputLayoutCpfHelper;
    @BindView(R.id.password) TextInputEditText inputLayoutPassword;
    @BindView(R.id.password_helper) TextInputLayout inputLayoutPasswordHelper;

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
        if (isUserLogged(this)) {
            navigateToMainScreen(this);
            finish();
        } else if (!isSettingsApplied(this)) {
            navigateToSettingsScreen(this);
        }
    }

    @OnEditorAction(R.id.password) boolean onPasswordEditorAction(int actionId) {
        if (actionId == getResources().getInteger(R.integer.entrar)) {
            doLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.button_login) void onButtonLoginClick() {
        doLogin();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogin() {
        if (!formUtils.enableOrRemoveErrorInView(inputLayoutCpfHelper,
                getString(R.string.hint_cpf_required), inputLayoutCpf)) {
            formUtils.enableOrRemoveErrorInView(inputLayoutCpfHelper,
                    getString(R.string.hint_cpf_invalid),
                    inputLayoutCpf.getTag() != null || inputLayoutCpf.getText().length() == 11);
        }

        formUtils.enableOrRemoveErrorInView(inputLayoutPasswordHelper,
                getString(R.string.hint_password_required), inputLayoutPassword);

        if (formUtils.hasErrors()) {
            showFormError();
            return;
        }

        if (!isDeviceConnectedToInternet(this)) {
            showNetworkError();
            return;
        }

        if (userService() == null) {
            validateAppSettings();
            return;
        }

        final MaterialDialog progressDialog = new MaterialDialog
                .Builder(this)
                .title(R.string.title_dialog_signing_in)
                .content(R.string.msg_dialog_signing_in)
                .progress(true, 0)
                .cancelable(true)
                .cancelListener(dialog -> unsubscribeAuthentication())
                .canceledOnTouchOutside(false)
                .build();

        final String cpf = inputLayoutCpf.getTag().toString();
        final String password = inputLayoutPassword.getText().toString();

        subscription = userService().authenticateUser(newLoginBody(cpf, password))
                .observeOn(mainThread())
                .subscribe(
                        new Subscriber<User>() {
                            @Override public void onStart() {
                                progressDialog.show();
                            }

                            @Override public void onError(final Throwable e) {
                                progressDialog.dismiss();
                                showAuthenticationError(e);
                            }

                            @Override public void onNext(final User user) {
                                progressDialog.dismiss();
                                validateUser(user);
                            }

                            @Override public void onCompleted() {}
                        }
                );
    }

    private UserService userService() {
        if (userService == null) {
            userService = createService(UserService.class, this);
        }
        return userService;
    }

    private void unsubscribeAuthentication() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void validateUser(User user) {
        if (isValidUser(user)) {
            loginUser(this, user);
            navigateToMainScreen(this);
            finish();
        } else {
            new MaterialDialog.Builder(this)
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
        if (!isSettingsApplied(this)) {
            snackbar(mRootView, getString(R.string.msg_settings_not_applied), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            navigateToSettingsScreen(LoginActivity.this);
                        }
                    });
        }
    }

    private void showNetworkError() {
        snackbar(mRootView, getString(R.string.msg_unable_to_sign_in_no_network), true);
    }

    private void showFormError() {
        final View currentFocus = getCurrentFocus();
        snackbar(mRootView, getString(R.string.msg_fix_errors_to_sign_in),
                true, new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        KeyboardUtils.showKeyboard(LoginActivity.this, currentFocus);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        KeyboardUtils.hideKeyboard(LoginActivity.this, currentFocus);
                    }
                });
    }

    @OnFocusChange(R.id.cpf) void onCpfViewFocusChange(boolean focused) {
        if (focused) {
            final Object cpf = inputLayoutCpf.getTag();

            if (cpf != null) {
                inputLayoutCpf.setText(cpf.toString());
                inputLayoutCpf.selectAll();
            }
        } else {
            if (!isEmpty(inputLayoutCpf.getText())
                    && inputLayoutCpf.getText().toString().length() == 11) {
                final String cpf = inputLayoutCpf.getText().toString();
                inputLayoutCpf.setText(LoginDataHelper.formatCpf(cpf));
                inputLayoutCpf.setTag(cpf);
            }
        }
    }

    @Override protected void onStop() {
        unsubscribeAuthentication();
        super.onStop();
    }
}
