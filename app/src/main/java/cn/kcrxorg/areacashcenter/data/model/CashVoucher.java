package cn.kcrxorg.areacashcenter.data.model;

public class CashVoucher {
    public String getCashVoucherId() {
        return cashVoucherId;
    }

    public void setCashVoucherId(String cashVoucherId) {
        this.cashVoucherId = cashVoucherId;
    }

    public String getCashVoucherName() {
        return cashVoucherName;
    }

    public void setCashVoucherName(String cashVoucherName) {
        this.cashVoucherName = cashVoucherName;
    }

    private String cashVoucherId;
    private String cashVoucherName;

}
