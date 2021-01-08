package cn.kcrxorg.areacashcenter.fragment;

import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xupdate.XUpdate;

import butterknife.BindView;
import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.SettingActivity;
import cn.kcrxorg.areacashcenter.mbutil.AppUtils;

@Page(name = "版本升级", anim = CoreAnim.none)
public class SettingUpdateFragment extends XPageFragment {
    @BindView(R.id.tv_content)
    TextView tv_content;

    private String mUpdateUrl = "http://120.25.169.17/download/AreaCashCenter/areacashupdate_api.json";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_update;
    }

    @Override
    protected void initViews() {
        tv_content.setText("当前版本号:V" + AppUtils.getVersionName(getContext()));

        XUpdate.newBuild(getContext())
                .updateUrl(mUpdateUrl)
                .update();
    }

    @Override
    protected void initListeners() {

    }
}
