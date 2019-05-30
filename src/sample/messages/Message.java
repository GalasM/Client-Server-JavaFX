package sample.messages;


import java.io.Serializable;

public class Message implements Serializable {
    MessageType type;
    String msg;
    int countSign;
    String name;
    boolean Turn;


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
}
