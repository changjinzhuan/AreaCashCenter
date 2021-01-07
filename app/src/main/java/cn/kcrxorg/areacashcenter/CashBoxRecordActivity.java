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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.tencent.mmkv.MMKV;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.kcrxorg.areacashcenter.adapter.BusinessAdapter;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.Business;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.CashBoxRecordMsg;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.User;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.data.packRelated.PackRelated;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;
import cn.kcrxorg.areacashcenter.mview.MProgressDialogTool;

public class CashBoxRecordActivity extends AppCompatActivity {

    public RFIDWithUHFUART mReader;
    MyLog myLog;
    //cashBoxCode=0000000000000C1000229014&&cashBoxDate=2020-12-25
    String cashboxcodeurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxRecord?";
    //String testurl="cashBoxCode=0000000000000C1000229014&&cashBoxDate=2020-12-25";
    String packrelatedurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "packRelated";
    BusinessAdapter businessAdapter;
    CashBoxRecordMsg cashBoxRecordMsg;

    Handler handler;

    ListView lv_cashboxrecord;

    List<Business> newbusinessList;

    Button btn_packrelated;


    TextView tv_cashboxrecord_datepick;

    String datestr = "";

    int serviceType = 0;
    int confirmType = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cash_box_record);
        myLog = new MyLog(this, 10000, 1);

        serviceType = getIntent().getIntExtra("serviceType", 0);
        confirmType = getIntent().getIntExtra("confirmType", 0);
        myLog.Write("获取到业务类型为:" + serviceType);
        epclist = new ArrayList<String>();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 200:
                        String me = msg.getData().getString("httpRs");
                        myLog.Write("获取到交取款明细消息:" + me);
                        MProgressDialogTool.stop();
                        if (me.contains("error!")) {
                            myLog.Write("获取交取款信息失败:" + me);
                            XToastUtils.error("获取交取款信息失败:" + me);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }

                        try {
                            cashBoxRecordMsg = JSONObject.parseObject(me, CashBoxRecordMsg.class);
                        } catch (Exception e) {
                            myLog.Write("获取交取款信息失败" + e);
                            XToastUtils.error("获取交取款信息失败" + e);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (cashBoxRecordMsg.getBusinessList().size() == 0) {
                            myLog.Write("该款包交取款明细为空" + me);
                            XToastUtils.error("该款包交取款明细为空:" + me);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        newbusinessList.clear();
                        List<Business> thisBislist = cashBoxRecordMsg.getBusinessList();
                        for (Business b : thisBislist) {
                            if (b.getServiceTypeName().contains("缴款") && serviceType == 0) {
                                newbusinessList.add(b);
                            } else if (b.getServiceTypeName().contains("取款") && serviceType == 1) {
                                newbusinessList.add(b);
                            }
                        }
                        if (newbusinessList.size() == 0) {
                            myLog.Write("无该款包交取款明细" + me);
                            XToastUtils.error("无该款包交取款明细" + me);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (confirmType == 0)//箱包库不显示照片
                        {
                            myLog.Write("这里是箱包库，清空人员照片不显示");
                            for (Business b : newbusinessList) {
                                b.setUserList(new ArrayList<User>());
                            }
                        }
                        myLog.Write("获取到交取款明细数量:" + newbusinessList.size());
                        businessAdapter.notifyDataSetChanged();

                        break;
                    case 202:
                        String rs = msg.getData().getString("httpRs");
                        myLog.Write("获取到提交取款明细消息:" + rs);
                        if (rs.contains("error!")) {
                            myLog.Write("提交取款信息失败:" + rs);
                            XToastUtils.error("提交取款信息失败:" + rs);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        BaseMsg baseMsg = null;
                        try {
                            baseMsg = JSONObject.parseObject(rs, BaseMsg.class);

                        } catch (Exception e) {
                            myLog.Write("提交取款信息失败:" + e);
                            XToastUtils.error("提交取款信息失败:" + e);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (baseMsg.getCode() == null || !baseMsg.getCode().equals("0")) {
                            myLog.Write("提交取款信息失败:" + rs);
                            XToastUtils.error("提交取款信息失败:" + rs);
                            SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.SUCCESS);
                        XToastUtils.success("提交取款信息成功！");
                        btn_packrelated.setEnabled(false);
                        break;

                }
            }
        };
        lv_cashboxrecord=findViewById(R.id.lv_cashboxrecord);
        newbusinessList=new ArrayList<Business>();
        businessAdapter=new BusinessAdapter(CashBoxRecordActivity.this,newbusinessList);
        lv_cashboxrecord.setAdapter(businessAdapter);
        lv_cashboxrecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String ServiceTypeName = newbusinessList.get(i).getServiceTypeName();
                myLog.Write("第" + i + "条业务信息选中,业务类型为:" + ServiceTypeName);
                if (!ServiceTypeName.contains("取款")) {
                    myLog.Write(ServiceTypeName + "业务,不需要扫描绑定款箱...");
                    XToastUtils.info(ServiceTypeName + "业务,不需要扫描绑定款箱...");
                    SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                if (confirmType == 1) {
                    myLog.Write("网点收款业务,不需要扫描绑定款箱...");
                    return;
                }
                Intent intent = new Intent(CashBoxRecordActivity.this, AddBoxActivity.class);
                intent.putExtra("businesscount", i);
                myLog.Write("businesscount=" + i);
                startActivityForResult(intent, 1);
            }
        });
        lv_cashboxrecord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (confirmType == 0) {
                    myLog.Write("现在是箱包库确认,不支持该业务");
                    return false;
                }
                if (newbusinessList.get(i).getCashBoxList().size() == 0) {
                    myLog.Write("该业务未绑定款箱，请绑定后交接...");
                    XToastUtils.info("该业务未绑定款箱，请绑定后交接...");
                    SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                    return false;
                }
                Intent cashboxconfirmintent = new Intent(CashBoxRecordActivity.this, CashBoxConfirmActivity.class);
                Bundle bundle = new Bundle();
                ArrayList list = new ArrayList();
                list.add(newbusinessList.get(i).getCashBoxList());
                bundle.putParcelableArrayList("cashboxlist", list);
                cashboxconfirmintent.putExtra("cashboxlist", bundle);
                startActivity(cashboxconfirmintent);
                finish();
                return true;
            }
        });


        btn_packrelated=findViewById(R.id.btn_packrelated);
        btn_packrelated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!checkCashBoxList())//有款包未关联
               {
                  myLog.Write("有业务未关联款箱，无法提交!");
                  SoundManage.PlaySound(CashBoxRecordActivity.this, SoundManage.SoundType.FAILURE);
                  return;
               }
               String operatUserId= HttpLogin.getUserID();
               for(Business business:newbusinessList)
               {
                   PackRelated packRelated=new PackRelated();
                   packRelated.setOperatUserId(HttpLogin.getUserID());
                   packRelated.setReceiveConfirm("1");
                   packRelated.setCashBoxList(business.getCashBoxList());
                   if(business.getServiceTypeName().contains("缴款"))
                   {
                       packRelated.setConfirmType("2");
                       packRelated.setDistributeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                   }else
                   {
                       packRelated.setConfirmType("1");
                       packRelated.setDistributeTime(datestr);
                   }
                   myLog.Write("开始提交交取款明细列表...");
                   String data=JSONObject.toJSONString(packRelated);
                   myLog.Write("url="+packrelatedurl);
                   myLog.Write("data="+data);
                   HttpTask httpTask=new HttpTask(packrelatedurl,handler,data,202);
                   httpTask.execute();

               }

            }
        });
        datestr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        tv_cashboxrecord_datepick=findViewById(R.id.tv_cashboxrecord_datepick);
        tv_cashboxrecord_datepick.setText(datestr);
        tv_cashboxrecord_datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将timeText传入用于显示所选择的时间
                showDialogPick((TextView) view);
            }
        });



    }
    public boolean checkCashBoxList()
    {
        for(Business b:newbusinessList)
        {
            // myLog.Write("b.getCashBoxList().size()="+b.getCashBoxList().size());
            if(b.getCashBoxList().size()==0||b.getCashBoxList()==null)
            {
                return false;
            }
        }
        return true;
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CashBoxRecordActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "年" + (monthOfYear+1) + "月" + dayOfMonth+"日");
                //选择完日期后弹出选择时间对话框
                timeText.setText(time);
                myLog.Write("选择的业务日期:"+time);
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

            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }
    private void getCashVoucherMsg(String cashboxcode,String datestr) {
        myLog.Write("开始获取到交取款明细列表...");
        myLog.Write("url="+cashboxcodeurl+"cashBoxCode="+cashboxcode+"&&cashBoxDate="+datestr);

        HttpTask httpTask=new HttpTask(cashboxcodeurl+"cashBoxCode="+cashboxcode+"&&cashBoxDate="+datestr,handler,200);
        httpTask.execute();
    }

    @Override
    protected void onDestroy() {
//       myLog.Write("我是onDestroy");
//        if (mReader != null) {
//            mReader.free();
//        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
//        myLog.Write("我是onPause");
        if (mReader != null) {
            mReader.free();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUHF();
    }

    public void initUHF()
    {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            myLog.Write(this.getClass() + "启动失败:" + ex.getMessage());
            XToastUtils.error("启动失败:" + ex.getMessage());
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
                Toast.makeText(CashBoxRecordActivity.this, "天线初始化失败",
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

            mypDialog = new ProgressDialog(CashBoxRecordActivity.this);
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
    List<String> epclist;
    private void readTag()
    {
        mReader.setPower(MMKV.defaultMMKV().getInt("scanpower", 10));
        UHFTAGInfo strUII = mReader.inventorySingleTag();
        if (strUII!=null) {
            String strEPC = strUII.getEPC();

            String cardnum = EpcReader.getEpc(strEPC);
            if (cardnum == null || cardnum.equals("")) {
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
                XToastUtils.error("扫描到款箱号:" + strEPC + "重复！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if (datestr.equals("") || datestr == null) {
                myLog.Write("请选择业务日期!");
                XToastUtils.info("请选择业务日期!");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            epclist.add(cardnum);
            MProgressDialogTool.init(CashBoxRecordActivity.this, "数据加载中...");
            getCashVoucherMsg(cardnum, datestr);
            SoundManage.PlaySound(this, SoundManage.SoundType.SUCCESS);

        } else {
            XToastUtils.info("未扫描到款箱!");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        myLog.Write("收到扫描取款款箱界面返回requestCode="+requestCode+" resultCode="+resultCode);
        switch (requestCode)
        {
            case 1://扫款箱界面返回的数据
                if(resultCode==RESULT_OK)
                {
                    try
                    {
                        Bundle bundle=data.getBundleExtra("cashboxlist");
                        ArrayList list = bundle.getParcelableArrayList("cashboxlist");
                        List<CashBox> cashBoxList= (List<CashBox>) list.get(0);
                        myLog.Write("获取到款箱列表size="+cashBoxList.size());
                        int boxcount=data.getIntExtra("businesscount",0);
                        myLog.Write("获取到款箱序号为="+boxcount);
                        newbusinessList.get(boxcount).setCashBoxList(cashBoxList);
                        String datajson= JSON.toJSONString(newbusinessList);
                        myLog.Write("datajson="+datajson);
                        businessAdapter.notifyDataSetChanged();
                    }catch (Exception e)
                    {
                        myLog.Write("扫描取款款箱出错:"+e.getMessage());
                    }
                }
                break;
            default:
                break;
        }

    }

}