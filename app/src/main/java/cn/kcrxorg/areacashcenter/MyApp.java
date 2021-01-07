package cn.kcrxorg.areacashcenter;

import android.app.Application;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

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
//                        Log.e("kcrx","error code="+error.getCode());
//                        if (error.getCode() != UpdateError.ERROR.CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
//                            XToastUtils.toast(error.toString());
//                        }
                        XToastUtils.error(error.toString());
                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OkHttpUpdateHttpServiceImpl())           //这个必须设置！实现网络请求功能。
                .init(this);
    }
}
