package cn.kcrxorg.areacashcenter;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.widget.picker.XSeekBar;
import com.xuexiang.xupdate.XUpdate;

public class SettingActivity extends AppCompatActivity {

    private String defaultserverurl = MyApp.DEFAULT_SERVER_URL;
    private String mUpdateUrl = "http://120.25.169.17/download/AreaCashCenter/areacashupdate_api.json";

    @BindView(R.id.et_serverurl)
    EditText et_serverurl;
    @BindView(R.id.btn_savesetting)
    Button btn_savesetting;
    @BindView(R.id.btn_update)
    Button btn_update;
    @BindView(R.id.xsb)
    XSeekBar xsb;


    MMKV kv;
    String serverurl = "";
    int scanpower;

    MyLog myLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        myLog = new MyLog(this, 10000, 1);
        ButterKnife.bind(this);
        kv = MMKV.defaultMMKV();
        serverurl = kv.getString("serverurl", defaultserverurl);
        scanpower = kv.getInt("scanpower", MyApp.DEFAULT_SCANPOWER);
        initView();
    }

    public void initView() {
        et_serverurl.setText(serverurl);
        xsb.setDefaultValue(scanpower);
        xsb.setOnSeekBarListener(((seekBar, newValue) -> kv.putInt("scanpower", newValue)));
    }

    @OnClick(R.id.btn_savesetting)
    void setBtn_savesetting() {
        try {
            myLog.Write("修改设置serverurl=" + et_serverurl.getText().toString());
            kv.encode("serverurl", et_serverurl.getText().toString());
            myLog.Write("设置修改为serverurl=" + et_serverurl.getText().toString());
            kv.decodeString("serverurl", MyApp.DEFAULT_SERVER_URL);
            XToastUtils.success("设置成功！");
        } catch (Exception e) {
            XToastUtils.error("设置失败:" + e.getMessage());
        }

    }

    @OnClick(R.id.btn_update)
    void upddate() {
        XUpdate.newBuild(SettingActivity.this)
                .updateUrl(mUpdateUrl)

                .update();
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}