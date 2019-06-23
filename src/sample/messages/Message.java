package sample.messages;


import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String msg;
    private int countSign;
    private String name;
    private boolean Turn;
    private int CurrentAccount;


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setCountSign(int countSign) {
        this.countSign = countSign;
    }

    public int getCountSign() {
        return countSign;
    }

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setTurn(boolean turn) { Turn = turn; }

    public boolean isTurn() { return Turn; }

    public int getCurrentAccount() { return CurrentAccount; }

    public void setCurrentAccount(int currentAccount) { CurrentAccount = currentAccount; }
}
