package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.impl.frameObjects.clientsFrames.ConnectClient;
import bgu.spl.net.impl.frameObjects.clientsFrames.DisconnectClient;
import bgu.spl.net.impl.frameObjects.clientsFrames.SendClient;
import bgu.spl.net.impl.frameObjects.clientsFrames.SubscribeClient;
import bgu.spl.net.impl.frameObjects.serverFrames.ConnectedServer;
import bgu.spl.net.impl.frameObjects.serverFrames.ErrorServer;
import bgu.spl.net.impl.frameObjects.serverFrames.MessageServer;
import bgu.spl.net.impl.frameObjects.serverFrames.ReciptServer;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StompMessagingProtocolImpl<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate = false;
    private int connectionId;
    private ConnectionsImpl<FrameObject> connections;
    private int currMsdId;

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     *
     * @param connectionId
     * @param connections
     */
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl<FrameObject>) connections;
        this.connectionId = connectionId;
        currMsdId = 1;
    }

    @Override
    public void process(T message) {
        FrameObject msg = (FrameObject) message;

        if (msg.getCommand().equals("CONNECT")) {
            FrameObject msgToReply = tryLogin(msg);
            connections.send(connectionId, msgToReply);
            if (msgToReply.isError()) {
                connections.disconnect(connectionId); // TODO verify disconnect
            }
        }
        else if (msg.getCommand().equals("SUBSCRIBE")) {
            FrameObject msgToReply = trySubscribe(msg);
            connections.send(connectionId, msgToReply);
            if (msgToReply.isError()) {
                connections.disconnect(connectionId);
            }
        }
        else if (msg.getCommand().equals("DISCONNECT")) {
            FrameObject msgToReply = tryDisconnect(msg);
            connections.send(connectionId, msgToReply);
            connections.disconnect(connectionId);
        }
        else if (msg.getCommand().equals("SEND"))
        {
            if(msg.getBody().contains("borrow")) { //send borrow
                FrameObject msgToReply = tryBorrow(msg);
                //needs to send
            }

            if(msg.getBody().contains("add")) { //send add
                FrameObject msgToReply = tryBorrow(msg);
                if(!msgToReply.isError()) {
                    SendClient sendClient = (SendClient)msgToReply;
                    connections.send(sendClient.getDestination(), msgToReply);
                }
                else {
                    connections.disconnect(connectionId);
                }
            }

            if(msg.getBody().contains("return")) { //send return
                FrameObject msgToReply = tryBorrow(msg);
                //needs to send
            }

            if(msg.getBody().contains("status")) { //send status
                FrameObject msgToReply = tryBorrow(msg);
                //needs to send
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

    private boolean validateHeaders(HashMap<String, String> headers) {
        for (String s : headers.keySet()) {
            if (headers.get(s) == null)
                return false;
        }
        return true;
    }

    private FrameObject tryLogin(FrameObject msg) {
        ConnectClient cc = (ConnectClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();
        if (validateHeaders(msg.getHeaders())) { //Validate headers
            String userName = cc.getLogin();
            String password = cc.getPasscode();
            if (connections.getUsers().containsKey(userName)) { //User exists
                if (connections.getUsers().get(userName).equals(password)) { //Connection succeed
                    outHeaders.put("version", cc.getAccept());
                    return new ConnectedServer("CONNECTED", outHeaders, "");
                } else { //Password don't match
                    outHeaders.put("receipt-id", String.valueOf(connectionId));
                    outHeaders.put("message", "Wrong password");
                    return new ErrorServer("ERROR", outHeaders, "", true);
                }
            } else { //Create new user
                connections.getUsers().put(userName, password);
                outHeaders.put("accept", cc.getAccept());
                return new ConnectedServer("CONNECTED", outHeaders, "");
            }
        } else { //Invalid input
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
    }

    private FrameObject trySubscribe(FrameObject msg) {
        SubscribeClient sc = (SubscribeClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();
        if (validateHeaders(msg.getHeaders())) { //Validate headers
            if (connections.getHandlers().containsKey(connectionId)) { //Check if connection exists
                String genre = sc.getDestination();
                String receiptId = sc.getReceiptId();
                String userName = "";
                for (String s : connections.getUserHandlers().keySet()) { //Find the user name
                    if (connections.getUserHandlers().get(s) == connectionId)
                        userName = s;
                }
                if (connections.getGenreSubscribers().containsKey(genre)) { //If genre exists
                    if (connections.getGenreSubscribers().get(genre).contains(userName))  //If user is already subscribed to this topic, do nothing.
                        return null;
                    else { //User isn't subscribed to this topic
                        outHeaders.put("receipt-id", receiptId);
                        return new ReciptServer("Receipt", outHeaders, "");
                    }
                } else { //Genre doesn't exist, create new one
                    ConcurrentLinkedQueue<String> users = new ConcurrentLinkedQueue<>();
                    users.add(userName);
                    connections.getGenreSubscribers().put(genre, users);
                    outHeaders.put("receipt-id", receiptId);
                    return new ReciptServer("Receipt", outHeaders, "");
                }
            } else { //Connection doesn't exist
                outHeaders.put("receipt-id", String.valueOf(connectionId));
                outHeaders.put("message", "user is not connected");
                return new ErrorServer("ERROR", outHeaders, "", true);
            }
        } else { //Invalid input
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
    }

    private FrameObject tryDisconnect(FrameObject msg) {
        DisconnectClient dc = (DisconnectClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();
        if (validateHeaders(msg.getHeaders())) { //Validate headers
            if (connections.getHandlers().containsKey(connectionId)) { //Check if connection exists
                String receiptId = dc.getReceiptId();
                String userName = "";
                for (String s : connections.getUserHandlers().keySet()) { //Find the user name
                    if (connections.getUserHandlers().get(s) == connectionId)
                        userName = s;
                }
                outHeaders.put("receipt-id", receiptId);
                return new ReciptServer("RECEIPT", outHeaders, "");

            } else { //Connection doesn't exist
                outHeaders.put("receipt-id", String.valueOf(connectionId));
                outHeaders.put("message", "user is not connected");
                return new ErrorServer("ERROR", outHeaders, "", true);
            }
        } else { //Invalid input
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
    }

    private FrameObject tryBorrow (FrameObject msg)
    {
        return null;
    }

    private FrameObject tryAdd (FrameObject msg)
    {
        SendClient sc = (SendClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();
        if (validateHeaders(msg.getHeaders())) { //Validate headers
            if (connections.getHandlers().containsKey(connectionId)) { //Check if connection exists
                String genre = sc.getDestination();
                String userName = "";
                for (String s : connections.getUserHandlers().keySet()) { //Find the user name
                    if (connections.getUserHandlers().get(s) == connectionId)
                        userName = s;
                }
                if (connections.getGenreSubscribers().containsKey(genre)) { //If genre exists
                    outHeaders.put("subscription", String.valueOf(connectionId));
                    outHeaders.put("Message-id", String.valueOf(currMsdId));
                    currMsdId++;
                    outHeaders.put("destination", String.valueOf(genre));
                    return new MessageServer("MESSAGE", outHeaders, userName + " has added the book " + sc.getBookName());
                }
                else { //Genre doesn't exist, create new one
                    ConcurrentLinkedQueue<String> users = new ConcurrentLinkedQueue<>();
                    users.add(userName);
                    connections.getGenreSubscribers().put(genre, users);
                    outHeaders.put("subscription", String.valueOf(connectionId));
                    outHeaders.put("Message-id", String.valueOf(currMsdId));
                    currMsdId++;
                    outHeaders.put("destination", String.valueOf(genre));
                    return new MessageServer("MESSAGE", outHeaders, userName + " has added the book " + sc.getBookName());
                }
            } else { //Connection doesn't exist
                outHeaders.put("receipt-id", String.valueOf(connectionId));
                outHeaders.put("message", "user is not connected");
                return new ErrorServer("ERROR", outHeaders, "", true);
            }
        } else { //Invalid input
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
    }

    private FrameObject tryReturn (FrameObject msg)
    {
        return null;
    }

    private FrameObject status (FrameObject msg)
    {
        return null;
    }

}

