package cn.kcrxorg.areacashcenter.httputil;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by chang on 2017/9/21.
 */

public class GetData {

    private OkHttpClient client = new OkHttpClient();

    public String getMsg(String path) throws Exception {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = StreamTool.read(inStream);
        inStream.close();
        String rsstr=new String(bt,"UTF-8");
        return rsstr;
    }
    public void okHttpDoGet(String url)
    {
        final String getrul=url;

        new Thread(new Runnable() {
            @Override
            public void run() {
           Request request=new Request.Builder().url(getrul).build();
            try {
               Response response=client.newCall(request).execute();
               if(response.isSuccessful())
               {
                  Log.e("kcrx",response.body().string()) ;
               }else
               {
                   Log.e("kcrx","login failed") ;
               }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            }
        }).start();
    }
    public String getPostMsg(String path,String data) throws Exception
    {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        //设置头部信息
        conn.setRequestProperty("headerdata", "ceshiyongde");
        //一定要设置 Content-Type 要不然服务端接收不到参数
        conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
        //指示应用程序要将数据写入URL连接,其值默认为false（是否传参）
        conn.setDoOutput(true);
        //httpURLConnection.setDoInput(true); 
        conn.setUseCaches(false);
        conn.setConnectTimeout(30000); //30秒连接超时
        conn.setReadTimeout(30000);    //30秒读取超时
        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data.getBytes());
        outwritestream.flush();
        outwritestream.close();
     //   Log.d(ScanActivity.logTag, "doJsonPost: conn"+conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = StreamTool.read(inStream);
        inStream.close();
        String rsstr=new String(bt,"UTF-8");
        return rsstr;
    }

}
