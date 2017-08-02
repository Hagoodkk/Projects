package com.example.project.Serializable;

import java.io.Serializable;

public class UserCredentials implements Serializable{
    private String username;
    private String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return this.username; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return this.password; }
}
