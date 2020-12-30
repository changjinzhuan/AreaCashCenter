package cn.kcrxorg.areacashcenter.data.model.msg;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashType;

public class CashTypeMsg extends BaseMsg{
    public List<CashType> getCashTypeList() {
        return cashTypeList;
    }

    public void setCashTypeList(List<CashType> cashTypeList) {
        this.cashTypeList = cashTypeList;
    }

    List<CashType> cashTypeList;
}
