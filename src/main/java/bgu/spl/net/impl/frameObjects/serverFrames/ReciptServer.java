package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class ReciptServer extends FrameObject {

    public ReciptServer(String type, HashMap<String, String> headers, String body){
        super(type, headers, body);
    }

    @Override
    public boolean execute() {
        return false;
    }
}
