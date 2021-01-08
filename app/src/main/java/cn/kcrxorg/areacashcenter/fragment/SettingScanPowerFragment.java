package cn.kcrxorg.areacashcenter.fragment;

import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.picker.XSeekBar;

import butterknife.BindView;
import cn.kcrxorg.areacashcenter.MyApp;
import cn.kcrxorg.areacashcenter.R;

@Page(name = "扫描功率设置", anim = CoreAnim.fade)
public class SettingScanPowerFragment extends XPageFragment {
    @BindView(R.id.xsb)
    XSeekBar xsb;
    @BindView(R.id.tv_xsb)
    TextView tvXsb;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_scanpower;
    }

    @Override
    protected void initViews() {
        xsb.setDefaultValue(MMKV.defaultMMKV().decodeInt("scanpower", MyApp.DEFAULT_SCANPOWER));
    }

    @Override
    protected void initListeners() {
        xsb.setOnSeekBarListener(new XSeekBar.OnSeekBarListener() {
            @Override
            public void onValueChanged(XSeekBar seekBar, int newValue) {
                MMKV.defaultMMKV().encode("scanpower", newValue);
                tvXsb.setText(newValue + "");
            }
        });
    }
}
