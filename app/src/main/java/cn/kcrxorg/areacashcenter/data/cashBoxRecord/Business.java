package cn.kcrxorg.areacashcenter.data.cashBoxRecord;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.cashBoxRecord.Cash;
import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class Business {
    public String getBankSn() {
        return bankSn;
    }

    public void setBankSn(String bankSn) {
        this.bankSn = bankSn;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getUpDateTime() {
        return upDateTime;
    }

    public void setUpDateTime(String upDateTime) {
        this.upDateTime = upDateTime;
    }

    public String getDistributeTime() {
        return distributeTime;
    }

    public void setDistributeTime(String distributeTime) {
        this.distributeTime = distributeTime;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public List<Cash> getCashList() {
        return cashList;
    }

    public void setCashList(List<Cash> cashList) {
        this.cashList = cashList;
    }

    public List<Zk> getZkList() {
        return zkList;
    }

    public void setZkList(List<Zk> zkList) {
        this.zkList = zkList;
    }

    String bankSn;
    String bankName;
    String upDateTime;
    String distributeTime;
    String totalMoney;
    String serviceTypeName;
    String lineName;
    List<Cash> cashList;
    List<Zk> zkList;
    List<CashBox> cashBoxList;

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }
}
