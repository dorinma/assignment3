package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class UnsubscribeClient extends FrameObject {

    public UnsubscribeClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
    }

    private String id;

    private void init() {
        this.id = getHeaders().get("id");
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
}
