package cn.kcrxorg.areacashcenter.data;

import java.io.Serializable;

public class Cash implements Serializable {

    public String getCashTypeId() {
        return cashTypeId;
    }

    public void setCashTypeId(String cashTypeId) {
        this.cashTypeId = cashTypeId;
    }

    public String getCashVoucherId() {
        return cashVoucherId;
    }

    public void setCashVoucherId(String cashVoucherId) {
        this.cashVoucherId = cashVoucherId;
    }

    public String getPhysicalTypeId() {
        return physicalTypeId;
    }

    public void setPhysicalTypeId(String physicalTypeId) {
        this.physicalTypeId = physicalTypeId;
    }

    public String getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(String cashMoney) {
        this.cashMoney = cashMoney;
    }

    private String cashTypeId;
    private String cashVoucherId;
    private String physicalTypeId;
    private String cashMoney;
}
