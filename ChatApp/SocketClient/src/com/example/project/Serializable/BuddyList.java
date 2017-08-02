package com.example.project.Serializable;

import java.io.Serializable;
import java.util.ArrayList;

public class BuddyList implements Serializable {
    private ArrayList<String> buddyList;
    private ArrayList<String> currentlyOnline;
    private ArrayList<String> currentlyOffline;

    public BuddyList(ArrayList<String> buddyList) {
        this.buddyList = buddyList;
        this.currentlyOffline = new ArrayList<>();
        this.currentlyOnline = new ArrayList<>();
    }
    public void setBuddyList(ArrayList<String> buddyList) { this.buddyList = buddyList; }
    public ArrayList<String> getBuddies() { return this.buddyList; }

    public ArrayList<String> getCurrentlyOnline() {
        return currentlyOnline;
    }

    public void setCurrentlyOnline(ArrayList<String> currentlyOnline) {
        this.currentlyOnline = currentlyOnline;
    }

    public ArrayList<String> getCurrentlyOffline() {
        return currentlyOffline;
    }

    public void setCurrentlyOffline(ArrayList<String> currentlyOffline) {
        this.currentlyOffline = currentlyOffline;
    }
}
