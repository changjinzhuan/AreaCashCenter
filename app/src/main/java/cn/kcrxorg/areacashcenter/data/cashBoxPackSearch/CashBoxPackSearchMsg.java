package cn.kcrxorg.areacashcenter.data.cashBoxPackSearch;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;
import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;

public class CashBoxPackSearchMsg extends BaseMsg {
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

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }

    String lineSn;
    String lineName;
    String serviceTypeId;
    String serviceTypeName;
    List<CashBox> cashBoxList;

}
