package example.model;

public class MerchantUser {
    private String name;
    private String currency;
    private String email;
    private String password;
    private int accountNumber;
    private int branchNumber;
    private int bankID;
    private String upiId;

    public MerchantUser() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String var1) {
        this.name = var1;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String var1) {
        this.currency = var1;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String var1) {
        this.email = var1;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String var1) {
        this.password = var1;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(int var1) {
        this.accountNumber = var1;
    }

    public int getBranchNumber() {
        return this.branchNumber;
    }

    public void setBranchNumber(int var1) {
        this.branchNumber = var1;
    }

    public int getBankID() {
        return this.bankID;
    }

    public void setBankID(int var1) {
        this.bankID = var1;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
