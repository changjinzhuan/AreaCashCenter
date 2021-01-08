package cn.kcrxorg.areacashcenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import cn.kcrxorg.areacashcenter.data.cashBoxLineRecord.CashBoxLineRecordMsg;
import cn.kcrxorg.areacashcenter.data.model.CashBox;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("cn.kcrxorg.areacashcenter", appContext.getPackageName());
    }

    @Test
    public void printClassJson() {
        CashBoxLineRecordMsg cashBoxLineRecordMsg = new CashBoxLineRecordMsg();
        cashBoxLineRecordMsg.setCode("0");
        cashBoxLineRecordMsg.setMsg("success");
        cashBoxLineRecordMsg.setLineName("细线");
        cashBoxLineRecordMsg.setLineSn("11");

        List<CashBox> cashBoxList = new ArrayList<>();
        CashBox cashBox1 = new CashBox();
        cashBox1.setCashBoxCode("K1996");
        CashBox cashBox2 = new CashBox();
        cashBox2.setCashBoxCode("K1997");
        cashBoxList.add(cashBox1);
        cashBoxList.add(cashBox2);
        cashBoxLineRecordMsg.setCashBoxList(cashBoxList);

        Log.e("test", JSONObject.toJSONString(cashBoxLineRecordMsg));
    }
}