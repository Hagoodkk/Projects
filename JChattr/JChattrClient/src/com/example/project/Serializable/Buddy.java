package com.example.project.Serializable;

import java.io.Serializable;

public class Buddy implements Serializable {
    private String username;
    private String displayName;
    private String groupName;

    public Buddy(String username, String displayName, String groupName) {
        this.username = username;
        this.displayName = displayName;
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
