package cn.kcrxorg.areacashcenter;

import android.os.Bundle;
import android.view.Window;

import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.core.PageOption;

import cn.kcrxorg.areacashcenter.fragment.NewSettingFragment;


public class NewSettingActivity extends XPageActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageOption.to(NewSettingFragment.class).open(this);
    }
}
