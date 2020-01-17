package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class ConnectedServer extends FrameObject {
    public ConnectedServer(String command, HashMap<String, String> headers, String body) {
        super(command, headers, body);
    }


    @Override
    public boolean execute() {
        return false;
    }
}
