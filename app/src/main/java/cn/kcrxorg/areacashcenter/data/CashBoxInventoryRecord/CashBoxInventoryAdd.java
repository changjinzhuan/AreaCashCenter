package cn.kcrxorg.areacashcenter.data.CashBoxInventoryRecord;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class CashBoxInventoryAdd {
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBankSn() {
        return bankSn;
    }

    public void setBankSn(String bankSn) {
        this.bankSn = bankSn;
    }

    public String getFlagType() {
        return flagType;
    }

    public void setFlagType(String flagType) {
        this.flagType = flagType;
    }

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }

    String updateTime;
    String bankSn;
    String flagType;
    List<CashBox> cashBoxList;
}
