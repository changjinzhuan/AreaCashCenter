package cn.kcrxorg.areacashcenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHFUART;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class UHFMainActivity extends AppCompatActivity {
    public RFIDWithUHFUART mReader;
    MyLog myLog;
    List<String> epclist;
    Handler handler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLog = new MyLog(this, 10000, 1);
        epclist = new ArrayList<>();
        initSound();
        initUHF(); //��ʼ��
    }

    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    private void initSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    /**
     * 播放提示音
     *
     * @param id 成功1，失败2
     */
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onDestroy() {
        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }

    public void initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            myLog.Write(this.getClass() + "启动失败:" + ex.getMessage());
            XToastUtils.error(ex.getMessage());
            return;
        }

        if (mReader != null) {
            new InitTask().execute();
        }
    }

    /**
     * �豸�ϵ��첽��
     *
     * @author liuruifeng
     */
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (!result) {
                XToastUtils.error("天线初始化失败");
                myLog.Write("天线初始化失败");
            } else {
                myLog.Write("天线初始化成功");
            }

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(UHFMainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("正在初始化...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

}
