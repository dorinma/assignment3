package bgu.spl.net.srv;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, User> users; //CHid_user
    private ConcurrentHashMap<String, LinkedList<User>> genreSubscribers; //genre_users
    private ConcurrentHashMap<Integer, ConnectionHandlerImpl> handlers; //????


    public ConnectionsImpl() {
        users = new ConcurrentHashMap<>();
        genreSubscribers = new ConcurrentHashMap<>();
        handlers = new ConcurrentHashMap<>();    ////////TO ADD THE CONHAN TO THE LIST
    }
    @Override
    public boolean send(int connectionId, T msg) {
        if (handlers.containsKey(connectionId)) {
            ConnectionHandlerImpl connectionHandler = handlers.get(connectionId);
            connectionHandler.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void send(String genre, T msg) {
        if (genreSubscribers.containsKey(genre) && !genreSubscribers.get(genre).isEmpty()) {
            LinkedList<User> subUsers = genreSubscribers.get(genre);
            for (User u : subUsers) {
                send(u.getConnectionId(), msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) { //TODO delete from subGenres, delete from handlers, close socket
        //delete this connections handler
       User currUser = users.get(connectionId);
       currUser.setLogged(false);
       currUser.getGenres().clear();
       currUser.getIdSubscriptions().clear();

        if (handlers.containsValue(connectionId)) {
            handlers.remove(connectionId);
        }

        //Delete this user from topics he's subscribed to
        for (String genre : genreSubscribers.keySet()) {
            LinkedList<User> subUsers = genreSubscribers.get(genre);
            for (User u : subUsers) {
                subUsers.remove(u);
                genreSubscribers.replace(genre, subUsers);
            }
        }

        //Close the socket


    }





    public ConcurrentHashMap<Integer, User> getUsers() {
        return users;
    }

    public void setUsers(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    public ConcurrentHashMap<String, LinkedList<User>> getGenreSubscribers() {
        return genreSubscribers;
    }

    public void setGenreSubscribers(ConcurrentHashMap<String, LinkedList<User>> genreSubscribers) {
        this.genreSubscribers = genreSubscribers;
    }

    public ConcurrentHashMap<Integer, ConnectionHandlerImpl> getHandlers() {
        return handlers;
    }

    public void setHandlers(ConcurrentHashMap<Integer, ConnectionHandlerImpl> handlers) {
        this.handlers = handlers;
    }

}

