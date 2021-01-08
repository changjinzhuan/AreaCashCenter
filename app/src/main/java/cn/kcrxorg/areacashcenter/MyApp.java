package cn.kcrxorg.areacashcenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import java.util.List;

import cn.kcrxorg.areacashcenter.httputil.update.OkHttpUpdateHttpServiceImpl;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class MyApp extends Application {
    public static final String DEFAULT_SERVER_URL = "http://172.66.1.2:8080/areaCashCenterTest/";
    public static final int DEFAULT_SCANPOWER = 10;
    MyLog myLog;

    @Override
    public void onCreate() {
        super.onCreate();
        myLog = new MyLog(this.getApplicationContext(), 10000, 1);

        initMMKV();
        initXupdate();
        initXPage();
    }

    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
        myLog.Write("初始化 mmkv root: " + rootDir);
        myLog.Write("获取到服务器地址:" + MMKV.defaultMMKV().getString("serverurl", DEFAULT_SERVER_URL));

    }

    private void initXupdate() {
        myLog.Write("初始化自动更新Xupdate");
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
                        XToastUtils.info(error.toString());
                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OkHttpUpdateHttpServiceImpl())           //这个必须设置！实现网络请求功能。
                .init(this);
    }

    private void initXPage() {
        PageConfig.getInstance()
                .setPageConfiguration(new PageConfiguration() { //页面注册
                    @Override
                    public List<PageInfo> registerPages(Context context) {
                        //自动注册页面,是编译时自动生成的，build一下就出来了。如果你还没使用@Page的话，暂时是不会生成的。
                        return AppPageConfig.getInstance().getPages(); //自动注册页面
                    }
                })
                .debug("PageLog")       //开启调试
                .setContainActivityClazz(XPageActivity.class) //设置默认的容器Activity
                .enableWatcher(false)   //设置是否开启内存泄露监测
                .init(this);
    }
}
