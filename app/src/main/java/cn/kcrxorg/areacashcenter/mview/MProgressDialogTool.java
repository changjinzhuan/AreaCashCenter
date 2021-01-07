package cn.kcrxorg.areacashcenter.mview;

import android.app.ProgressDialog;
import android.content.Context;

public class MProgressDialogTool {
    private static ProgressDialog mypDialog;

    public static void init(Context context, String info) {
        mypDialog = new ProgressDialog(context);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage(info);
        mypDialog.setCanceledOnTouchOutside(false);
        mypDialog.show();
    }

    public static void stop() {
        mypDialog.dismiss();
    }
}
