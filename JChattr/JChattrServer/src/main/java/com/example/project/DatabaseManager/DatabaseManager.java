package com.example.project.DatabaseManager;

import com.example.project.Serializable.Buddy;
import com.example.project.Serializable.BuddyList;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private Connection connection;

    private final String h2Folder = "jdbc:h2:~/ChatterDatabase";
    private final String h2Username = "hVwiUW4ujGAhmS6s";
    private final String h2Password = "2KNlRLJG0ZqRTRpd";

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager();
            databaseManager.createConnection();
        }
        return databaseManager;
    }

    private void createConnection() {
        try {
            this.connection = DriverManager.getConnection(h2Folder, h2Username, h2Password);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("Could not create database connection.");
            System.exit(1);
        }
    }

    public boolean createTables() {
        try {
            Statement statement = connection.createStatement();
            String createUsersTable = "CREATE TABLE Users (\n" +
                    "\tUserID Integer PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    Username varchar(255) NOT NULL,\n" +
                    "    DisplayName varchar(255) NOT NULL,\n" +
                    "    PasswordSaltedHash varchar(255) NOT NULL,\n" +
                    "    PasswordSalt varchar(255) NOT NULL\n" +
                    ");";
            statement.executeUpdate(createUsersTable);
            String createBuddyListTable = "CREATE TABLE BuddyList (\n" +
                    "\tUserID Integer NOT NULL,\n" +
                    "    BuddyID Integer NOT NULL,\n" +
                    "    GroupName varchar(255) NOT NULL,\n" +
                    "    CONSTRAINT FK_UserKey FOREIGN KEY (UserID)\n" +
                    "    REFERENCES Users(UserID),\n" +
                    "    CONSTRAINT FK_BuddyKey FOREIGN KEY (BuddyID)\n" +
                    "    REFERENCES Users(UserID)\n" +
                    ");";
            statement.executeUpdate(createBuddyListTable);
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean addUser(String username, String passwordSaltedHash, String passwordSalt) {
        String displayName = username;
        username = username.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String checkUser = "SELECT UserID FROM Users WHERE Username='" + username + "'";
            ResultSet rs = statement.executeQuery(checkUser);
            if (!rs.next()) {
                String addUser =
                        "INSERT INTO Users (Username, DisplayName, PasswordSaltedHash, PasswordSalt) " +
                                "VALUES ('" + username + "', '" + displayName + "', '" + passwordSaltedHash + "', '" + passwordSalt + "')";
                statement.executeUpdate(addUser);
                return true;
            } else {
                System.out.println("User already exists");
                return false;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public String getUserDisplayName(String username) {
        username = username.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String getDisplayName = "SELECT DisplayName FROM Users WHERE Username='" + username + "'";
            ResultSet rs = statement.executeQuery(getDisplayName);
            if (rs.next()) {
                return rs.getString("DisplayName");
            } else return null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public boolean removeUser(String username) {
        username = username.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String deleteUserBuddies =
                    "DELETE FROM BuddyList WHERE UserID=(SELECT UserID FROM Users WHERE Username='" + username + "')";
            statement.executeUpdate(deleteUserBuddies);
            String deleteFromBuddies =
                    "DELETE FROM BuddyList WHERE BuddyID=(SELECT UserID FROM Users WHERE Username='" + username + "')";
            statement.executeUpdate(deleteFromBuddies);
            String deleteUser = "DELETE FROM Users WHERE Username='" + username + "'";
            statement.executeUpdate(deleteUser);
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean addBuddyToUser(String username, String buddyName, String groupName) {
        username = username.toLowerCase();
        buddyName = buddyName.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String checkIfBuddyExists = "SELECT UserID FROM BuddyList WHERE " +
                    "UserID=(SELECT UserID FROM Users WHERE Username='" + username + "') " +
                    "AND BuddyID=(SELECT UserID FROM Users WHERE Username='" + buddyName + "')";
            ResultSet rs = statement.executeQuery(checkIfBuddyExists);
            if (!rs.next()) {
                String addBuddy = "INSERT INTO BuddyList (UserID, BuddyID, GroupName) VALUES " +
                        "((SELECT UserID FROM Users WHERE Username='" + username + "'), " +
                        "(SELECT UserID FROM Users WHERE Username='" + buddyName + "'), " +
                        "'" + groupName + "')";
                statement.executeUpdate(addBuddy);
                return true;
            } else {
                System.out.println("Buddy already exists.");
                return false;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean removeBuddyFromUser(String username, String buddyName) {
        username = username.toLowerCase();
        buddyName = buddyName.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String removeBuddy = "DELETE FROM BuddyList WHERE " +
                    "UserID=(SELECT UserID FROM Users WHERE Username='" + username + "') AND " +
                    "BuddyID=(SELECT UserID FROM Users WHERE Username='" + buddyName + "')";
            statement.executeUpdate(removeBuddy);
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public String getUserSalt(String username) {
        username = username.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String getSalt = "SELECT PasswordSalt FROM Users WHERE Username='" + username + "'";
            ResultSet rs = statement.executeQuery(getSalt);
            if (rs.next()) {
                return rs.getString("PasswordSalt");
            } else return null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public boolean comparePasswordSaltedHash(String username, String passwordSaltedHash) {
        username = username.toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String usernameSaltedHash = "SELECT PasswordSaltedHash FROM Users WHERE Username='" + username + "'";
            ResultSet rs = statement.executeQuery(usernameSaltedHash);
            if (rs.next()) {
                String storedPasswordSaltedHash = rs.getString("PasswordSaltedHash");
                if (storedPasswordSaltedHash.equals(passwordSaltedHash)) return true;
            }
            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public BuddyList getBuddyList(String username) {
        username = username.toLowerCase();
        BuddyList buddyList = new BuddyList();
        try {
            Statement statement = connection.createStatement();
            String getBuddies = "SELECT DisplayName, GroupName FROM Users\n" +
                    "INNER JOIN BuddyList ON Users.UserID=BuddyList.BuddyID\n" +
                    "WHERE BuddyList.UserID=(SELECT UserID FROM Users WHERE Username='" + username + "')";
            ResultSet rs = statement.executeQuery(getBuddies);
            while (rs.next()) {
                String displayName = rs.getString("DisplayName");
                buddyList.addBuddy(new Buddy(displayName.toLowerCase(), displayName, rs.getString("GroupName")));
            }
            return buddyList;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public void dropAllTables() {
        try {
            Statement statement = connection.createStatement();
            String dropUsers = "DROP TABLE Users";
            String dropBuddies = "DROP TABLE BuddyList";
            statement.executeUpdate(dropBuddies);
            statement.executeUpdate(dropUsers);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void viewUsers() {
        try {
            Statement statement = connection.createStatement();
            String getUsers = "SELECT * FROM  Users";
            ResultSet rs = statement.executeQuery(getUsers);
            while (rs.next()) {
                System.out.println(rs.getString("UserID") + "," + rs.getString("Username") + "," +
                        rs.getString("DisplayName"));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void viewBuddyList() {
        try {
            Statement statement = connection.createStatement();
            String getBuddyList = "SELECT * FROM  BuddyList";
            ResultSet rs = statement.executeQuery(getBuddyList);
            while (rs.next()) {
                System.out.println(rs.getString("UserID") + "," + rs.getString("BuddyID") + "," +
                        rs.getString("GroupName"));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
