package com.example.project;

import com.example.project.DatabaseManager.DatabaseManager;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Buddy;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseManagerTest {
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    @Test
    public void stage1_createTablesTest() throws Exception {
        databaseManager.createTables();
    }
    @Test
    public void stage2_addUserTest() throws Exception {
        assert(databaseManager.addUser("Alice", "TestSaltedHash", "TestSalt"));
        assert(databaseManager.addUser("Bob", "TestSaltedHash2", "TestSalt2"));
        assert(databaseManager.addUser("Charles", "TestSaltedHash3", "TestSalt3"));
        assert(!databaseManager.addUser("Alice", "TestSaltedHash", "TestSalt"));
    }
    @Test
    public void stage3_addBuddyToUserTest() throws Exception {
        assert(databaseManager.addBuddyToUser("Alice", "Bob", "Friends"));
        assert(databaseManager.addBuddyToUser("Alice", "Charles", "Friends"));
        assert(databaseManager.addBuddyToUser("Charles", "Alice", "Friends"));
        assert(!databaseManager.addBuddyToUser("Alice", "Bob", "Friends"));
    }
    @Test
    public void stage9_viewUsers() throws  Exception {
        databaseManager.viewUsers();
        System.out.println();
    }
    @Test
    public void stage10_viewBuddyList() throws  Exception {
        databaseManager.viewBuddyList();
        System.out.println();
    }
    @Test
    public void stage4_comparePasswordSaltedHashTest() throws Exception {
        assert(databaseManager.comparePasswordSaltedHash("Alice", "TestSaltedHash"));
        assert(!databaseManager.comparePasswordSaltedHash("Alice", "This"));
    }
    @Test
    public void stage5_getBuddyListTest() throws Exception {
        BuddyList buddyList = databaseManager.getBuddyList("Alice");
        ArrayList<Buddy> buddies = buddyList.getBuddies();
        assert(buddies.size() == 2);
        assert(buddies.get(0).getDisplayName().equals("Bob"));
        assert(buddies.get(1).getDisplayName().equals("Charles"));
        buddyList = databaseManager.getBuddyList("Charles");
        buddies = buddyList.getBuddies();
        assert(buddies.size() == 1);
        assert(buddies.get(0).getDisplayName().equals("Alice"));
    }
    @Test
    public void stage6_removeBuddyFromUserTest() throws Exception {
        databaseManager.removeBuddyFromUser("Alice", "Charles");
        BuddyList buddyList = databaseManager.getBuddyList("Alice");
        ArrayList<Buddy> buddies = buddyList.getBuddies();
        assert(buddies.size() == 1);
        assert(buddies.get(0).getDisplayName().equals("Bob"));
    }
    @Test
    public void stage7_removeUserTest() throws Exception {
        databaseManager.removeUser("Bob");
        BuddyList buddyList = databaseManager.getBuddyList("Alice");
        ArrayList<Buddy> buddies = buddyList.getBuddies();
        assert(buddies.size() == 0);
    }

    @Test
    public void stage8_resetTests() throws Exception {
        databaseManager.dropAllTables();
    }




}
