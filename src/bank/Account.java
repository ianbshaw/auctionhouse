package bank;

import java.io.Serializable;

/*class that represents account objects with bank*/
public class Account implements Serializable {
    private String name;
    private int balance;
    private int account_number;
    private boolean deleteFlag = false;
    private boolean balanceFlag = false;

    public Account(String name, int initBalance) {
        balance = initBalance;
        this.name = name;
    }

    public boolean getBalanceFlag() {
        return balanceFlag;
    }

    public void setBalanceFlag(boolean balanceFlag) {
        this.balanceFlag = balanceFlag;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public int getAccount_number() {
        return account_number;
    }

    public void setAccount_number(int account_number) {
        this.account_number = account_number;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
