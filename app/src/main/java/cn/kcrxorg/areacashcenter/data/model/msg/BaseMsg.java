package cn.kcrxorg.areacashcenter.data.model.msg;

public class BaseMsg {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    String code="99";
    String msg;
}
