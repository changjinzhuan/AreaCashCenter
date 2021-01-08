package cn.kcrxorg.areacashcenter.fragment;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageContainerListFragment;
import com.xuexiang.xpage.enums.CoreAnim;

@Page(name = "系统设置", anim = CoreAnim.none)
public class NewSettingFragment extends XPageContainerListFragment {


    @Override
    protected Class[] getPagesClasses() {
        return new Class[]
                {
                        SettingServerUrlFragment.class,
                        SettingScanPowerFragment.class,
                        SettingUpdateFragment.class
                };
    }


}
