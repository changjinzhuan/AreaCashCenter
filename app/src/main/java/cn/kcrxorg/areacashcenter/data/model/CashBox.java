package cn.kcrxorg.areacashcenter.data.model;

import java.io.Serializable;

public class CashBox implements Serializable {
    private String cashBoxCode;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private String color="";

    public String getCashBoxCode() {
        return cashBoxCode;
    }

    public void setCashBoxCode(String cashBoxCode) {
        this.cashBoxCode = cashBoxCode;
    }
}
