package cn.kcrxorg.areacashcenter.mbutil;

import android.util.Log;

public class MyLogger {

    public static boolean debug = true ;

    public static void e(String TAG, String msg) {
        if(debug){
            Log.e(TAG, msg) ;
        }

    }
    private static int LOG_MAXLENGTH = 2000;
    public static void show(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
