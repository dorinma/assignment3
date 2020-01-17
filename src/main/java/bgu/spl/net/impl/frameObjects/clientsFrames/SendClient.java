package bgu.spl.net.impl.frameObjects.clientsFrames;

import bgu.spl.net.impl.frameObjects.FrameObject;
import java.util.HashMap;

public class SendClient extends FrameObject {
    private String destination;
    private String mission;
    private String bookName;

    public SendClient(String type, HashMap<String, String> headers, String body) {
        super(type, headers, body);
        init();
    }

    private void init() {
        this.destination = getHeaders().get("destination");
        if(getBody().contains("added"))
            mission = "add";
        else if(getBody().contains("borrow"))
            mission = "borrow";
        else if(getBody().contains("Returning"))
            mission = "return";
        else if(getBody().contains("status"))
            mission = "status";
        String [] bodyWords = getBody().split(" ");
        this.bookName = bodyWords[bodyWords.length-1];
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    @Override
    public boolean execute() {
        return false;
    }
}
