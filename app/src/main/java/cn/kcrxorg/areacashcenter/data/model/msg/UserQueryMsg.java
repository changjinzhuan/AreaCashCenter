package cn.kcrxorg.areacashcenter.data.model.msg;

public class UserQueryMsg extends BaseMsg{

    private String bankSN;
    private String bankName;
    private String userName;
    private String upBankSN;
    private String upBankName;
    private String userID;
    private String idCard;
    private String icCard;
    private String idImage;

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    private String roleID;

    public String getBankSN() {
        return bankSN;
    }

    public void setBankSN(String bankSN) {
        this.bankSN = bankSN;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUpBankSN() {
        return upBankSN;
    }

    public void setUpBankSN(String upBankSN) {
        this.upBankSN = upBankSN;
    }

    public String getUpBankName() {
        return upBankName;
    }

    public void setUpBankName(String upBankName) {
        this.upBankName = upBankName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }
}
