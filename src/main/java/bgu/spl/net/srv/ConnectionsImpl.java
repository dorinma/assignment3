package bgu.spl.net.srv;

import bgu.spl.net.impl.frameObjects.FrameObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, User> users; //CHid_user
    private ConcurrentHashMap<String, LinkedList<User>> genreSubscribers; //genre_users
    private ConcurrentHashMap<Integer, ConnectionHandler> handlers;

    private static class SingletopnConnectionImpl {
        private static ConnectionsImpl instance = new ConnectionsImpl<FrameObject>();
    }

    public static ConnectionsImpl getInstance() {
        return SingletopnConnectionImpl.instance;
    }

    public ConnectionsImpl() {
        users = new ConcurrentHashMap<>();
        genreSubscribers = new ConcurrentHashMap<>();
        handlers = new ConcurrentHashMap<>();
    }
    public String toString()
    {
        String toPrint ="users:" + "\n";

        for (Integer id : users.keySet()) {
            toPrint = toPrint + id + users.get(id).toString() + "\n";
        }
        toPrint = toPrint + "handlers:" + "\n";
        for (Integer id : handlers.keySet()) {
            toPrint = toPrint + id + "\n";
        }
        return toPrint;
    }


    @Override
    public boolean send(int connectionId, T msg) throws IOException {
        if (handlers.containsKey(connectionId)) {
            handlers.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void send(String genre, T msg) throws IOException {
        if (genreSubscribers.containsKey(genre) && !genreSubscribers.get(genre).isEmpty()) {
            LinkedList<User> subUsers = genreSubscribers.get(genre);
            for (User u : subUsers) {
                send(u.getConnectionId(), msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
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

    public ConcurrentHashMap<Integer, ConnectionHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(ConcurrentHashMap<Integer, ConnectionHandler> handlers) {
        this.handlers = handlers;
    }

    public <T> void addHandler (int id, ConnectionHandler<T> ch)
    {
        this.handlers.put(id, ch);
    }

}

