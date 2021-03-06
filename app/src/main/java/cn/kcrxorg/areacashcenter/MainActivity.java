package cn.kcrxorg.areacashcenter;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.ftpserver.ftplet.FtpException;

import cn.kcrxorg.areacashcenter.data.model.msg.HttpLogin;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.UserLGtool;
import cn.kcrxorg.areacashcenter.mbutil.XToastUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    Button btn_cashboxscanin;
    Button btn_cashboxscanout;
    Button btn_cashboxinvertory;


    MyLog myLog;

    UserLGtool userLGtool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLog = new MyLog(this, 10000, 1);
        userLGtool = new UserLGtool();
        try {
            userLGtool.startFtpServer(this);
            myLog.Write("FTP服务已启动...");
        } catch (FtpException e) {
            XToastUtils.error("FTP服务启动失败..." + e.toString());
            myLog.Write("FTP服务启动失败..." + e.toString());
        }
        line_network = findViewById(R.id.line_network);
        line_CashCenter = findViewById(R.id.line_CashCenter);

        btn_cashboxin = findViewById(R.id.btn_cashboxin);
        btn_cashboxopen = findViewById(R.id.btn_cashboxopen);
        btn_cashboxprocess = findViewById(R.id.btn_cashboxprocess);
        btn_cashboxout = findViewById(R.id.btn_cashboxout);
        btn_cashboxmake = findViewById(R.id.btn_cashboxmake);
        btn_cashboxgeiup = findViewById(R.id.btn_cashboxgeiup);
        btn_cashboxreceive = findViewById(R.id.btn_cashboxreceive);
        btn_cashboxstatesearch = findViewById(R.id.btn_cashboxstatesearch);
        btn_cashboxscanin = findViewById(R.id.btn_cashboxscanin);
        btn_cashboxscanout = findViewById(R.id.btn_cashboxscanout);
        btn_cashboxinvertory = findViewById(R.id.btn_cashboxinvertory);

        btn_cashboxin.setOnClickListener(this);
        btn_cashboxopen.setOnClickListener(this);
        btn_cashboxprocess.setOnClickListener(this);
        btn_cashboxout.setOnClickListener(this);
        btn_cashboxmake.setOnClickListener(this);
        btn_cashboxgeiup.setOnClickListener(this);
        btn_cashboxreceive.setOnClickListener(this);
        btn_cashboxstatesearch.setOnClickListener(this);
        btn_cashboxscanin.setOnClickListener(this);
        btn_cashboxscanout.setOnClickListener(this);
        btn_cashboxinvertory.setOnClickListener(this);
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
        switch (view.getId()) {
            case R.id.btn_cashboxin:
                Intent CashBoxPackSearchintent = new Intent(this, CashBoxPackSearchActivity.class);
                CashBoxPackSearchintent.putExtra("serviceType", 0);//箱号入库
                startActivity(CashBoxPackSearchintent);
                break;

            case R.id.btn_cashboxout:
                Intent CashBoxPackSearchintent1 = new Intent(this, CashBoxPackSearchActivity.class);
                CashBoxPackSearchintent1.putExtra("serviceType", 1);//箱号出库

                startActivity(CashBoxPackSearchintent1);
                break;
            case R.id.btn_cashboxopen:
                Intent cashboxopenintent = new Intent(this, CashBoxRecordActivity.class);
                cashboxopenintent.putExtra("serviceType", 0);//缴款业务
                cashboxopenintent.putExtra("confirmType", 0);//箱包库不需要交接
                startActivity(cashboxopenintent);
                break;
            case R.id.btn_cashboxprocess:
                Intent cashboxoutintent = new Intent(this, CashBoxRecordActivity.class);
                cashboxoutintent.putExtra("serviceType", 1);//取款业务
                cashboxoutintent.putExtra("confirmType", 0);//箱包库不需要交接
                startActivity(cashboxoutintent);
                break;
            case R.id.btn_cashboxmake:
                Intent cashboxmakeintent = new Intent(this, CashSendApplyActivity.class);
                startActivity(cashboxmakeintent);
                break;
            case R.id.btn_cashboxgeiup:
                Intent cashboxgeiupintent = new Intent(this, CashBoxRecordActivity.class);
                cashboxgeiupintent.putExtra("serviceType", 0);//缴款业务
                cashboxgeiupintent.putExtra("confirmType", 1);//网点需要交接
                startActivity(cashboxgeiupintent);
                break;
            case R.id.btn_cashboxreceive:
                Intent cashboxreceiveintent = new Intent(this, CashBoxRecordActivity.class);
                cashboxreceiveintent.putExtra("serviceType", 1);//取款业务
                cashboxreceiveintent.putExtra("confirmType", 1);//网点需要交接
                startActivity(cashboxreceiveintent);
                break;
            case R.id.btn_cashboxstatesearch://箱包查询
                Intent cashboxconfirmsearcintent = new Intent(this, CashBoxConfirmSearchActivity.class);
                startActivity(cashboxconfirmsearcintent);
                break;
            case R.id.btn_cashboxscanin:
                Intent cashboxscaninintent = new Intent(this, CashBoxInventoryActivity.class);
                cashboxscaninintent.putExtra("scantype", 1);//1入库
                startActivity(cashboxscaninintent);
                break;
            case R.id.btn_cashboxscanout:
                Intent cashboxscanoutintent = new Intent(this, CashBoxInventoryActivity.class);
                cashboxscanoutintent.putExtra("scantype", 2);//2出库
                startActivity(cashboxscanoutintent);
                break;
            case R.id.btn_cashboxinvertory:
                Intent cashboxinvertoryintent = new Intent(this, CashBoxInventoryActivity.class);
                cashboxinvertoryintent.putExtra("scantype", 3);//3盘库
                startActivity(cashboxinvertoryintent);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userLGtool.stop();
        myLog.Write("FTP服务已关闭...");

    }
}