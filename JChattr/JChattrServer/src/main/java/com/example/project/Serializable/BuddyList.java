package com.example.project.Serializable;

import java.io.Serializable;
import java.util.ArrayList;

public class BuddyList implements Serializable {
    private ArrayList<Buddy> buddies;
    private ArrayList<Buddy> currentlyOnline;
    private ArrayList<Buddy> currentlyOffline;

    public BuddyList() {
        this.buddies = new ArrayList<>();
        this.currentlyOffline = new ArrayList<>();
        this.currentlyOnline = new ArrayList<>();
    }

    public ArrayList<Buddy> getBuddies() {
        return buddies;
    }

    public void setBuddies(ArrayList<Buddy> buddies) {
        this.buddies = buddies;
    }

    public void addBuddy(Buddy buddy) {
        if (!buddies.contains(buddy)) buddies.add(buddy);
    }

    public ArrayList<Buddy> getCurrentlyOnline() {
        return currentlyOnline;
    }

    public void setCurrentlyOnline(ArrayList<Buddy> currentlyOnline) {
        this.currentlyOnline = currentlyOnline;
    }

    public ArrayList<Buddy> getCurrentlyOffline() {
        return currentlyOffline;
    }

    public void setCurrentlyOffline(ArrayList<Buddy> currentlyOffline) {
        this.currentlyOffline = currentlyOffline;
    }

    public String getGroupName(String buddyName) {
        for (Buddy buddy : buddies) {
            if (buddy.getDisplayName().equals(buddyName)) {
                return buddy.getGroupName();
            }
        }
        return null;
    }


    public boolean hasBuddy(String buddyName) {
        for (Buddy buddy : buddies) {
            if (buddy.getDisplayName().equals(buddyName)) return true;
        }
        return false;
    }
}
