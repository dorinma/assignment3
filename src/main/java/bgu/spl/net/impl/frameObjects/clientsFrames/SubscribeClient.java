package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class SubscribeClient extends FrameObject {

    public SubscribeClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
    }

    private String destination;
    private String id;
    private String receiptId;

    private void init() {
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
