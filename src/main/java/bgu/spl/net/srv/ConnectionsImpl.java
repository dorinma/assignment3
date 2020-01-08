package bgu.spl.net.srv;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<String, String> users;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> genreSubscribers;
    private ConcurrentHashMap<Integer, ConnectionHandlerImpl> handlers;
    private ConcurrentHashMap<String, Integer> userHandlers;

    public ConnectionsImpl() {
        users = new ConcurrentHashMap<>();
        genreSubscribers = new ConcurrentHashMap<>();
        userHandlers = new ConcurrentHashMap<>();
        handlers = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(handlers.containsKey(connectionId)) {
            ConnectionHandlerImpl connectionHandler = handlers.get(connectionId);
            connectionHandler.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void send(String genre, T msg) {
       // FrameObject frameObject = (FrameObject)msg;
        if(genreSubscribers.containsKey(genre) && !genreSubscribers.get(genre).isEmpty()) {
            ConcurrentLinkedQueue<String> subUsers = genreSubscribers.get(genre);
            for (String s : subUsers) {
                int connId = userHandlers.get(s);
                send(connId, msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if(handlers.containsValue(connectionId)){
            //Find the user with connectionId
            String currUser = "";
            for (String s : userHandlers.keySet()) {
                if(userHandlers.get(s) == connectionId)
                    currUser = s;
            }

            //Delete this user from topics he's subscribed to
            for (String genre : genreSubscribers.keySet()) {
                ConcurrentLinkedQueue<String> subUsers = genreSubscribers.get(genre);
                for (String usr : subUsers) {
                    if(usr.equals(currUser)) {
                        subUsers.remove(usr);
                        genreSubscribers.replace(genre, subUsers);
                    }
                }
            }

            //Close the socket
        }
    }

    public ConcurrentHashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(ConcurrentHashMap<String, String> users) {
        this.users = users;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> getGenreSubscribers() {
        return genreSubscribers;
    }

    public void setGenreSubscribers(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> genreSubscribers) {
        this.genreSubscribers = genreSubscribers;
    }

    public ConcurrentHashMap<Integer, ConnectionHandlerImpl> getHandlers() {
        return handlers;
    }

    public void setHandlers(ConcurrentHashMap<Integer, ConnectionHandlerImpl> handlers) {
        this.handlers = handlers;
    }

    public ConcurrentHashMap<String, Integer> getUserHandlers() {
        return userHandlers;
    }

    public void setUserHandlers(ConcurrentHashMap<String, Integer> userHandlers) {
        this.userHandlers = userHandlers;
    }
}
