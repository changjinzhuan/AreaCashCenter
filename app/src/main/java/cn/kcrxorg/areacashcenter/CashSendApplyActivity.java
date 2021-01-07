package cn.kcrxorg.areacashcenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.tencent.mmkv.MMKV;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.kcrxorg.areacashcenter.adapter.CashAdapater;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.Cash;
import cn.kcrxorg.areacashcenter.data.CashSendApply;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.ServiceType;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.data.model.msg.ServiceTypeMsg;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class CashSendApplyActivity extends AppCompatActivity {

    public RFIDWithUHFUART mReader;
    private MyLog myLog;

    TextView tv_datepick;
    TextView tv_cashmoney;
    Spinner sp_servicetype;
    ServiceTypeMsg serviceTypeMsg;
    String serviceTypeurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "serviceTypeRecord";
    String cashSendApplyurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashSendApply";
    List<String> serviceTypeList;
    ArrayAdapter<String> serviceTypearrayAdapter;
    Button btn_addcash;
    Handler handler;
    CashSendApply cashSendApply;
    List<Cash> cashList;
    List<Cash> cashViewList;
    List<CashBox> cashBoxList;
    CashBoxAdapter cashBoxAdapter;

    GridView lv_cash;
    CashAdapater cashAdapater;

    BigDecimal cashmoneyd;

    List<String> epclist;

    Button btn_scanbox;
    ListView lv_boxs;

    String   datestr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cashsendapply);
        myLog=new MyLog(this,10000,1);
        myLog.Write(this.getLocalClassName()+"启动成功");

        handler=new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100://业务提交响应消息
                        String me = msg.getData().getString("httpRs");
                        myLog.Write("获取到提交响应消息:" + me);
                        if (me.contains("error")) {
                            myLog.Write("业务提交响应失败!" + me);
                            XToastUtils.error("业务提交响应失败!" + me);
                            SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        BaseMsg baseMsg = JSONObject.parseObject(me, BaseMsg.class);
                        if (baseMsg.getCode().equals("0")) {
                            SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.SUCCESS);
                            XToastUtils.success("提交成功!");
                            btn_scanbox.setEnabled(false);
                        } else {
                            XToastUtils.error("提交失败" + baseMsg.getMsg());
                        }
                        break;
                    case 200://业务选择框查询消息
                        String rs = msg.getData().getString("httpRs");
                        myLog.Write("获取到业务选择列表:" + rs);
                        if (rs.contains("error")) {
                            myLog.Write("获取到业务选择列表失败!" + rs);
                            XToastUtils.error("获取到业务选择列表失败!" + rs);
                            SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        try {
                            serviceTypeMsg = JSONObject.parseObject(rs, ServiceTypeMsg.class);
                        } catch (Exception e) {
                            myLog.Write("获取到业务选择列表失败!" + e);
                            XToastUtils.error("获取到业务选择列表失败!" + e);
                            SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        for (ServiceType serviceType : serviceTypeMsg.getServiceTypeList()) {
                            serviceTypeList.add(serviceType.getServiceTypeName());
                        }
                        serviceTypearrayAdapter.notifyDataSetChanged();

                        break;


                }

            }
        };
        initUHF();//启动RFID天线

        cashSendApply=new CashSendApply();
        cashSendApply.setBankSn(HttpLogin.getBankSN());
        cashSendApply.setOperatUserId(HttpLogin.getUserID());
        cashSendApply.setUpDateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        cashList=new ArrayList<Cash>();
        cashViewList=new ArrayList<>();
        cashBoxList=new ArrayList<>();
        cashmoneyd=new BigDecimal(0);
        datestr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        cashSendApply.setDistributeTime(datestr);
        //日期选择
        tv_datepick=findViewById(R.id.tv_datepick);
        tv_datepick.setText(datestr);
        tv_datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将timeText传入用于显示所选择的时间
                showDialogPick((TextView) v);
            }
        });
        sp_servicetype=findViewById(R.id.sp_servicetype);
        serviceTypeList=new ArrayList<String>();
        serviceTypearrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,serviceTypeList);
        serviceTypearrayAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
        sp_servicetype.setAdapter(serviceTypearrayAdapter);
        sp_servicetype.setPrompt( "请选择业务类型:" );
        //获取业务类型列表
        getServiceTypes();

        sp_servicetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String serviceTypeid=serviceTypeMsg.getServiceTypeList().get(i).getServiceTypeId();
                myLog.Write("选择的业务id为:"+serviceTypeid);
                cashSendApply.setServiceTypeId(serviceTypeid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //总金额
        tv_cashmoney=findViewById(R.id.tv_cashmoney);

        btn_addcash=findViewById(R.id.btn_addcash);
        btn_addcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcash=new Intent(CashSendApplyActivity.this, AddCashActivity.class);
                startActivityForResult(addcash,1);
            }
        });

        lv_cash=findViewById(R.id.lv_cash);
        lv_cash.setNumColumns(3);
        cashAdapater=new CashAdapater(this,cashViewList);
        lv_cash.setAdapter(cashAdapater);

        epclist=new ArrayList<String>();

        btn_scanbox=findViewById(R.id.btn_scanbox);//提交按钮

        btn_scanbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cashSendApply.getCashBoxList()==null||cashSendApply.getCashBoxList().size()==0)
                {
                    XToastUtils.info("请扫描款箱后才可提交...");

                    SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                if(cashSendApply.getDistributeTime()==null||cashSendApply.getDistributeTime().equals(""))
                {
                    XToastUtils.info("请选择配送日期后提交...");

                    SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                if(cashSendApply.getCashList()==null||cashSendApply.getCashList().size()==0)
                {
                    XToastUtils.info("请添加明细后提交...");

                    SoundManage.PlaySound(CashSendApplyActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }

                myLog.Write("开始提交业务数据");
                String data=JSONObject.toJSONString(cashSendApply);
                myLog.Write("data="+data);
                HttpTask httpTask=new HttpTask(cashSendApplyurl,handler,data,100);
                httpTask.execute();
            }
        });


        cashBoxAdapter=new CashBoxAdapter(this, cashBoxList);
        lv_boxs=findViewById(R.id.lv_boxs);
        lv_boxs.setAdapter(cashBoxAdapter);

    }
    //获取业务类型列表
    private void getServiceTypes() {
        myLog.Write("开始获取到业务选择列表...");
        HttpTask httpTask=new HttpTask(serviceTypeurl,handler,200);
        httpTask.execute();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CashSendApplyActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "年" + (monthOfYear+1) + "月" + dayOfMonth+"日");
                //选择完日期后弹出选择时间对话框
                timeText.setText(time);
                myLog.Write("配送日期:"+time);
                String monthstr="";
                String daystr="";
                if((monthOfYear+1)<10)
                {
                    monthstr="0"+(monthOfYear+1);
                }else
                {
                    monthstr=(monthOfYear+1)+"";
                }
                if(dayOfMonth<10)
                {
                    daystr="0"+dayOfMonth;
                }else
                {
                    daystr=dayOfMonth+"";
                }
                datestr=year+"-"+monthstr+"-"+daystr;
                cashSendApply.setDistributeTime(datestr);

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
                Toast.makeText(CashSendApplyActivity.this, "天线初始化失败",
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

            mypDialog = new ProgressDialog(CashSendApplyActivity.this);
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
        mReader.setPower(MMKV.defaultMMKV().getInt("scanpower", 10));
        UHFTAGInfo strUII = mReader.inventorySingleTag();
        if (strUII!=null) {
            String strEPC = strUII.getEPC();
//            addEPCToList(strEPC, strUII.getRssi());
//            tv_count.setText("" + adapter.getCount());
            String cardnum=EpcReader.getEpc(strEPC);
            if(cardnum==null||cardnum.equals("")) {
                myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                XToastUtils.error("扫描到款箱号:" + strEPC + "非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if (!cardnum.startsWith("W") && !cardnum.startsWith("K")) {
                myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                XToastUtils.error("扫描到款箱号:" + strEPC + "非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            cardnum = cardnum.substring(0, 5);
            if (checkRepeat(cardnum) == false) {
                myLog.Write("扫描到款箱号:" + strEPC + "重复过滤");
                XToastUtils.error("扫描到款箱号:" + strEPC + "重复扫描");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
//            if(!strEPC.startsWith("0000000000000C"))
//            {
//                myLog.Write("扫描到款箱号:"+strEPC+"非本系统标签");
//                toastMessage("扫描到款箱号:"+strEPC+"非本系统标签");
//                playSound(2);
//                return;
//            }
            epclist.add(cardnum);

            CashBox cashBox=new CashBox();
            cashBox.setCashBoxCode(cardnum);
            cashBoxList.add(cashBox);
            cashSendApply.setCashBoxList(cashBoxList);
            SoundManage.PlaySound(this, SoundManage.SoundType.SUCCESS);
            cashBoxAdapter.notifyDataSetChanged();
            lv_boxs.setSelection(lv_boxs.getBottom());

        } else {
            XToastUtils.error("未扫描到款箱!");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if(resultCode== RESULT_OK)
                {
                    Bundle bundle=data.getBundleExtra("cash");
                    Cash cashview=(Cash)bundle.getSerializable("cashview");
                    Cash cash=(Cash)bundle.getSerializable("cash");
                    cashViewList.add(cashview);
                    myLog.Write("收到新增现金明细:"+cashview.getCashVoucherId()+":"+cashview.getCashMoney());
                    cashmoneyd=cashmoneyd.add(new BigDecimal(cashview.getCashMoney()));
                    myLog.Write("总金额:"+cashmoneyd);
                    tv_cashmoney.setText(formatTosepara(cashmoneyd)+"元");
                    cashAdapater.notifyDataSetChanged();
                    cashList.add(cash);
                    cashSendApply.setCashList(cashList);
                    cashSendApply.setTotalMoney(cashmoneyd+"");
                }
                    break;
            default:
                break;
        }
    }
    public static String formatTosepara(BigDecimal data) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(data.floatValue());
    }
    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}