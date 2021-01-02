package cn.kcrxorg.areacashcenter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.ArrayList;
import java.util.List;

import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.mbutil.EpcReader;
import cn.kcrxorg.areacashcenter.mbutil.MyLog;
import cn.kcrxorg.areacashcenter.mbutil.SoundManage;

public class AddBoxActivity extends AppCompatActivity {
    public RFIDWithUHFUART mReader;
    MyLog myLog;
    ListView lv_getmoneyboxs;

    List<CashBox> cashBoxList;
    CashBoxAdapter cashBoxAdapter;

    int businesscount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_box);
        myLog=new MyLog(this,10000,1);
        initUHF();
        lv_getmoneyboxs=findViewById(R.id.lv_getmoneyboxs);
        cashBoxList=new ArrayList<CashBox>();
        epclist=new ArrayList<String>();
        cashBoxAdapter=new CashBoxAdapter(AddBoxActivity.this,cashBoxList);
        lv_getmoneyboxs.setAdapter(cashBoxAdapter);

        businesscount=getIntent().getIntExtra("businesscount",0);
        myLog.Write("获取到businesscount="+businesscount);
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
            new AddBoxActivity.InitTask().execute();
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
                Toast.makeText(AddBoxActivity.this, "天线初始化失败",
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

            mypDialog = new ProgressDialog(AddBoxActivity.this);
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
        mReader.setPower(10);
        UHFTAGInfo strUII = mReader.inventorySingleTag();
        if (strUII!=null) {
            String strEPC = strUII.getEPC();

            String cardnum= EpcReader.getEpc(strEPC);
            if(cardnum==null||cardnum.equals(""))
            {
                myLog.Write("扫描到款箱号:"+strEPC+"非法！");
                toastMessage("扫描到款箱号:"+strEPC+"非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            if(!cardnum.startsWith("W")&&!cardnum.startsWith("K"))
            {
                myLog.Write("扫描到款箱号:"+strEPC+"非法！");
                toastMessage("扫描到款箱号:"+strEPC+"非法！");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            cardnum= cardnum.substring(0,5);
            if(checkRepeat(cardnum)==false)
            {
                myLog.Write("扫描到款箱号:"+strEPC+"重复过滤");
                SoundManage.PlaySound(this, SoundManage.SoundType.FAILURE);
                return;
            }
            epclist.add(cardnum);
            CashBox cashBox=new CashBox();
            cashBox.setCashBoxCode(cardnum);
            cashBoxList.add(cashBox);
            cashBoxAdapter.notifyDataSetChanged();
           // getCashVoucherMsg(cardnum);
            SoundManage.PlaySound(this, SoundManage.SoundType.SUCCESS);
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

    @Override
    public void onBackPressed() {
        //关闭天线
        if (mReader != null) {
            mReader.free();
        }
        Intent intent2 = new Intent();
        intent2.putExtra("businesscount",businesscount);
        Bundle bundle=new Bundle();
        ArrayList list = new ArrayList();
        list.add(cashBoxList);
        bundle.putParcelableArrayList("cashboxlist",list);
        intent2.putExtra("cashboxlist",bundle);
        setResult(RESULT_OK,intent2);

        finish();

        super.onBackPressed();

    }
}