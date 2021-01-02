package cn.kcrxorg.areacashcenter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //updatetest
   LinearLayout line_network;
   LinearLayout line_CashCenter;

    Button btn_cashboxin;
    Button btn_cashboxopen;
    Button btn_cashboxprocess;
    Button btn_cashboxout;
    Button btn_cashboxmake;
    Button btn_cashboxgeiup;
    Button btn_cashboxreceive;
    Button btn_cashboxstatesearch;



    MyLog myLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLog=new MyLog(this,10000,1);

        line_network=findViewById(R.id.line_network);
        line_CashCenter=findViewById(R.id.line_CashCenter);

        btn_cashboxin=findViewById(R.id.btn_cashboxin);
        btn_cashboxopen=findViewById(R.id.btn_cashboxopen);
        btn_cashboxprocess=findViewById(R.id.btn_cashboxprocess);
        btn_cashboxout=findViewById(R.id.btn_cashboxout);
        btn_cashboxmake=findViewById(R.id.btn_cashboxmake);
        btn_cashboxgeiup=findViewById(R.id.btn_cashboxgeiup);
        btn_cashboxreceive=findViewById(R.id.btn_cashboxreceive);
        btn_cashboxstatesearch=findViewById(R.id.btn_cashboxstatesearch);

        btn_cashboxin.setOnClickListener(this);
        btn_cashboxopen.setOnClickListener(this);
        btn_cashboxprocess.setOnClickListener(this);
        btn_cashboxout.setOnClickListener(this);
        btn_cashboxmake.setOnClickListener(this);
        btn_cashboxgeiup.setOnClickListener(this);
        btn_cashboxreceive.setOnClickListener(this);
        btn_cashboxstatesearch.setOnClickListener(this);

        //根据用户初始化界面
        initview();
    }

    private void initview() {

        String roleid= HttpLogin.getRoleID();
        if(roleid.equals("5"))
        {
          line_CashCenter.setVisibility(View.VISIBLE);
        }else if(roleid.equals("7"))
        {
            line_network.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_cashboxin:
                 Intent CashBoxPackSearchintent=new Intent(this,CashBoxPackSearchActivity.class);
                 CashBoxPackSearchintent.putExtra("serviceType",0);//箱号入库
                 startActivity(CashBoxPackSearchintent);
                break;

            case R.id.btn_cashboxout:
                Intent CashBoxPackSearchintent1=new Intent(this,CashBoxPackSearchActivity.class);
                CashBoxPackSearchintent1.putExtra("serviceType",1);//箱号出库
                startActivity(CashBoxPackSearchintent1);
                break;
            case R.id.btn_cashboxopen:
                Intent cashboxopenintent=new Intent(this, CashBoxRecordActivity.class);
                cashboxopenintent.putExtra("serviceType",0);//缴款业务
                startActivity(cashboxopenintent);
                break;
            case R.id.btn_cashboxprocess:
                Intent cashboxoutintent=new Intent(this, CashBoxRecordActivity.class);
                cashboxoutintent.putExtra("serviceType",1);//取款业务
                startActivity(cashboxoutintent);
                break;
            case R.id.btn_cashboxmake:
                Intent cashboxmakeintent=new Intent(this,CashSendApplyActivity.class);
                startActivity(cashboxmakeintent);
                break;
            case R.id.btn_cashboxgeiup:
                Intent cashboxgeiupintent=new Intent(this, CashBoxRecordActivity.class);
                cashboxgeiupintent.putExtra("serviceType",0);//缴款业务
                startActivity(cashboxgeiupintent);
                break;
            case R.id.btn_cashboxreceive:
                Intent cashboxreceiveintent=new Intent(this, CashBoxRecordActivity.class);
                cashboxreceiveintent.putExtra("serviceType",1);//取款业务
                startActivity(cashboxreceiveintent);
                break;
            case R.id.btn_cashboxstatesearch://箱包查询
                 Intent cashboxconfirmsearcintent=new Intent(this,CashBoxConfirmSearchActivity.class);
                 startActivity(cashboxconfirmsearcintent);
                break;
            default:
                break;
        }

    }
}