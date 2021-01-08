package cn.kcrxorg.areacashcenter.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.widget.progress.loading.RotateLoadingView;
import com.xuexiang.xupdate.XUpdate;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kcrxorg.areacashcenter.MainActivity;
import cn.kcrxorg.areacashcenter.MyApp;
import cn.kcrxorg.areacashcenter.NewSettingActivity;
import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.SettingActivity;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.mbutil.AppUtils;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private MyLog myLog;
    String serverurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "";
    String url = serverurl + "userQuery";

    private TextView tv_serverset;
    @BindView(R.id.arc_loading)
    RotateLoadingView mLoadingView;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        myLog = new MyLog(this, 10000, 1);
        myLog.Write("程序已启动...");
        tv_version.setText("V" + AppUtils.getVersionName(this));
        //初始化配置
        //mSharedPreferences = getSharedPreferences("Area",MODE_PRIVATE);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        tv_serverset=findViewById(R.id.tv_serverset);
        //系统设置按钮
        tv_serverset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(LoginActivity.this, NewSettingActivity.class);
                startActivity(settingintent);
            }
        });


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                myLog.Write("收到登录消息："+loginResult.getSuccess());
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);

                if (loginResult.getError() != null) {
                    showLoginFailed(HttpLogin.getMsg());
                    SoundManage.PlaySound(LoginActivity.this, SoundManage.SoundType.FAILURE);
                    mLoadingView.stop();
                }
                if (loginResult.getSuccess() != null) {
                    mLoadingView.stop();
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    myLog.Write("登录成功...");
                    Intent mainintent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainintent);
                    //Complete and destroy login activity once successful
                   // finish();
                }

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    mLoadingView.start();
                    Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            String admin=usernameEditText.getText().toString();
                            String pwd= passwordEditText.getText().toString();
                            myLog.Write("开始登录："+url+"?loginName="+admin+"&&loginPwd="+pwd);
                            HttpLogin.okHttpDoGet(url+"?loginName="+admin+"&&loginPwd="+pwd);
                        }
                    });
                    t1.start();
                    //在数据库连接完成之前暂停其他活动
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                mLoadingView.start();
                Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        String admin=usernameEditText.getText().toString();
                        String pwd= passwordEditText.getText().toString();
                        myLog.Write("开始登录："+url+"?loginName="+admin+"&&loginPwd="+pwd);
                        HttpLogin.okHttpDoGet(url+"?loginName="+admin+"&&loginPwd="+pwd);
                    }
                });
                t1.start();
                //在数据库连接完成之前暂停其他活动
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        XToastUtils.success(welcome);
    }

    private void showLoginFailed(String errorString) {
        XToastUtils.error(errorString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingView.recycle();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoadingView.stop();
    }
}