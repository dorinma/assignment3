package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class ErrorServer extends FrameObject {

    private String receiptId;
    private String message;

    public ErrorServer(String command, HashMap<String, String> headers, String body, boolean isError) {
        super(command, headers, body, isError);
        init();
    }

    private void init() {
        this.receiptId = getHeaders().get("receipt-id");
        this.message = getHeaders().get("message");
    }

    @Override
    public boolean execute() {
        return false;
    }
}
