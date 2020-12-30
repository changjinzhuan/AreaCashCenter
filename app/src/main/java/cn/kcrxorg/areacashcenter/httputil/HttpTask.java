package cn.kcrxorg.areacashcenter.httputil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpTask extends AsyncTask<Void, Void, Boolean> {
    String murl="";
    String msg="error!";
    int mwhat=0;
    Handler mHandler;
    String data="";


    public HttpTask(String url, Handler handler,int what)
    {
        murl=url;
        mHandler=handler;
        mwhat=what;
    }
    public HttpTask(String url,Handler handler,String data,int what)
    {
        murl=url;
        mHandler=handler;
        this.data=data;
        mwhat=what;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            //   Thread.sleep(1000);
            GetData getData=new GetData();
            Log.e("kcrx","HttpTaskurl="+murl);
            Log.e("kcrx","HttpTaskdata="+data);
            if(data=="")
            {
                msg=getData.getMsg(murl);
            }else
            {
                msg= getData.getPostMsg(murl,data);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean)
        {
            Bundle bundle = new Bundle();
            bundle.putString("httpRs", msg);
            Message message = new Message();
            message.what = mwhat;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }else
        {
            Bundle bundle = new Bundle();
            bundle.putString("httpRs", msg);
            Message message = new Message();
            message.what = mwhat;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }
}
