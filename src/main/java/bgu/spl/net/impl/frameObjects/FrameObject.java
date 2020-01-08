package bgu.spl.net.impl.frameObjects;

import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;

public abstract class FrameObject {

    private String command;
    private HashMap<String, String> headers;
    private String body;
    private final String frameEnd = "\u0000";


    public FrameObject(String command, HashMap<String, String> headers, String body) {
        this.command = command;
        this.headers = headers;
        this.body = body;
    }

    public abstract boolean execute();

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public HashMap<String, String>  getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String>  headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrameEnd() {
        return frameEnd;
    }

    @Override
    public String toString() { return ""; } //TODO implement
}
