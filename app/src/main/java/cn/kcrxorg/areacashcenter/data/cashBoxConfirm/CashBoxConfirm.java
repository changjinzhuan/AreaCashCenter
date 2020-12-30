package cn.kcrxorg.areacashcenter.data.cashBoxConfirm;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class CashBoxConfirm {
    String cashBoxDate;

    public String getCashBoxDate() {
        return cashBoxDate;
    }

    public void setCashBoxDate(String cashBoxDate) {
        this.cashBoxDate = cashBoxDate;
    }

    public String getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(String confirmType) {
        this.confirmType = confirmType;
    }

    public String getIcCard1() {
        return icCard1;
    }

    public void setIcCard1(String icCard1) {
        this.icCard1 = icCard1;
    }

    public String getIcCard2() {
        return icCard2;
    }

    public void setIcCard2(String icCard2) {
        this.icCard2 = icCard2;
    }

    public String getIcCard3() {
        return icCard3;
    }

    public void setIcCard3(String icCard3) {
        this.icCard3 = icCard3;
    }

    public String getIcCard4() {
        return icCard4;
    }

    public void setIcCard4(String icCard4) {
        this.icCard4 = icCard4;
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

    String confirmType;
    String icCard1;
    String icCard2;
    String icCard3;
    String icCard4;

    String receiveConfirm;
    List<CashBox> cashBoxList;
}
