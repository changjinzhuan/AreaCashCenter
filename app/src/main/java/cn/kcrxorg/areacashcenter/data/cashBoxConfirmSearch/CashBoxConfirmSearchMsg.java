package cn.kcrxorg.areacashcenter.data.cashBoxConfirmSearch;

import cn.kcrxorg.areacashcenter.data.model.msg.BaseMsg;

public class CashBoxConfirmSearchMsg extends BaseMsg {
     String processConfirm;

    public String getProcessConfirm() {
        return processConfirm;
    }

    public void setProcessConfirm(String processConfirm) {
        this.processConfirm = processConfirm;
    }
}
