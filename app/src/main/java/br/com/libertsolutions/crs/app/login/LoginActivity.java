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
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import butterknife.Bind;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import com.afollestad.materialdialogs.MaterialDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Tela de login, nesta usuários do sistema deverão logar para ter acesso
 * aos dados obtidos do servidor.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
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
            final String cpf = mCpfView.getTag().toString();
            final String password = mPasswordView.getText().toString();

            final MaterialDialog progressDialog = new MaterialDialog
                    .Builder(LoginActivity.this)
                    .title("Entrando")
                    .content("Por favor aguarde enquanto validamos suas credenciais...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            final UserService service = RetrofitHelper
                    .createService(UserService.class, this);

            if (service != null) {
                service.authenticateUser(LoginBody.of(cpf, password))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                progressDialog.dismiss();
                                new MaterialDialog.Builder(LoginActivity.this)
                                        .title("Falha ao tentar entrar")
                                        .content(e.getMessage())
                                        .positiveText("OK")
                                        .show();
                            }

                            @Override
                            public void onNext(User user) {
                                progressDialog.dismiss();
                                if (LoginHelper.isValidUser(user)) {
                                    LoginHelper.loginUser(LoginActivity.this, user);
                                    finish();
                                } else {
                                    new MaterialDialog.Builder(LoginActivity.this)
                                            .title("Problemas com credenciais")
                                            .content("Seu CPF ou senha estão incorretos")
                                            .positiveText("OK")
                                            .show();
                                }
                            }
                        });
            }
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
