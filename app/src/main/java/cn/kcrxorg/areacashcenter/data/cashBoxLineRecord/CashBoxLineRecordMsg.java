package cn.kcrxorg.areacashcenter.data.cashBoxLineRecord;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;

public class CashBoxLineRecordMsg extends BaseMsg {
    String lineSn;
    String lineName;
    List<CashBox> cashBoxList;

    public String getLineSn() {
        return lineSn;
    }

    public void setLineSn(String lineSn) {
        this.lineSn = lineSn;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }
}
