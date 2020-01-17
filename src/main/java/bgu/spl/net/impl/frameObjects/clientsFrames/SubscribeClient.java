package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class SubscribeClient extends FrameObject {

    public SubscribeClient(String type, HashMap<String, String> headers, String body) {

        super(type, headers, body);
        init();
    }

    private String destination;
    private String id;
    private String receiptId;

    public void init() {
        this.destination = getHeaders().get("destination");
        this.id = getHeaders().get("id");
        this.receiptId = getHeaders().get("receipt");
    }

    @Override
    public boolean execute() {
        return false;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
}
