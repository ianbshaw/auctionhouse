package bank;

import java.io.Serializable;

/*class to represent a request from bank to freeze portion of account for bid*/
public class LockFundsRequest implements Serializable {
    private int quantity;
    private String from;
    private String to;
    private boolean UnlockFlag;
    private boolean InsufficientFunds;

    public boolean isInsufficientFunds() {
        return InsufficientFunds;
    }

    public void setInsufficientFunds(boolean insufficientFunds) {
        InsufficientFunds = insufficientFunds;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isUnlockFlag() {
        return UnlockFlag;
    }

    public void setUnlockFlag(boolean unlockFlag) {
        UnlockFlag = unlockFlag;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
