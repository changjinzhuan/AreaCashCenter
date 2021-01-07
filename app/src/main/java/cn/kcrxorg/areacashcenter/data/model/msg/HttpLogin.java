package cn.kcrxorg.areacashcenter.data.model.msg;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;


import java.util.concurrent.TimeUnit;

import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpLogin {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
            .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
            .build();
    private MyLog myLog;

    private static String code = "99";
    private static String msg;
    private static String bankSN;
    private static String bankName;

    public static String getCode() {
        return code;
    }

    public static String getMsg() {
        return msg;
    }

    public static String getBankSN() {
        return bankSN;
    }

    public static String getBankName() {
        return bankName;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUpBankSN() {
        return upBankSN;
    }

    public static String getUpBankName() {
        return upBankName;
    }

    public static String getUserID() {
        return userID;
    }

    public static String getIdCard() {
        return idCard;
    }

    public static String getIcCard() {
        return icCard;
    }

    public static String getIdImage() {
        return idImage;
    }

    private static String userName;
    private static String upBankSN;
    private static String upBankName;
    private static String userID;
    private static String idCard;
    private static String icCard;
    private static String idImage;

    public static String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    private static String roleID;


    public static void okHttpDoGet(String url)
    {
        final String getrul=url;

        Request request=new Request.Builder().url(getrul).build();
                try {
                    Response response=client.newCall(request).execute();
                    if(response.isSuccessful()) {
                        String res = response.body().string();
                        Log.e("kcrx", "responsebody=" + res);

                        JSONObject jsonObject = JSONObject.parseObject(res);
                        String code1 = jsonObject.getString("code");
                        Log.e("kcrx", "code=" + code1);
                        if (!code1.equals("0")) {
                            code = "1";
                            msg = jsonObject.getString("msg");
                            return;
                        }
                        UserQueryMsg userRecord = JSONObject.parseObject(res, UserQueryMsg.class);

                        if (userRecord.getCode() == "0" || userRecord.getCode().equals("0"))//获取用户成功，登录
                        {
                            code = userRecord.getCode();
                            msg = userRecord.getMsg();
                            bankSN = userRecord.getBankSN();
                            bankName = userRecord.getBankName();
                            userName = userRecord.getUserName();
                            upBankSN = userRecord.getUpBankSN();
                            upBankName = userRecord.getUpBankName();
                            userID = userRecord.getUserID();
                            idCard = userRecord.getIdCard();
                            icCard = userRecord.getIcCard();
                            idImage = userRecord.getIdImage();
                            roleID = userRecord.getRoleID();
                        } else {
                            code = "1";
                            msg = jsonObject.getString("msg");
                        }
                    } else {
                        code = "1";
                        msg = response.body().string();
                        // Log.e("kcrx","login failed") ;
                    }
                } catch (Exception e) {
                    code = "1";
                    msg = e.getMessage();
                    Log.e("kcrx", "登录失败" + e.getMessage());
                    e.printStackTrace();
                }
            }
}



