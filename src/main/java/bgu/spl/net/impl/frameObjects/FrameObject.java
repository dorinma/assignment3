package bgu.spl.net.impl.frameObjects;

import java.util.HashMap;

public abstract class FrameObject {

    private String command;
    private HashMap<String, String> headers;
    private String body;
    private final String frameEnd = "\u0000";
    private boolean isError = false;

    public FrameObject(String command, HashMap<String, String> headers, String body) {
        this.command = command;
        this.headers = headers;
        this.body = body;
    }

    public FrameObject(String command, HashMap<String, String> headers, String body, boolean isError) {
        this.command = command;
        this.headers = headers;
        this.body = body;
        this.isError = isError;
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

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    @Override
    public String toString() {
        String output = this.command + "\n";
        for (String key : headers.keySet()) {
            output += key + ":" + headers.get(key) + "\n";
        }
        output += body +'\n' + '\u0000';
        return output;
    }
}
