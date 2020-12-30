package cn.kcrxorg.areacashcenter.data.model.msg;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.CashVoucher;

public class CashVoucherMsg extends BaseMsg{
    public List<CashVoucher> getCashVoucherList() {
        return cashVoucherList;
    }

    public void setCashVoucherList(List<CashVoucher> cashVoucherList) {
        this.cashVoucherList = cashVoucherList;
    }

    List<CashVoucher> cashVoucherList;
}
