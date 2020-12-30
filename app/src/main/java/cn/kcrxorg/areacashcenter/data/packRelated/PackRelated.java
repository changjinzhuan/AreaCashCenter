package cn.kcrxorg.areacashcenter.data.packRelated;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class PackRelated {
    public String getOperatUserId() {
        return operatUserId;
    }

    public void setOperatUserId(String operatUserId) {
        this.operatUserId = operatUserId;
    }

    public String getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(String confirmType) {
        this.confirmType = confirmType;
    }



    public String getReceiveConfirm() {
        return receiveConfirm;
    }

    public void setReceiveConfirm(String receiveConfirm) {
        this.receiveConfirm = receiveConfirm;
    }

    public List<CashBox> getCashBoxList() {
        return cashBoxList;
    }

    public void setCashBoxList(List<CashBox> cashBoxList) {
        this.cashBoxList = cashBoxList;
    }

    String operatUserId;
    String confirmType;

    public String getDistributeTime() {
        return distributeTime;
    }

    public void setDistributeTime(String distributeTime) {
        this.distributeTime = distributeTime;
    }

    String distributeTime;
    String receiveConfirm;
    List<CashBox> cashBoxList;
}
