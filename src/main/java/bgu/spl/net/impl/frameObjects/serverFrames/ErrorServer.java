package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class ErrorServer extends FrameObject {
    public ErrorServer(String command, HashMap<String, String> headers, String body) {
        super(command, headers, body);
    }

    @Override
    public boolean execute() {
        return false;
    }
}
