package cn.kcrxorg.areacashcenter.data;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class CashSendApply {
    public String getBankSn() {
        return bankSn;
    }

    public void setBankSn(String bankSn) {
        this.bankSn = bankSn;
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

    public void setTotalMoney(String totalMoeny) {
        this.totalMoney = totalMoeny;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getOperatUserId() {
        return operatUserId;
    }

    public void setOperatUserId(String operatUserId) {
        this.operatUserId = operatUserId;
    }

    public List<Cash> getCashList() {
        return cashList;
    }

    public void setCashList(List<Cash> cashList) {
        this.cashList = cashList;
    }

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }

    private String bankSn;
    private String upDateTime;
    private String distributeTime;
    private String totalMoney;
    private String serviceTypeId;
    private String operatUserId;
    private List<Cash> cashList;
    private List<CashBox> cashBoxList;


}
