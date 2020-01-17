package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.impl.frameObjects.clientsFrames.*;
import bgu.spl.net.impl.frameObjects.serverFrames.ConnectedServer;
import bgu.spl.net.impl.frameObjects.serverFrames.ErrorServer;
import bgu.spl.net.impl.frameObjects.serverFrames.MessageServer;
import bgu.spl.net.impl.frameObjects.serverFrames.ReciptServer;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.User;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StompMessagingProtocolImpl<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate = false;
    private int connectionId;
    private ConnectionsImpl<FrameObject> connections;
    private int currMsgId;

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
        currMsgId = 1;
        shouldTerminate = false;
    }

    @Override
    public void process(T message) throws IOException {
        FrameObject msg = (FrameObject) message;

        if (msg.getCommand().equals("CONNECT")) {
            FrameObject msgToReply = tryLogin(msg);
            connections.send(connectionId, msgToReply);
//            if (shouldTerminate)
//                connections.disconnect(connectionId);

        } else if (msg.getCommand().equals("SUBSCRIBE")) {
            FrameObject msgToReply = trySubscribe(msg);
            connections.send(connectionId, msgToReply);
//            if (shouldTerminate)
//                connections.disconnect(connectionId);

        } else if (msg.getCommand().equals("UNSUBSCRIBE")) {
            FrameObject msgToReply = tryUnsubscribe(msg);
            connections.send(connectionId, msgToReply);
//            if (shouldTerminate)
//                connections.disconnect(connectionId);

        } else if (msg.getCommand().equals("DISCONNECT")) {
            FrameObject msgToReply = tryDisconnect(msg);
            connections.send(connectionId, msgToReply);
            // connections.disconnect(connectionId);

        } else if (msg.getCommand().equals("SEND")) {
            FrameObject msgToReply = trySend(msg);
            connections.send(msgToReply.getHeaders().get("destination"), msgToReply);
//            if (shouldTerminate)
//                connections.disconnect(connectionId);
        }

    }

    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private FrameObject tryLogin(FrameObject msg) {
        ConnectClient cc = (ConnectClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();

        if (!validateHeaders(msg.getHeaders())) { //Invalidate headers
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        boolean foundUser = false;

        for (Integer id : connections.getUsers().keySet()) {
            User u = connections.getUsers().get(id);

            if (u.getUserName().equals(((ConnectClient) msg).getLogin()))
            {

                foundUser = true;
                if (u.isLogged())
                {
                    shouldTerminate = true;
                    outHeaders.put("receipt-id", String.valueOf(connectionId));
                    outHeaders.put("message", "cannot logout from a logout user");
                    return new ErrorServer("ERROR", outHeaders, "", true);
                }
                else
                {
                    if (u.getUserPass().equals(((ConnectClient) msg).getPasscode()))
                    {
                        //Connection succeed
                        u.setLogged(true);
                        u.setConnectionId(connectionId);
                        connections.getUsers().remove(id);
                        connections.getUsers().put(connectionId, u);
                        outHeaders.put("version", cc.getAccept());
                        return new ConnectedServer("CONNECTED", outHeaders, "");
                    }
                    else
                    {
                        //Password doesn't match
                        System.out.println("~~~~~~~~Password doesn't match\n");
                        shouldTerminate = true;
                        outHeaders.put("receipt-id", String.valueOf(connectionId));
                        outHeaders.put("message", "Wrong password");
                        //return new ErrorServer("ERROR", outHeaders, "", true);
                        return new ReciptServer("RECEIPT", outHeaders, "");
                    }
                }
            }
        }
        if (!foundUser) {
            //create new user
            User newUser = new User(((ConnectClient) msg).getLogin(), ((ConnectClient) msg).getPasscode(), connectionId);
            newUser.setLogged(true);
            connections.getUsers().put(connectionId, newUser);
            outHeaders.put("version", cc.getAccept());
            return new ConnectedServer("CONNECTED", outHeaders, "");

        }
        return null;
    }

    private FrameObject trySubscribe(FrameObject msg) {
        SubscribeClient sc = (SubscribeClient) msg;
        sc.init();
        HashMap<String, String> outHeaders = new HashMap<>();

        if (!validateHeaders(msg.getHeaders())) //Invalidate headers
        {
            System.out.println("XXX NOT VALID INPUT XXX");
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        User currUser = connections.getUsers().get(connectionId);
        if (currUser == null || !currUser.isLogged()) //user does not connect/ exist
        {
            System.out.println("XXX NOT VALID USER XXX");
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "user is not connected");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
        String genre = sc.getDestination();
        String receiptId = sc.getReceiptId();
        String temp = sc.getId();
        int subId = Integer.parseInt(temp);

        if (connections.getGenreSubscribers().containsKey(genre)) { //If genre exists
            if (connections.getGenreSubscribers().get(genre).contains(currUser))  //If user is already subscribed to this topic, do nothing.
                return null;
            else {
                //User isn't subscribed to this topic
                currUser.getGenres().add(genre);
                currUser.getIdSubscriptions().put(subId, genre);
                connections.getGenreSubscribers().get(genre).add(currUser);
                outHeaders.put("receipt-id", receiptId);
                return new ReciptServer("RECEIPT", outHeaders, "");
            }
        } else { //Genre doesn't exist, create new one
            LinkedList<User> users = new LinkedList<>();
            currUser.getGenres().add(genre);
            currUser.getIdSubscriptions().put(subId, genre);
            users.add(currUser);
            connections.getGenreSubscribers().put(genre, users);
            outHeaders.put("receipt-id", receiptId);
            return new ReciptServer("RECEIPT", outHeaders, "");
        }
    }

    private FrameObject tryUnsubscribe(FrameObject msg) {

        UnsubscribeClient sc = (UnsubscribeClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();

        if (!validateHeaders(msg.getHeaders())) //Invalidate headers
        {
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        User currUser = connections.getUsers().get(connectionId);
        if (currUser == null || !currUser.isLogged()) //user does not connect/ exist
        {
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "user is not connected");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        int subId = Integer.parseInt(sc.getId());
        String genre = currUser.getIdSubscriptions().get(subId);
        if (genre == null) {
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "user does not subscribe to this genre");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }
        List<User> usersSub = connections.getGenreSubscribers().get(genre);

        usersSub.remove(currUser);
        currUser.getGenres().remove(genre);
        outHeaders.put("receipt-id", String.valueOf(connectionId));
        outHeaders.put("message", "user is not connected");
        return new ErrorServer("RECEIPT", outHeaders, "", true);
    }

    private FrameObject tryDisconnect(FrameObject msg) {

        shouldTerminate = true;
        DisconnectClient dc = (DisconnectClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();

        User currUser = connections.getUsers().get(connectionId);
        if (currUser == null || !currUser.isLogged()) //user does not connect/ exist
        {
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "user is not connected");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        if (!validateHeaders(msg.getHeaders())) { // not validate headers
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        String receiptId = dc.getReceiptId();
        currUser.setLogged(false);
        outHeaders.put("receipt-id", receiptId);

        return new ReciptServer("RECEIPT", outHeaders, "");
    }

    private FrameObject trySend (FrameObject msg) {

        SendClient sc = (SendClient) msg;
        HashMap<String, String> outHeaders = new HashMap<>();
        if (!validateHeaders(msg.getHeaders())) {  //validate headers
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "malformed frame received");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        User currUser = connections.getUsers().get(connectionId);
        if (currUser == null || !currUser.isLogged()) //user isn't connected or doesn't exist
        {
            shouldTerminate = true;
            outHeaders.put("receipt-id", String.valueOf(connectionId));
            outHeaders.put("message", "user is not connected");
            return new ErrorServer("ERROR", outHeaders, "", true);
        }

        String destination = sc.getDestination(); //this is the genre
        int subscription = -1;
        //TODO fix
        int newId = 1 + currUser.getIdSubscriptions().size() +2000;
        for (int i : currUser.getIdSubscriptions().keySet()) {
            if(currUser.getIdSubscriptions().get(i).equals(destination)){
                newId++;
                subscription = i;
            }
        }

        if (subscription == -1) { //create new genre
            String genre = sc.getDestination();
            LinkedList<User> users = new LinkedList<>();
            currUser.getGenres().add(genre);
            currUser.getIdSubscriptions().put(newId, genre);
            users.add(currUser);
            connections.getGenreSubscribers().put(genre, users);
            //outHeaders.put("receipt-id", receiptId);

            outHeaders.put("subscription", Integer.toString(newId));
            outHeaders.put("Message-id", Integer.toString(currMsgId));
            currMsgId++;
            outHeaders.put("destination", destination);

            return  new MessageServer("MESSAGE", outHeaders, sc.getBody());

        }

        outHeaders.put("subscription", Integer.toString(subscription));
        outHeaders.put("Message-id", Integer.toString(currMsgId));
        currMsgId++;
        outHeaders.put("destination", destination);
        return  new MessageServer("MESSAGE", outHeaders, sc.getBody());
    }

    private boolean validateHeaders(HashMap<String, String> headers) {
        for (String s : headers.keySet()) {
            if (headers.get(s) == null)
                return false;
        }
        return true;
    }


}

