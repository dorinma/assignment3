package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;

public class ConnectClient extends FrameObject {

    private String accept;
    private String host;
    private String login;
    private String passcode;

    public ConnectClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
        init();
    }

    @Override
    public boolean execute() {
        return false;
    }

    private void init() {
        this.accept = getHeaders().get("accept-version");
        this.host = getHeaders().get("host");
        this.login = getHeaders().get("login");
        this.passcode = getHeaders().get("passcode");
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}
