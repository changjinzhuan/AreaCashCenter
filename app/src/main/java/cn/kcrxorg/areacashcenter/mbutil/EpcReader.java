package cn.kcrxorg.areacashcenter.mbutil;

import android.util.Log;

import java.nio.charset.Charset;

import cn.kcrxorg.areacashcenter.data.TagEpcData;

public class EpcReader {

    //3B9CD92A114E00AF09BA934D
    //484D303030310000
    public static String getEpc(String ecphex) {
        String s;
        // "484D31353333"
        int lengh = "4B31393838000000".length();
        if(ecphex.length()>lengh) {
            return null;
        }
        try {
            String hex=trimend(ecphex,"00");
            Log.e("kcrx","hex="+hex);
            byte[] b=MyHexTool.HexString2Bytes(hex);
            s= new String(b);
        }catch (Exception e) {
            Log.e("kcrx","epc出错:"+e);
            return null;
        }
        return s;

    }


    /*
     * 删除末尾字符串
     */
    public static String trimend(String inStr, String suffix) {
        while (inStr.endsWith(suffix)) {
            inStr = inStr.substring(0, inStr.length() - suffix.length());
        }
        return inStr;
    }

    public static TagEpcData readEpc(String epcstr) {
        try {
            TagEpcData ted = new TagEpcData();
            //包号
            String tedidstr = epcstr.substring(0, 8);
            Long tedid = Long.parseLong(tedidstr, 16);
            ted.setTagid(tedid);
            //版本号
            String versionidstr = epcstr.substring(8, 9);
            int versionid = Integer.parseInt(versionidstr);
            ted.setVersionid(versionid);
            //券别
            String pervalueidstr = epcstr.substring(9, 10);
            int pervalueid = Integer.parseInt(pervalueidstr);
            ted.setPervalueid(pervalueid);
            //张数
            String amountstr = epcstr.substring(10, 14);
            int amount = Integer.parseInt(amountstr, 16);
            ted.setAmount(amount);
            //随机数
            String randomstr = epcstr.substring(14, 16);
            ted.setRandom(randomstr);
            //操作数
            String operatecountstr = epcstr.substring(16, 19);
            int operatecount = Integer.parseInt(operatecountstr, 16);
            ted.setOperatecount(operatecount);
            //校验码
            String checkcodestr = epcstr.substring(19, 22);
            ted.setCheckcode(checkcodestr);
            //状态字
            String statusstr = epcstr.substring(22);
            System.out.println("statusstr=" + statusstr);
            int status = Integer.parseInt(statusstr, 16);
            //  System.out.println(Integer.toBinaryString(status));
            statusstr = String.format("%08d", Integer.parseInt(Integer.toBinaryString(status)));
            ;
            System.out.println(statusstr);
            //状态字判断
            String jobstutsstr = statusstr.substring(0, 1);
            if (jobstutsstr.equals("0")) {
                ted.setJobstuts(false);
            } else {
                ted.setJobstuts(true);
            }
            String hasElecstr = statusstr.substring(1, 2);
            if (hasElecstr.equals("0")) {
                ted.setHasElec(false);
            } else {
                ted.setHasElec(true);
            }
            String epcExstr = statusstr.substring(4, 5);
            if (epcExstr.equals("0")) {
                ted.setEpcEx(false);
            } else {
                ted.setEpcEx(true);
            }
            String lockeExstr = statusstr.substring(5, 6);
            if (lockeExstr.equals("0")) {
                ted.setLockeEx(false);
            } else {
                ted.setLockeEx(true);
            }
            String lockstutsstr = statusstr.substring(6, 8);
            if (lockstutsstr.equals("00")) {
                ted.setLockstuts("unLock");
            } else if (lockstutsstr.equals("01")) {
                ted.setLockstuts("Lock");
            } else if (lockstutsstr.equals("11")) {
                ted.setLockstuts("unKnown");
            } else {
                ted.setLockstuts("unlawful");
            }
            return ted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
