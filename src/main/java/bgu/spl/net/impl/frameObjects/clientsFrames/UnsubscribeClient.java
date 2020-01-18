package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class UnsubscribeClient extends FrameObject {

    public UnsubscribeClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
        init();
    }

    private String id;
    private String receiptId;

    private void init() {
        this.id = getHeaders().get("id");
        this.receiptId = getHeaders().get("receipt");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean execute() {
        return false;
    }

    public String getReceiptId() {
        return receiptId;
    }
}
