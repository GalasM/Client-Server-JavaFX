package sample.messages;

import java.io.Serializable;

public class Message implements Serializable {
    MessageType type;
    String msg;

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


}
