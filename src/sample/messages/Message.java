package sample.messages;


import javafx.scene.control.Button;

import java.io.Serializable;

public class Message implements Serializable {
    MessageType type;
    String msg;
    Button sign = null;
    int countSign;

    public void setSign(Button sign) {
        this.sign = sign;
    }

    public Button getSign() {
        return sign;
    }

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
}
