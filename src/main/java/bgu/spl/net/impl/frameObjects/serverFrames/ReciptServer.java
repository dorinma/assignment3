package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class ReciptServer extends FrameObject {

    private String receiptId;

    public ReciptServer(String type, HashMap<String, String> headers, String body){
        super(type, headers, body);
        init();
    }


    private void init() { this.receiptId = getHeaders().get("receipt-id"); }

    @Override
    public boolean execute() {
        return false;
    }
}
