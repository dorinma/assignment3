package bgu.spl.net.impl.frameObjects.serverFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class MessageServer extends FrameObject {
    public MessageServer(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
//        System.out.println(type + ", \n" );
//        for (String s : headers.keySet()) {
//            System.out.println(s + " - " + headers.get(s) + "\n");
//        }
//        System.out.println("body:" + body);

    }

    @Override
    public boolean execute() {
        return false;
    }
}
