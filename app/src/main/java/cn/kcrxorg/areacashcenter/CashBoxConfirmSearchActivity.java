package cn.kcrxorg.areacashcenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.kcrxorg.areacashcenter.data.cashBoxConfirmSearch.CashBoxConfirmSearchMsg;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.ServiceType;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.ServiceTypeMsg;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;

public class CashBoxConfirmSearchActivity extends AppCompatActivity {

    String cashboxconfirmsearchurl="http://192.168.3.33:8080/areaCashCenterTest/cashBoxConfirmSearch";
    public RFIDWithUHFUART mReader;
    MyLog myLog;
    List<String> epclist;
    Handler handler;
    String datestr="";

    TextView tv_cashboxconfirmsearch_datepick;
    TextView tv_confirmsearchcashboxnum;
    TextView tv_processconfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cash_box_confirm_search);
        myLog=new MyLog(this,10000,1);
        handler=new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {

                    case 200://查询消息
                        String rs=msg.getData().getString("httpRs");
                        myLog.Write("获取到查询消息列表:"+rs);
                        if(rs.contains("error"))
                        {
                            myLog.Write("获取到查询消息失败!"+rs);
                            toastMessage("获取到查询消息失败!"+rs);
                            SoundManage.PlaySound(CashBoxConfirmSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        CashBoxConfirmSearchMsg cashBoxConfirmSearchMsg=JSONObject.parseObject(rs,CashBoxConfirmSearchMsg.class);
                        if(!cashBoxConfirmSearchMsg.getCode().equals("0"))
                        {
                            myLog.Write("获取到查询消息失败!"+cashBoxConfirmSearchMsg.getMsg());
                            toastMessage("获取到查询消息失败!"+cashBoxConfirmSearchMsg.getMsg());
                            SoundManage.PlaySound(CashBoxConfirmSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if(cashBoxConfirmSearchMsg.getProcessConfirm().equals(""))
                        {
                            tv_processconfirm.setText("无流转消息");
                            myLog.Write("无流转消息");
                            SoundManage.PlaySound(CashBoxConfirmSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        tv_processconfirm.setText(cashBoxConfirmSearchMsg.getProcessConfirm());
                        SoundManage.PlaySound(CashBoxConfirmSearchActivity.this, SoundManage.SoundType.SUCCESS);
                        break;
                }

            }
        };
        tv_cashboxconfirmsearch_datepick=findViewById(R.id.tv_cashboxconfirmsearch_datepick);
        tv_cashboxconfirmsearch_datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPick((TextView) view);
            }
        });
        initUHF();//启动RFID天线
        epclist=new ArrayList<>();

        tv_confirmsearchcashboxnum=findViewById(R.id.tv_confirmsearchcashboxnum);
        tv_processconfirm=findViewById(R.id.tv_processconfirm);

    }
    //日期选择框
    private void showDialogPick(final TextView timeText)
    {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(CashBoxConfirmSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "年" + (monthOfYear+1) + "月" + dayOfMonth+"日");
                //选择完日期后弹出选择时间对话框
                timeText.setText(time);
                myLog.Write("选择的业务日期:"+time);
                datestr=(year+"-"+ (monthOfYear+1)+"-"+dayOfMonth);
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
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
                Toast.makeText(CashBoxConfirmSearchActivity.this, "天线初始化失败",
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

            mypDialog = new ProgressDialog(CashBoxConfirmSearchActivity.this);
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
            if(checkRepeat(cardnum)==false)
            {
                myLog.Write("扫描到款箱号:"+strEPC+"重复过滤");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(datestr.equals(""))
            {
                myLog.Write("请选择流转查询日期！");
                toastMessage("请选择流转查询日期！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            epclist.add(cardnum);
            tv_confirmsearchcashboxnum.setText("箱号:"+cardnum);
            HttpTask httpTask=new HttpTask(cashboxconfirmsearchurl+"?cashBoxCode="+cardnum+"&&cashBoxDate="+datestr,handler,200);
            httpTask.execute();

        } else {
            toastMessage("未扫描到款箱!");
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
        }
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
}