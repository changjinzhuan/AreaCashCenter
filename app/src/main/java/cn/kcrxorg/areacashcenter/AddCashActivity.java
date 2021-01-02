package cn.kcrxorg.areacashcenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.kcrxorg.areacashcenter.data.Cash;
import cn.kcrxorg.areacashcenter.data.model.CashType;
import cn.kcrxorg.areacashcenter.data.model.CashVoucher;
import cn.kcrxorg.areacashcenter.data.model.msg.CashTypeMsg;
import cn.kcrxorg.areacashcenter.data.model.msg.CashVoucherMsg;
import cn.kcrxorg.areacashcenter.httputil.HttpTask;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;

public class AddCashActivity extends AppCompatActivity {

    Cash cash;
    Cash cashview;

    Spinner sp_cashtypeid;
    CashTypeMsg cashTypeMsg;
    List<String> cashtypelist;
    ArrayAdapter<String> cashtypeadapater;
    Spinner sp_cashvoucherid;
    CashVoucherMsg cashVoucherMsg;
    List<String> cashvoucherlist;
    ArrayAdapter<String> cashvoucheradapater;

    Spinner sp_physicaltypeid;

    EditText et_cashmoney;

    Button btn_addcashsub;

    MyLog myLog;

    String cashtypeurl="http://172.66.1.2:8080/areaCashCenterTest/cashTypeRecord";
    String cashvoucherurl="http://172.66.1.2:8080/areaCashCenterTest/cashVoucherRecord";
    Handler handler;

    BigDecimal cashmoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addcashactivty);

        myLog=new MyLog(this,10000,1);

        cash=new Cash();
        cashview=new Cash();
        cashmoney=new BigDecimal(0);

        //类型选择
        sp_cashtypeid=findViewById(R.id.sp_cashtypeid);
        cashtypelist=new ArrayList<String>();
        cashtypeadapater=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cashtypelist);
        cashtypeadapater.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
        sp_cashtypeid.setAdapter(cashtypeadapater);
        //券别选择
        sp_cashvoucherid=findViewById(R.id.sp_cashvoucherid);
        cashvoucherlist=new ArrayList<String>();
        cashvoucheradapater=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cashvoucherlist);
        cashvoucheradapater.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
        sp_cashvoucherid.setAdapter(cashvoucheradapater);

        sp_physicaltypeid=findViewById(R.id.sp_physicaltypeid);
        et_cashmoney=findViewById(R.id.et_cashmoney);
        btn_addcashsub=findViewById(R.id.btn_addcashsub);


        btn_addcashsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashmoney=new BigDecimal(et_cashmoney.getText()+"");
                if(cashmoney.compareTo(new BigDecimal(0))!=1)
                {
                    toastMessage("输入的金额有误，无法添加");
                    SoundManage.PlaySound(AddCashActivity.this, SoundManage.SoundType.FAILURE);
                    return;
                }
                cashview.setCashMoney(et_cashmoney.getText()+"");
                cashview.setCashTypeId(cashtypelist.get(sp_cashtypeid.getSelectedItemPosition()));
                cashview.setCashVoucherId(cashvoucherlist.get(sp_cashvoucherid.getSelectedItemPosition()));
                cashview.setPhysicalTypeId("现金");

                cash.setCashVoucherId(cashVoucherMsg.getCashVoucherList().get(sp_cashvoucherid.getSelectedItemPosition()).getCashVoucherId());
                cash.setCashTypeId(cashTypeMsg.getCashTypeList().get(sp_cashtypeid.getSelectedItemPosition()).getCashTypeId());
                cash.setPhysicalTypeId("1");
                cash.setCashMoney(et_cashmoney.getText()+"");

                Intent intent2 = new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("cashview",cashview);
                bundle.putSerializable("cash",cash);
                intent2.putExtra("cash",bundle);
                setResult(RESULT_OK,intent2);
                finish();
            }
        });


        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 201://类型消息
                        String rs=msg.getData().getString("httpRs");
                        myLog.Write("获取到类型消息列表:"+rs);
                        cashTypeMsg= JSONObject.parseObject(rs, CashTypeMsg.class);
                        for(CashType cashType:cashTypeMsg.getCashTypeList())
                        {
                            cashtypelist.add(cashType.getCashTypeName());
                        }
                        cashtypeadapater.notifyDataSetChanged();
                        break;
                    case 202://券别消息
                        String rs1=msg.getData().getString("httpRs");
                        myLog.Write("获取到券别消息列表:"+rs1);
                        cashVoucherMsg= JSONObject.parseObject(rs1, CashVoucherMsg.class);
                        for(CashVoucher cashVoucher:cashVoucherMsg.getCashVoucherList())
                        {
                            cashvoucherlist.add(cashVoucher.getCashVoucherName());
                        }
                        cashvoucheradapater.notifyDataSetChanged();
                        break;
                    default:

                        break;
                }

            }
        };
       getCashType();
       getCashVoucher();
    }

    private void getCashVoucher() {

        myLog.Write("开始获取到券别选择列表...");
        HttpTask httpTask=new HttpTask(cashvoucherurl,handler,202);
        httpTask.execute();

    }

    private void getCashType() {
        myLog.Write("开始获取到业务选择列表...");
        HttpTask httpTask=new HttpTask(cashtypeurl,handler,201);
        httpTask.execute();
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}