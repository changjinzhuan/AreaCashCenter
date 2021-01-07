package cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;

public class CashBoxInventoryRecordMsg extends BaseMsg {
    List<CashBox> cashBoxList;

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }
}
