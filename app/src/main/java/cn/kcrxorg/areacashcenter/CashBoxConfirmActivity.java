package cn.kcrxorg.areacashcenter;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.kcrxorg.areacashcenter.adapter.CardUserAdapter;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.cashBoxConfirm.CashBoxConfirm;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.UserQueryMsg;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;

public class CashBoxConfirmActivity extends AppCompatActivity {
    String url="http://172.66.1.2:8080/areaCashCenterTest/userQuery";
    String cashboxconfirmurl="http://172.66.1.2:8080/areaCashCenterTest/cashBoxConfirm";

    public RFIDWithUHFUART mReader;
    //??
    MyLog myLog;
    //2021commit
    List<String> epclist;
    Handler handler;
    GridView gv_carduser;
    UserQueryMsg[] userQueryMsgs;


    ListView lv_boxsconfirmcashboxs;
    List<CashBox> cashBoxList;
    CashBoxAdapter cashBoxAdapter;

    int nowstate=0;//1 2 3 4

    CashBoxConfirm cashBoxConfirm;

    String confirmType="2";

    Button btn_cashboxconfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cash_box_confirm);

        myLog=new MyLog(this,10000,1);
        //confirmType=getIntent().getStringExtra("confirmType");

        cashBoxList=new ArrayList<CashBox>();
        Bundle bundle=getIntent().getBundleExtra("cashboxlist");
        ArrayList list = bundle.getParcelableArrayList("cashboxlist");
        cashBoxList= (List<CashBox>) list.get(0);
        myLog.Write("获取到款箱列表size="+cashBoxList.size());


        cashBoxConfirm=new CashBoxConfirm();
        cashBoxConfirm.setCashBoxDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        cashBoxConfirm.setConfirmType(confirmType);
        cashBoxConfirm.setReceiveConfirm("1");

        userQueryMsgs=new UserQueryMsg[4];

        gv_carduser=findViewById(R.id.gv_carduser);
        CardUserAdapter cardUserAdapter=new CardUserAdapter(this,userQueryMsgs);
        gv_carduser.setAdapter(cardUserAdapter);

        lv_boxsconfirmcashboxs=findViewById(R.id.lv_boxsconfirmcashboxs);

        cashBoxAdapter=new CashBoxAdapter(this,cashBoxList);
        lv_boxsconfirmcashboxs.setAdapter(cashBoxAdapter);

        handler=new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 200:
                        String me=msg.getData().getString("httpRs");
                        myLog.Write("获取到用户刷卡消息:"+me);
                        if(me.contains("error"))
                        {
                            myLog.Write("获取到用户刷卡失败!"+me);
                            toastMessage("获取到用户刷卡失败!"+me);
                            SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        switch (nowstate)
                        {
                            case 0:
                                UserQueryMsg userQueryMsg=JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg.getCode().equals("0"))
                                {
                                    myLog.Write("获取到用户刷卡失败!"+userQueryMsg.getMsg());
                                    toastMessage("获取到用户刷卡失败!"+userQueryMsg.getMsg());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if(userQueryMsg.getRoleID().equals("6"))
                                {
                                    myLog.Write("获取到用户刷卡失败:请操作员刷卡");
                                    toastMessage("获取到用户刷卡失败:请操作员刷卡");
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[0]=userQueryMsg;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxConfirm.setIcCard1(userQueryMsg.getUserID());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate=1;
                                }catch (Exception e)
                                {
                                    myLog.Write("获取到用户刷卡失败!"+e.getMessage());
                                    toastMessage("获取到用户刷卡失败!"+e.getMessage());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            case 1:
                                UserQueryMsg userQueryMsg2=JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg2.getCode().equals("0"))
                                {
                                    myLog.Write("获取到用户刷卡失败!"+userQueryMsg2.getMsg());
                                    toastMessage("获取到用户刷卡失败!"+userQueryMsg2.getMsg());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if(userQueryMsg2.getRoleID().equals("6"))
                                {
                                    myLog.Write("获取到用户刷卡失败:请操作员刷卡");
                                    toastMessage("获取到用户刷卡失败:请操作员刷卡");
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[1]=userQueryMsg2;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxConfirm.setIcCard2(userQueryMsg2.getUserID());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate=2;
                                }catch (Exception e)
                                {
                                    myLog.Write("获取到用户刷卡失败!"+e.getMessage());
                                    toastMessage("获取到用户刷卡失败!"+e.getMessage());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }

                                break;
                            case 2:
                                UserQueryMsg userQueryMsg3=JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg3.getCode().equals("0"))
                                {
                                    myLog.Write("获取到用户刷卡失败!"+userQueryMsg3.getMsg());
                                    toastMessage("获取到用户刷卡失败!"+userQueryMsg3.getMsg());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if(!userQueryMsg3.getRoleID().equals("6"))
                                {
                                    myLog.Write("获取到用户刷卡失败:不是携款员卡:"+userQueryMsg3.getUserName());
                                    toastMessage("获取到用户刷卡失败:不是携款员卡:"+userQueryMsg3.getUserName());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[2]=userQueryMsg3;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxConfirm.setIcCard3(userQueryMsg3.getUserID());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate=3;
                                }catch (Exception e)
                                {
                                    myLog.Write("获取到用户刷卡失败!"+e.getMessage());
                                    toastMessage("获取到用户刷卡失败!"+e.getMessage());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            case 3:
                                UserQueryMsg userQueryMsg4=JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg4.getCode().equals("0"))
                                {
                                    myLog.Write("获取到用户刷卡失败!"+userQueryMsg4.getMsg());
                                    toastMessage("获取到用户刷卡失败!"+userQueryMsg4.getMsg());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if(!userQueryMsg4.getRoleID().equals("6"))
                                {
                                    myLog.Write("获取到用户刷卡失败:不是携款员卡:"+userQueryMsg4.getUserName());
                                    myLog.Write("获取到用户刷卡失败:不是携款员卡:"+userQueryMsg4.getUserName());

                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[3]=userQueryMsg4;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxConfirm.setIcCard4(userQueryMsg4.getUserID());
                                    nowstate=4;
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.SUCCESS);
                                }catch (Exception e)
                                {
                                    myLog.Write("获取到用户刷卡失败!"+e.getMessage());
                                    toastMessage("获取到用户刷卡失败!"+e.getMessage());
                                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;

                            default:
                                break;
                        }
                        break;
                    case 201://提交消息
                        String rs=msg.getData().getString("httpRs");
                        myLog.Write("获取到提交消息:"+rs);
                        if(rs.contains("error"))
                        {
                            myLog.Write("获取到提交失败消息!"+rs);
                            toastMessage("获取到提交失败消息!"+rs);
                            SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        BaseMsg baseMsg=JSONObject.parseObject(rs,BaseMsg.class);
                        if(!baseMsg.getCode().equals("0"))
                        {
                            myLog.Write("获取到提交失败消息:"+baseMsg.getMsg());
                            toastMessage("获取到提交失败消息:"+baseMsg.getMsg());
                            SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        toastMessage("提交成功!"+baseMsg.getMsg());
                        btn_cashboxconfirm.setEnabled(false);
                        SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.SUCCESS);
                        break;
                    default:
                        break;



                }
            }
        };
        btn_cashboxconfirm=findViewById(R.id.btn_cashboxconfirm);
        btn_cashboxconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nowstate!=4)
                {
                    toastMessage("操作员或押运员未登录无法提交");
                    myLog.Write("操作员或押运员未登录无法提交");
                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                if(cashBoxList.size()==0||cashBoxList==null)
                {
                    toastMessage("未扫描钞箱，无法提交！");
                    myLog.Write("未扫描钞箱，无法提交！");
                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                if(!checkAllCashbox())
                {
                    toastMessage("有款箱未确认,无法提交！");
                    myLog.Write("有款箱未确认,无法提交！");
                    SoundManage.PlaySound(CashBoxConfirmActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                cashBoxConfirm.setCashBoxList(cashBoxList);
                String data=JSONObject.toJSONString(cashBoxConfirm);

                HttpTask httpTask=new HttpTask(cashboxconfirmurl,handler,data,201);
                httpTask.execute();
            }
        });

        epclist=new ArrayList<>();

        initUHF();

    }


    @Override
    protected void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }
    public void initUHF()
    {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            myLog.Write(this.getClass()+"启动失败:"+ex.getMessage());
            toastMessage(ex.getMessage());
            return;
        }

        if (mReader != null) {
            new CashBoxConfirmActivity.InitTask().execute();
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
                Toast.makeText(CashBoxConfirmActivity.this, "天线初始化失败",
                        Toast.LENGTH_SHORT).show();
                myLog.Write("天线初始化失败");
            }else
            {
                myLog.Write("天线初始化成功");
            }

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(CashBoxConfirmActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("正在初始化...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //  myLog.Write("按钮被按下keyCode="+keyCode);//280 扳机 139左scan 293 右scan
        if (keyCode == 139 ||keyCode == 280 ||keyCode == 293)
        {
            readTag();
        }

        return super.onKeyDown(keyCode, event);
    }
    private void readTag()
    {
        mReader.setPower(10);
        UHFTAGInfo strUII = mReader.inventorySingleTag();
        if (strUII!=null) {
            String strEPC = strUII.getEPC();
//            addEPCToList(strEPC, strUII.getRssi());
//            tv_count.setText("" + adapter.getCount());
            String cardnum= EpcReader.getEpc(strEPC);
            myLog.Write("扫描到标签号:"+cardnum);
            if(cardnum==null||cardnum.equals(""))
            {
                myLog.Write("扫描到款箱号:"+strEPC+"非法！");
                toastMessage("扫描到款箱号:"+strEPC+"非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(!cardnum.startsWith("W")&&!cardnum.startsWith("K")&&!cardnum.startsWith("HM"))
            {
                myLog.Write("扫描到款箱号:"+strEPC+"非法！");
                toastMessage("扫描到款箱号:"+strEPC+"非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
           // cardnum= cardnum.substring(0,5);
            if(checkRepeat(cardnum)==false)
            {
                myLog.Write("扫描到款箱号:"+strEPC+"重复过滤");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(nowstate!=4&&!cardnum.startsWith("HM"))
            {
                myLog.Write("请刷卡登录后扫描款箱确认...");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            epclist.add(cardnum);
            if(nowstate==4)//已经登录完成，可以扫描款箱...
            {
               if(!checkCashBoxList(cardnum))
               {
                   myLog.Write(cardnum+"款箱不在交接列表中...");
                   toastMessage(cardnum+"款箱不在交接列表中...");
                   SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                   return;
               }

                cashBoxAdapter.notifyDataSetChanged();
                lv_boxsconfirmcashboxs.setSelection(lv_boxsconfirmcashboxs.getBottom());
                SoundManage.PlaySound(this, SoundManage.SoundType.SUCCESS);
                return;
            }

            String cardurl=url+"?icCard="+cardnum;
            myLog.Write("刷卡登录提交:"+cardurl);
            HttpTask httpTask=new HttpTask(cardurl,handler,200);
            httpTask.execute();

        } else {
            toastMessage("未扫描到款箱!");
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
        }
    }
    private boolean checkCashBoxList(String cardnum)
    {
        for(CashBox c:cashBoxList)
        {
            if(c.getCashBoxCode().equals(cardnum))
            {
                c.setColor("green");
                return true;
            }

        }
        return false;
    }
    private  boolean checkAllCashbox()
    {
        for(CashBox c:cashBoxList)
        {
            if(!c.getColor().equals("green"))
            {
                return false;
            }
        }
        return true;
    }
    private boolean checkRepeat(String strEPC)
    {
        for (int i=0;i<epclist.size();i++)
        {
            if(epclist.get(i).equals(strEPC))
            {
                return false;
            }
        }
        return true;
    }
    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
  // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

}