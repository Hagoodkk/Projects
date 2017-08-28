1. **JChattr (Personal Project)** - chat application with a multi-threaded server that accepts client connections through the uses of sockets. It uses data streams and serializable objects to pass back and forth information. JavaFX is used for UI development. IntelliJ IDE used for development and application runs on this platform. Note when testing that the "Add Buddy" button references the Server's database for the buddy being added, so only users that are registered with the app can be added to the buddy list. Simply create two accounts for testing purposes, then add each to each other's list.

    *Check It Out!*
    --------------------------------
    JChattr is currently hosted on a RaspberryPi and can be accessed publicly.
    - **[JChattrClient](https://www.dropbox.com/s/d0sedy8yougbafi/JChattrClient.jar?dl=0)**
    - Download the executable JAR file above, and double-click it to get started
    - Accounts "Bob" and "Carl" have been created for testing purposes, both with password "m"
    - Log in to the test accounts and play around with the app. If you make your own accounts to talk with each other, know that a name cannot be added to the buddy list until the account for that name is created.
    
     *Behavioral Features*
     ---------------------------------
     - [x] **Instant Messaging**
     - [x] **Dynamic Buddy Lists**
     - [x] **Chatrooms**
     
      *Technical Features*
    ------------------------------------
      - [x] **Object Data Streams** - with sockets and serializable objects. Sends information back and forth between client/server 20x/sec, making for instant message delivery.

      - [x] **User Authentication** - on account creation, user's password is salted and then the salted password is hashed. The salted hash is what's sent over the socket connection and stored on the server side, so the plaintext password is never sent over the net. For login, the salt is sent back to the client, the client computes the hash and sends it over, and the server checks the salted hash against the stored salted hash. If they match, authentication is successful and the user can log in.

      - [x] **Database Management** - user information is indexed via unique primary keys, and these keys are linked to other tables as foreign keys to ensure data integrity.

     - [x] **Maven Dependency Management** - server-side of the program uses maven to integrate JUnit and the H2 Embedded Server, so the relied-upon API's are always up to date without hassle for the software developer.
     
     ---------------------------------
