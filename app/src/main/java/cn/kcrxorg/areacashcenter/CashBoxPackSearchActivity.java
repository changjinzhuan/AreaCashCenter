package cn.kcrxorg.areacashcenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.tencent.mmkv.MMKV;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.kcrxorg.areacashcenter.adapter.CardUserAdapter;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.cashBoxPackSearch.CashBoxPackConfirm;
import cn.kcrxorg.areacashcenter.data.cashBoxPackSearch.CashBoxPackSearchMsg;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.data.model.msg.UserQueryMsg;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class CashBoxPackSearchActivity extends AppCompatActivity {

    String url = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "userQuery";
    String cashBoxPackSearchurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxPackSearch";
    String cashBoxPackConfirmurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxPackConfirm";
    public RFIDWithUHFUART mReader;
    MyLog myLog;
    List<String> epclist;
    Handler handler;
    GridView gv_carduser;

    ListView lv_cashlist;
    List<CashBox> cashBoxList;
    CashBoxAdapter cashBoxAdapter;

    CashBoxPackSearchMsg cashBoxPackSearchMsg;
    CashBoxPackConfirm cashBoxPackConfirm;

    TextView tv_cashboxrecord_datepick;
    TextView tv_linename;
    TextView tv_servicetypename;
    String datestr = "";
    int nowstate = 0;//1 2 3 4
    UserQueryMsg[] userQueryMsgs;

    Button btn_cashBoxPackConfirm;
    int serviceType = 0;

    RadioButton rdbtn_jiaokuan;
    RadioButton rdbtn_getmoney;
    int getserviceType = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_box_pack_search);
        myLog = new MyLog(this, 10000, 1);

        serviceType = getIntent().getIntExtra("serviceType", 0);
        myLog.Write("获取到业务类型为:" + serviceType);
        cashBoxPackConfirm = new CashBoxPackConfirm();
        cashBoxPackConfirm.setReceiveConfirm("1");

        datestr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        cashBoxPackConfirm.setDistributeTime(datestr);
        tv_cashboxrecord_datepick = findViewById(R.id.tv_cashboxpacksearch_datepick);
        tv_cashboxrecord_datepick.setText(datestr);
        tv_cashboxrecord_datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将timeText传入用于显示所选择的时间
                showDialogPick((TextView) view);
            }
        });

        userQueryMsgs = new UserQueryMsg[4];
        gv_carduser = findViewById(R.id.gv_carduser);
        CardUserAdapter cardUserAdapter=new CardUserAdapter(this,userQueryMsgs);
        gv_carduser.setAdapter(cardUserAdapter);

        lv_cashlist=findViewById(R.id.lv_cashlist);
        cashBoxList=new ArrayList<CashBox>();
        cashBoxAdapter=new CashBoxAdapter(this,cashBoxList);
        lv_cashlist.setAdapter(cashBoxAdapter);

        tv_linename=findViewById(R.id.tv_linename);
        tv_servicetypename=findViewById(R.id.tv_servicetypename);
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 200://刷卡消息
                        String me=msg.getData().getString("httpRs");
                        myLog.Write("获取到用户刷卡消息");
                        if(me.contains("error")) {
                            myLog.Write("获取到用户刷卡失败!" + me);
                            XToastUtils.error("获取到用户刷卡失败!" + me);
                            SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        switch (nowstate)
                        {
                            case 0:
                                UserQueryMsg userQueryMsg= JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg.getCode().equals("0")) {
                                    myLog.Write("获取到用户刷卡失败!" + userQueryMsg.getMsg());
                                    XToastUtils.error("获取到用户刷卡失败!" + userQueryMsg.getMsg());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[0]=userQueryMsg;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxPackConfirm.setIcCard1(userQueryMsg.getUserID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate = 1;
                                    getCashBoxPackSearchMsg(userQueryMsg.getIcCard(), userQueryMsg.getRoleID());
                                }catch (Exception e) {
                                    myLog.Write("获取到用户刷卡失败!" + e.getMessage());
                                    XToastUtils.error("获取到用户刷卡失败!" + e.getMessage());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            case 1:
                                UserQueryMsg userQueryMsg2=JSONObject.parseObject(me,UserQueryMsg.class);
                                if(!userQueryMsg2.getCode().equals("0")) {
                                    myLog.Write("获取到用户刷卡失败!" + userQueryMsg2.getMsg());
                                    XToastUtils.error("获取到用户刷卡失败!" + userQueryMsg2.getMsg());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    if(datestr.equals(""))
                                    {
                                        myLog.Write("请选择配送日期!");
                                        XToastUtils.info("请选择配送日期!");
                                        SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                        break;
                                    }
                                    userQueryMsgs[1]=userQueryMsg2;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxPackConfirm.setIcCard2(userQueryMsg2.getUserID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate = 2;
                                    getCashBoxPackSearchMsg(userQueryMsg2.getIcCard(), userQueryMsg2.getRoleID());
                                } catch (Exception e) {
                                    myLog.Write("获取到用户刷卡失败!" + e.getMessage());
                                    XToastUtils.error("获取到用户刷卡失败!" + e.getMessage());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            case 2:
                                UserQueryMsg userQueryMsg3 = JSONObject.parseObject(me, UserQueryMsg.class);
                                if (!userQueryMsg3.getCode().equals("0")) {
                                    myLog.Write("获取到用户刷卡失败!" + userQueryMsg3.getMsg());
                                    XToastUtils.error("获取到用户刷卡失败!" + userQueryMsg3.getMsg());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if (!check2People(userQueryMsg3.getRoleID()))//检查该用户类型人数
                                {
                                    myLog.Write("该用户类型人数已满，不可再扫描!类型为:" + userQueryMsg3.getRoleID());
                                    XToastUtils.error("该用户类型人数已满，不可再扫描!类型为:" + userQueryMsg3.getRoleID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try {
                                    userQueryMsgs[2] = userQueryMsg3;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxPackConfirm.setIcCard3(userQueryMsg3.getUserID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.SUCCESS);
                                    nowstate = 3;
                                    getCashBoxPackSearchMsg(userQueryMsg3.getIcCard(), userQueryMsg3.getRoleID());
                                } catch (Exception e) {
                                    myLog.Write("获取到用户刷卡失败!" + e.getMessage());
                                    XToastUtils.error("获取到用户刷卡失败!" + e.getMessage());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            case 3:
                                UserQueryMsg userQueryMsg4 = JSONObject.parseObject(me, UserQueryMsg.class);
                                if (!userQueryMsg4.getCode().equals("0")) {
                                    myLog.Write("获取到用户刷卡失败!" + userQueryMsg4.getMsg());
                                    XToastUtils.error("获取到用户刷卡失败!" + userQueryMsg4.getMsg());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                if (!check2People(userQueryMsg4.getRoleID()))//检查该用户类型人数
                                {
                                    myLog.Write("该用户类型人数已满，不可再扫描!类型为:" + userQueryMsg4.getRoleID());
                                    XToastUtils.error("该用户类型人数已满，不可再扫描!类型为:" + userQueryMsg4.getRoleID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                try
                                {
                                    userQueryMsgs[3]=userQueryMsg4;
                                    cardUserAdapter.notifyDataSetChanged();
                                    cashBoxPackConfirm.setIcCard4(userQueryMsg4.getUserID());
                                    nowstate = 4;
                                    getCashBoxPackSearchMsg(userQueryMsg4.getIcCard(), userQueryMsg4.getRoleID());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.SUCCESS);
                                }catch (Exception e) {
                                    myLog.Write("获取到用户刷卡失败!" + e.getMessage());
                                    XToastUtils.error("获取到用户刷卡失败!" + e.getMessage());
                                    SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case 201:
                        String rs=msg.getData().getString("httpRs");
                        myLog.Write("获取到线路款包消息:");
                        if(rs.contains("error")) {
                            myLog.Write("获取到线路款包消息失败!" + rs);
                            XToastUtils.error("获取到线路款包消息失败!" + rs);
                            SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        cashBoxPackSearchMsg=JSONObject.parseObject(rs,CashBoxPackSearchMsg.class);
                        if(!cashBoxPackSearchMsg.getCode().equals("0")) {
                            myLog.Write("获取到线路款包消息失败!" + cashBoxPackSearchMsg.getMsg());
                            XToastUtils.error("获取到线路款包消息失败!" + cashBoxPackSearchMsg.getMsg());
                            SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        cashBoxPackConfirm.setServiceTypeId(cashBoxPackSearchMsg.getServiceTypeId());
                        cashBoxPackConfirm.setLineSn(cashBoxPackSearchMsg.getLineSn());
                        tv_linename.setText(cashBoxPackSearchMsg.getLineName());
                        tv_servicetypename.setText(cashBoxPackSearchMsg.getServiceTypeName());
                        for(CashBox cashBox:cashBoxPackSearchMsg.getCashBoxList())
                        {
                            if(cashBoxPackSearchMsg.getServiceTypeName().contains("缴款")&&serviceType==0)
                            {
                                cashBoxList.add(cashBox);
                            }else if(cashBoxPackSearchMsg.getServiceTypeName().contains("取款")&&serviceType==1)
                            {
                                cashBoxList.add(cashBox);
                            }
                        }
                        if(cashBoxList.size()==0) {
                            myLog.Write("无该款包交取款明细" + rs);
                            XToastUtils.info("无该款包交取款明细" + rs);
                            SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        cashBoxPackConfirm.setCashBoxList(cashBoxList);
                        cashBoxAdapter.notifyDataSetChanged();
                        break;
                    case 202:
                            String rs1=msg.getData().getString("httpRs");
                            myLog.Write("获取到提交消息:"+rs1);
                            if(rs1.contains("error")) {
                                myLog.Write("获取到提交失败消息!" + rs1);
                                XToastUtils.error("获取到提交失败消息!" + rs1);
                                SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                break;
                            }
                            BaseMsg baseMsg=JSONObject.parseObject(rs1,BaseMsg.class);
                            if(!baseMsg.getCode().equals("0")) {
                                myLog.Write("获取到提交失败消息:" + baseMsg.getMsg());
                                XToastUtils.error("获取到提交失败消息:" + baseMsg.getMsg());
                                SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                                break;
                            }
                        XToastUtils.success("提交成功!" + baseMsg.getMsg());
                         //   btn_cashboxconfirm.setEnabled(false);
                            btn_cashBoxPackConfirm.setEnabled(false);
                            SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.SUCCESS);
                            break;
                    default:
                        break;
                }
            }
        };
        btn_cashBoxPackConfirm=findViewById(R.id.btn_cashBoxPackConfirm);
        btn_cashBoxPackConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!checkAllCashbox())
               {
                   myLog.Write("有款箱未扫描确认，无法提交！");
                   XToastUtils.info("有款箱未扫描确认，无法提交！");
                   SoundManage.PlaySound(CashBoxPackSearchActivity.this, SoundManage.SoundType.FAILURE);
                   return;
               }
                String data = JSONObject.toJSONString(cashBoxPackConfirm);
                myLog.Write("data=" + data);
                HttpTask httpTask = new HttpTask(cashBoxPackConfirmurl, handler, data, 202);
                httpTask.execute();
            }
        });
        rdbtn_jiaokuan = findViewById(R.id.rdbtn_jiaokuan);
        rdbtn_jiaokuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getserviceType = 22;
                myLog.Write("选择了交款");
            }
        });
        rdbtn_getmoney = findViewById(R.id.rdbtn_getmoney);
        rdbtn_getmoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getserviceType = 23;
                myLog.Write("选择了申领");
            }
        });
        epclist = new ArrayList<>();
        initUHF();
    }

    private void getCashBoxPackSearchMsg(String icCard, String rowID) {
        if (!rowID.equals("6")) {
            myLog.Write("这不是押运员，不需要获取款包明细...");
            return;
        }
        //这里需要获取信息列表
        if (cashBoxPackSearchMsg == null) {
            String cashBoxPackSearch = cashBoxPackSearchurl + "?icCard=" + icCard + "&&distributeTime=" + datestr + "&&serviceTypeId=" + getserviceType;
            myLog.Write("cashBoxPackSearch=" + cashBoxPackSearch);
            HttpTask httpTask = new HttpTask(cashBoxPackSearch, handler, 201);
            httpTask.execute();
        }
    }

    private boolean checkAllCashbox() {
        for (CashBox c : cashBoxList) {
            if (!c.getColor().equals("green")) {
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CashBoxPackSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                cashBoxPackConfirm.setDistributeTime(datestr);
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
                Toast.makeText(CashBoxPackSearchActivity.this, "天线初始化失败",
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

            mypDialog = new ProgressDialog(CashBoxPackSearchActivity.this);
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
            String cardnum= EpcReader.getEpc(strEPC);
            myLog.Write("扫描到标签号:"+cardnum);
            if(cardnum==null||cardnum.equals("")) {
                myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                XToastUtils.error("扫描到款箱号:" + strEPC + "非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(!cardnum.startsWith("W")&&!cardnum.startsWith("K")&&!cardnum.startsWith("HM")) {
                myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                XToastUtils.error("扫描到款箱号:" + strEPC + "非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            // cardnum= cardnum.substring(0,5);
            if(checkRepeat(cardnum)==false) {
                myLog.Write("扫描到款箱号:" + strEPC + "重复过滤");
                XToastUtils.error("扫描到款箱号:" + cardnum + "重复！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(nowstate!=4&&!cardnum.startsWith("HM"))
            {
                myLog.Write("请操作员刷卡登录后扫描款箱确认...");
                XToastUtils.error("请操作员刷卡登录后扫描款箱确认...");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            epclist.add(cardnum);
            if (nowstate == 4 && (cardnum.startsWith("K") || cardnum.startsWith("W")))//登录完成可以扫描款箱
            {
                if (!checkCashBoxList(cardnum)) {
                    myLog.Write(cardnum + "款箱不在交接列表中...");
                    XToastUtils.error(cardnum + "款箱不在交接列表中...");
                    SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                    return;
                }
                cashBoxAdapter.notifyDataSetChanged();
                lv_cashlist.setSelection(lv_cashlist.getBottom());
                SoundManage.PlaySound(this, SoundManage.SoundType.SUCCESS);
               return;
           }
            String cardurl=url+"?icCard="+cardnum;
            myLog.Write("刷卡登录提交:"+cardurl);
            HttpTask httpTask=new HttpTask(cardurl,handler,200);
            httpTask.execute();

        } else {
            XToastUtils.error("未扫描到款箱!");
            SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
        }
    }
    private boolean checkCashBoxList(String cardnum) {
        for (CashBox c : cashBoxList) {
            if (c.getCashBoxCode().equals(cardnum)) {
                c.setColor("green");
                return true;
            }

        }
        return false;
    }

    private boolean checkRepeat(String strEPC) {
        for (int i = 0; i < epclist.size(); i++) {
            if (epclist.get(i).equals(strEPC)) {
                return false;
            }
        }
        return true;
    }

    //当前扫描卡的人数类型大于2
    private boolean check2People(String roleID) {
        int count = 0;
        for (UserQueryMsg userQueryMsg : userQueryMsgs) {
            if (userQueryMsg == null) {
                continue;
            }
            if (roleID.equals(userQueryMsg.getRoleID())) {
                count++;
            }
        }
        myLog.Write("该类型用户数量为：" + count);
        return !(count >= 2);
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}