package cn.kcrxorg.areacashcenter.mbutil;

import android.util.Log;

import java.nio.charset.Charset;

public class EpcReader {

    //484D303030310000
    public static String getEpc(String ecphex)
    {
        String s;
                 // "484D31353333"
        int lengh="4B31393838000000".length();
        if(ecphex.length()>lengh)
        {
            return null;
        }
        try
        {
            String hex=trimend(ecphex,"00");
            Log.e("kcrx","hex="+hex);
            byte[] b=MyHexTool.HexString2Bytes(hex);
            s= new String(b);
        }catch (Exception e)
        {
            Log.e("kcrx","epc出错:"+e);
            return null;
        }
        return s;

    }


    /*
     * 删除末尾字符串
     */
    public static String trimend(String inStr, String suffix) {
        while(inStr.endsWith(suffix)){
            inStr = inStr.substring(0,inStr.length()-suffix.length());
        }
        return inStr;
    }

}
