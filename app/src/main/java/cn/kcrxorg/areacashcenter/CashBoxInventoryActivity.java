package cn.kcrxorg.areacashcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord.CashBoxInventoryAdd;
import cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord.CashBoxInventoryRecordMsg;
import cn.kcrxorg.areacashcenter.data.cashBoxLineRecord.CashBoxLineRecordMsg;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.Business;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.CashBoxRecordMsg;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.CashBoxError;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;
import cn.kcrxorg.areacashcenter.mview.MProgressDialogTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashBoxInventoryActivity extends UHFMainActivity {

    @BindView(R.id.lv_invertory)
    ListView lv_invertory;

    @BindView(R.id.tv_invertorystate)
    SuperTextView tv_invertorystate;
    @BindView(R.id.btn_subinventory)
    SuperButton btn_subinventory;
    @BindView(R.id.btn_scan_error)
    SuperButton btn_scan_error;


    CashBoxLineRecordMsg cashBoxLineRecordMsg;

    CashBoxAdapter cashBoxAdapter;
    List<CashBox> cashBoxList;
    private boolean loopFlag = false;
    private boolean isinventoryFlag = false;

    List<CashBoxError> cashBoxErrorList;
    BadgeView badgeView;


    String cashBoxInventoryRecordurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxInventoryRecord";
    String cashBoxInventoryAddurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxInventoryAdd";


    String cashboxLineRecordurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxLineRecord?";
    String geturltest = "http://192.168.2.2:8080/dowload/test.json";
    String cashBoxInventoryRecord = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxInventoryRecord?";
    int scantype;
    String datastr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private boolean isfirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cash_box_inventory);
        ButterKnife.bind(this);

        scantype = getIntent().getIntExtra("scantype", -1);
        cashBoxList = new ArrayList<CashBox>();
        cashBoxErrorList = new ArrayList<>();
        badgeView = new BadgeView(CashBoxInventoryActivity.this);
        badgeView.bindTarget(btn_scan_error);
        cashBoxAdapter = new CashBoxAdapter(this, cashBoxList);
        lv_invertory.setAdapter(cashBoxAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 200://刷新数据
                        String me = msg.getData().getString("httpRs");
                        //     myLog.Write("获取到出入库业务:" + me);
                        MProgressDialogTool.stop();
                        if (me.contains("error!")) {
                            myLog.Write("获取交取款信息失败:" + me);
                            XToastUtils.error("获取交取款信息失败:" + me);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        try {
                            cashBoxLineRecordMsg = JSONObject.parseObject(me, CashBoxLineRecordMsg.class);
                        } catch (Exception e) {
                            myLog.Write("获取交取款信息失败" + e);
                            XToastUtils.error("获取交取款信息失败" + e);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (cashBoxLineRecordMsg.getCashBoxList().size() == 0) {
                            myLog.Write("该款包交取款明细为空" + me);
                            XToastUtils.error("该款包交取款明细为空:" + me);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        for (CashBox c : cashBoxLineRecordMsg.getCashBoxList()) {
                            cashBoxList.add(c);
                        }
                        myLog.Write("获取到线路款包明细" + cashBoxList.size() + "条");
                        XToastUtils.success("获取到线路款包明细" + cashBoxList.size() + "条");
                        SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.SUCCESS);
                        cashBoxAdapter.notifyDataSetChanged();
                        break;
                    case 201://UHF天线数据 第一箱数据
                        String result = msg.obj + "";
                        myLog.Write("盘点读取第一箱数据卡号" + result);
                        isfirst = false;
                        getCashVoucherMsg(result, datastr);//获取款箱列表
                        epclist.clear();
                        isinventoryFlag = false;
                        stopInventory();
                        break;
                    case 203: //UHF后续数据
                        String result2 = msg.obj + "";
                        myLog.Write("盘点读取后续卡号" + result2);
//                        if(cashBoxList==null||cashBoxList.size()==0)
//                        {
//                            CashBox cashBox=new CashBox();
//                            cashBox.setCashBoxCode(result2);
//                            cashBox.setColor("green");
//                            cashBoxList.add(cashBox);
//                            cashBoxAdapter.notifyDataSetChanged();
//                            break;
//                        }
                        for (CashBox c : cashBoxList) {
                            if (c.getCashBoxCode().equals(result2)) {
                                c.setColor("green");
                                SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.SUCCESS);
                            } else {
                                setCashBoxError(c.getCashBoxCode(), "不在任务列表");
                                myLog.Write(c.getCashBoxCode() + "不在任务列表");
                            }
                        }
                        cashBoxAdapter.notifyDataSetChanged();
                        break;
                    case 202://提交结果数据
                        String subrs = msg.getData().getString("httpRs");
                        myLog.Write("获取到出入库业务提交结果" + subrs);
                        if (subrs.contains("error")) {
                            myLog.Write("获取到出入库业务提交失败!" + subrs);
                            XToastUtils.error("获取到出入库业务提交失败!" + subrs);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        BaseMsg baseMsg;
                        try {
                            baseMsg = JSONObject.parseObject(subrs, BaseMsg.class);
                        } catch (Exception e) {
                            myLog.Write("获取到出入库业务提交失败!" + e);
                            XToastUtils.error("获取到出入库业务提交失败!" + e);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (!baseMsg.getCode().equals("0")) {
                            myLog.Write("获取到出入库业务提交失败!" + baseMsg.getMsg());
                            XToastUtils.error("获取到出入库业务提交失败!" + baseMsg.getMsg());
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        myLog.Write("出入库业务提交成功");
                        XToastUtils.success("出入库业务提交成功");
                        SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.SUCCESS);

                        break;

                    default:
                        break;
                }
            }
        };
        //   initData();

        //盘库业务，直接获取任务列表并且不提交
        if (scantype == 3) {
            btn_subinventory.setVisibility(View.GONE);
            //不需要首个扫描
            isfirst = false;
            //获取盘库业务
            getCashBoxInventoryRecord();
        }
    }

    //获取盘库库存明细
    private void getCashBoxInventoryRecord() {
        MProgressDialogTool.init(this, "正在查找出入库业务数据...");
        String url = cashBoxInventoryRecordurl + "?bankSn=" + HttpLogin.getBankSN();
        HttpTask httpTask = new HttpTask(url, handler, 200);
        httpTask.execute();
    }

    private void initData() {
        String url = cashBoxInventoryRecordurl + "?bankSn=" + HttpLogin.getBankSN();
        HttpTask httpTask = new HttpTask(url, handler, 200);
        httpTask.execute();
    }

    private void getCashVoucherMsg(String cashboxcode, String datestr) {
        MProgressDialogTool.init(this, "正在查找出入库业务数据...");
        myLog.Write("正在查找出入库业务数据...");
        myLog.Write("url=" + cashboxLineRecordurl + "cashBoxCode=" + cashboxcode + "&&cashBoxDate=" + datestr);
        // HttpTask httpTask=new HttpTask(cashboxLineRecordurl+"cashBoxCode="+cashboxcode+"&&cashBoxDate="+datestr,handler,200);
        HttpTask httpTask = new HttpTask(cashboxLineRecordurl + "cashBoxCode=" + cashboxcode + "&&cashBoxDate=" + datestr, handler, 200);
        httpTask.execute();
    }

    @OnClick(R.id.btn_subinventory)
    void subinventoryClick() {
        if (cashBoxList == null || cashBoxList.size() == 0) {
            myLog.Write("扫描款包数量为空,无法提交！");
            XToastUtils.error("扫描款包数量为空,无法提交！");
            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
            return;
        }
        for (CashBox cashBox : cashBoxList) {
            if (!cashBox.getColor().equals("green")) {
                myLog.Write("仍有款包未扫描无法提交");
                XToastUtils.error("仍有款包未扫描无法提交");
                SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                return;
            }
        }
        CashBoxInventoryAdd cashBoxInventoryAdd = new CashBoxInventoryAdd();
        cashBoxInventoryAdd.setBankSn(HttpLogin.getBankSN());
        cashBoxInventoryAdd.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        cashBoxInventoryAdd.setFlagType(scantype + "");
        cashBoxInventoryAdd.setCashBoxList(cashBoxList);
        String data = JSONObject.toJSONString(cashBoxInventoryAdd);
        HttpTask httpTask = new HttpTask(cashBoxInventoryAddurl, handler, data, 202);
        httpTask.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            isinventoryFlag = !isinventoryFlag;
            readTag();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void readTag() {
        if (isinventoryFlag) {
            if (mReader.startInventoryTag()) {
                loopFlag = true;
                myLog.Write("开始扫描");
                tv_invertorystate.setCenterString("扫描已开始");
                tv_invertorystate.setBackgroundColor(getResources().getColor(R.color.xui_btn_green_select_color));
                new TagThread().start();
            } else {
                mReader.stopInventory();
            }
        } else {
            stopInventory();
        }
    }

    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            if (mReader.stopInventory()) {
                tv_invertorystate.setCenterString("扫描已停止");
                tv_invertorystate.setBackgroundColor(getResources().getColor(R.color.xui_config_color_red));
                myLog.Write("识别已停止");
            }
        }
    }

    private boolean checkRepeat(String strEPC) {
        for (int i = 0; i < epclist.size(); i++) {
            if (epclist.get(i).equals(strEPC)) {
                return false;
            }
        }
        return true;
    }


    private void setCashBoxError(String cardnum, String error) {
        CashBoxError cashBoxError = new CashBoxError();
        cashBoxError.setCashBoxCode(cardnum);
        cashBoxError.setError(error);
        cashBoxErrorList.add(cashBoxError);
        badgeView.setBadgeNumber(cashBoxErrorList.size());
    }

    class TagThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strEPC;
            UHFTAGInfo res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strEPC = res.getEPC();

                    String cardnum = EpcReader.getEpc(strEPC);
                    myLog.Write("扫描到标签号:" + cardnum);
                    if (cardnum == null || cardnum.equals("")) {
                        myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                        //   setCashBoxError(strEPC,"扫描到款箱号:" + strEPC + "非法！");
                        continue;
                    }
                    //包含@的是二代款包
                    if (!cardnum.startsWith("W") && !cardnum.startsWith("K")) {
                        myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                        //  setCashBoxError(cardnum,"扫描到款箱号:" + strEPC + "非法！");
                        continue;
                    }
                    if (checkRepeat(cardnum) == false) {
                        myLog.Write("扫描到款箱号:" + strEPC + "重复过滤");
                        continue;
                    }
                    epclist.add(cardnum);

                    if (isfirst) {
                        Message msg = handler.obtainMessage(201);
                        msg.obj = cardnum;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage(203);
                        msg.obj = cardnum;
                        handler.sendMessage(msg);
                    }
                }
            }
        }
    }

}