package cn.kcrxorg.areacashcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord.CashBoxInventoryAdd;
import cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord.CashBoxInventoryRecordMsg;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
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

    CashBoxInventoryRecordMsg cashBoxInventoryRecordMsg;

    CashBoxAdapter cashBoxAdapter;
    List<CashBox> cashBoxList;
    private boolean loopFlag = false;
    private boolean isinventoryFlag = false;


    String cashBoxInventoryRecordurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxInventoryRecord";
    String cashBoxInventoryAddurl = MMKV.defaultMMKV().getString("serverurl", MyApp.DEFAULT_SERVER_URL) + "cashBoxInventoryAdd";

    int scantype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cash_box_inventory);
        ButterKnife.bind(this);

        scantype = getIntent().getIntExtra("scantype", -1);
        cashBoxList = new ArrayList<CashBox>();
        cashBoxAdapter = new CashBoxAdapter(this, cashBoxList);
        lv_invertory.setAdapter(cashBoxAdapter);

        if (scantype == 3) {
            btn_subinventory.setVisibility(View.GONE);
        }

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 200://刷新数据
                        String rs = msg.getData().getString("httpRs");
                        myLog.Write("获取到出入库业务:" + rs);
                        MProgressDialogTool.stop();
                        if (rs.contains("error")) {
                            myLog.Write("获取到出入库业务失败!" + rs);
                            XToastUtils.error("获取到出入库业务失败!" + rs);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }

                        try {
                            cashBoxInventoryRecordMsg = JSONObject.parseObject(rs, CashBoxInventoryRecordMsg.class);
                        } catch (Exception e) {
                            myLog.Write("获取到出入库业务失败!" + e);
                            XToastUtils.error("获取到出入库业务失败!" + e);
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (!cashBoxInventoryRecordMsg.getCode().equals("0")) {
                            myLog.Write("获取到出入库业务失败!" + cashBoxInventoryRecordMsg.getMsg());
                            XToastUtils.error("获取到出入库业务失败!" + cashBoxInventoryRecordMsg.getMsg());
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        if (cashBoxInventoryRecordMsg.getCashBoxList().size() == 0) {
                            myLog.Write("获取到出入库业务,款包数量为空");
                            XToastUtils.error("获取到出入库业务,款包数量为空");
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                            break;
                        }
                        for (CashBox c : cashBoxInventoryRecordMsg.getCashBoxList()) {
                            cashBoxList.add(c);
                        }
                        cashBoxAdapter.notifyDataSetChanged();

                        break;
                    case 201://刷卡数据
                        String result = msg.obj + "";
                        myLog.Write("盘点读取到卡号" + result);
                        if (cashBoxInventoryRecordMsg.getCashBoxList() == null || cashBoxInventoryRecordMsg.getCashBoxList().size() == 0) {
                            CashBox cashBox = new CashBox();
                            cashBox.setCashBoxCode(result);
                            cashBox.setColor("green");
                            cashBoxList.add(cashBox);
                            myLog.Write("cashBoxList.size=" + cashBoxList.size());
                            SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.SUCCESS);
                            cashBoxAdapter.notifyDataSetChanged();
                            break;
                        } else {
                            for (CashBox c : cashBoxList) {
                                if (c.getCashBoxCode().equals(result)) {
                                    c.setColor("green");
                                    SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.SUCCESS);
                                }
                            }
                        }
                        cashBoxAdapter.notifyDataSetChanged();
                        break;

                    case 202:
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
        initData();
    }

    private void initData() {
        MProgressDialogTool.init(this, "正在查找出入库业务数据...");
        String url = cashBoxInventoryRecordurl + "?bankSn=" + HttpLogin.getBankSN();
        HttpTask httpTask = new HttpTask(url, handler, 200);
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

        //  myLog.Write("按钮被按下keyCode="+keyCode);//280 扳机 139左scan 293 右scan
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

    class TagThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strEPC;
            UHFTAGInfo res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                // myLog.Write("loopping....");
                if (res != null) {
                    strEPC = res.getEPC();
                    String cardnum = EpcReader.getEpc(strEPC);
                    myLog.Write("扫描到标签号:" + cardnum);
                    if (cardnum == null || cardnum.equals("")) {
                        myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                        //    XToastUtils.error("扫描到款箱号:"+strEPC+"非法！");
                        //    SoundManage.PlaySound(CashBoxInventoryActivity.this, SoundManage.SoundType.FAILURE);
                        continue;
                    }
                    if (!cardnum.startsWith("W") && !cardnum.startsWith("K")) {
                        myLog.Write("扫描到款箱号:" + strEPC + "非法！");
                        //     XToastUtils.error("扫描到款箱号:"+strEPC+"非法！");
                        //      SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                        continue;
                    }
                    if (checkRepeat(cardnum) == false) {
                        myLog.Write("扫描到款箱号:" + strEPC + "重复过滤");
                        //     XToastUtils.error("扫描到款箱号:"+strEPC+"重复扫描");
                        //    SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                        continue;
                    }
                    epclist.add(cardnum);
                    Message msg = handler.obtainMessage(201);
                    msg.obj = cardnum;
                    handler.sendMessage(msg);
                }
            }
        }
    }

}