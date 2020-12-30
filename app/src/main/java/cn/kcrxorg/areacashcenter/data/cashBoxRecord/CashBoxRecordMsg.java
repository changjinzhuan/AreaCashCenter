package cn.kcrxorg.areacashcenter.data.cashBoxRecord;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;

public class CashBoxRecordMsg extends BaseMsg {

    public List<Business> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<Business> businessList) {
        this.businessList = businessList;
    }

    List<Business> businessList;
}
