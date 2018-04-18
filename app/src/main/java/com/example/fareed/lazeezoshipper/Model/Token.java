package com.example.fareed.lazeezoshipper.Model;

/**
 * Created by fareed on 18/04/2018.
 */

public class Token {
    public String token;
    public boolean isServerToken;


    public Token() {
    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
