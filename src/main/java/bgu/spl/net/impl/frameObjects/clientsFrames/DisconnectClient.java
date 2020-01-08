package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class DisconnectClient extends FrameObject {

    private String receiptId;

    public DisconnectClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
        init();
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    private void init() {
        this.receiptId = getHeaders().get("receipt");
    }

    @Override
    public boolean execute() {
        return false;
    }
}
