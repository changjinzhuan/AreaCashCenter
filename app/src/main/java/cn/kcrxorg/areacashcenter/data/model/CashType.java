package cn.kcrxorg.areacashcenter.data.model;

public class CashType {

    public String getCashTypeId() {
        return cashTypeId;
    }

    public void setCashTypeId(String cashTypeId) {
        this.cashTypeId = cashTypeId;
    }

    public String getCashTypeName() {
        return cashTypeName;
    }

    public void setCashTypeName(String cashTypeName) {
        this.cashTypeName = cashTypeName;
    }

    private String cashTypeId;
    private String cashTypeName;
}
