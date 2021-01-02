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

import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import cn.kcrxorg.areacashcenter.MainActivity;
import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.httputil.OKHttpUpdateHttpService;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private MyLog myLog;
    String serverurl="http://172.66.1.2:8080/areaCashCenterTest/";
    String url=serverurl+"userQuery";
    String updateurl="https://gitee.com/miao_po/AreaCashCenter/raw/master/updateapi/update_api.json";
    //https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_test.json
    //https://gitee.com/miao_po/AreaCashCenter/raw/master/updateapi/update_api.json
    private TextView tv_serverset;

    //功率配置
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myLog=new MyLog(this,10000,1);
        myLog.Write("程序已启动...");

        initUpdate();
        //初始化配置
        //mSharedPreferences = getSharedPreferences("Area",MODE_PRIVATE);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        tv_serverset=findViewById(R.id.tv_serverset);

        tv_serverset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XUpdate.newBuild(LoginActivity.this)
                        .updateUrl(updateurl)
                        .update();
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
                    showLoginFailed(loginResult.getError());
                    SoundManage.PlaySound(LoginActivity.this, SoundManage.SoundType.FAILURE);
                }
                if (loginResult.getSuccess() != null) {
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
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void initUpdate()
    {
        XUpdate.get()
                .debug(true)
                .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != UpdateError.ERROR.CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                            ToastUtils.toast(error.toString());
                        }
                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
                .init(getApplication());
    }
}