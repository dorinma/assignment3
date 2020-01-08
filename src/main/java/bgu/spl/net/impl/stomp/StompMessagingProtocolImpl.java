package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.impl.frameObjects.clientsFrames.ConnectClient;
import bgu.spl.net.impl.frameObjects.clientsFrames.SubscribeClient;
import bgu.spl.net.impl.frameObjects.serverFrames.ConnectedServer;
import bgu.spl.net.impl.frameObjects.serverFrames.ErrorServer;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;

import java.util.HashMap;

public class StompMessagingProtocolImpl<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate = false;
    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     *
     * @param connectionId
     * @param connections
     */

    private int connectionId;
    private ConnectionsImpl<FrameObject> connections;
    private String currUser;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl<FrameObject>) connections;
    }

    @Override
    public void process(T message) {
        FrameObject msg = (FrameObject)message;
        if(msg.getCommand().equals("CONNECT")) {
            ConnectClient cc = (ConnectClient)msg;
            String userName = cc.getLogin();
            String password = cc.getPasscode();
            if(connections.getUsers().containsKey(userName)) {
                if(connections.getUsers().get(userName).equals(password)) {
                    HashMap<String, String> outHeaders = new HashMap<>();
                    outHeaders.put("version", cc.getAccept());
                    ConnectedServer cs = new ConnectedServer("CONNECTED", outHeaders, "");
                    currUser = userName;
                }
                else {
                    HashMap<String, String> outHeaders = new HashMap<>();
                    outHeaders.put("receipt-id", ""); //TODO where do I get the id from?
                    outHeaders.put("message", "user name and password do not match");
                    ErrorServer es = new ErrorServer("ERROR", outHeaders, "");


                    //TODO need to close this connection
                }
            }
            else {
                connections.getUsers().put(userName, password);
                HashMap<String, String> outHeaders = new HashMap<>();
                outHeaders.put("accept", cc.getAccept());
                ConnectedServer cs = new ConnectedServer("CONNECTED",outHeaders, "");
                currUser = userName;
            }
        }

        else if(msg.getCommand().equals("SUBSCRIBE")) {
            SubscribeClient sc = (SubscribeClient)msg;
            String destination = sc.getDestination();
            //String id = sc.getId();
            String receiptId = sc.getReceiptId();
            //TODO check first if there's a user connected
            if(connections.getGenreSubscribers().containsKey(destination)) {
                if(connections.getGenreSubscribers().get(destination).contains(currUser)); //If user is already subscribe to this topic, do nothing.
            }
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
