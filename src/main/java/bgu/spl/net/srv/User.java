package bgu.spl.net.srv;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class User {

    private String userName; //cant be users with the same name
    private String userPass;
    private boolean isLogged;
    private int connectionId;
    private List genres;
    private HashMap<Integer, String> idSubscriptions;

    public User(String userName, String userPass, int connectionHandlerId) {
        this.userName = userName;
        this.userPass = userPass;
        this.isLogged = true;
        this.connectionId = connectionHandlerId;
        this.genres = new LinkedList();
        this.idSubscriptions = new HashMap<>();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public List getGenres() {
        return genres;
    }

    public void setGenres(List genres) {
        this.genres = genres;
    }

    public HashMap<Integer, String> getIdSubscriptions() {
        return idSubscriptions;
    }

    public void setIdSubscriptions(HashMap<Integer, String> books_geners) {
        this.idSubscriptions = books_geners;
    }


}
