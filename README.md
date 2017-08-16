# Projects

*In this section, you will find a collection of personal and school projects for demonstration.*

**Table of Contents**
1. **JChattr (Personal Project)** - chat application with a multi-threaded server that accepts client connections through the uses of sockets. It uses data streams and serializable objects to pass back and forth information. JavaFX is used for UI development. IntelliJ IDE used for development and application runs on this platform. Note when testing that the "Add Buddy" button references the Server's database for the buddy being added, so only users that are registered with the app can be added to the buddy list. Simply create two accounts for testing purposes, then add each to each other's list.
*Technical Features*
a. Object data streams with sockets and serializable objects. Sends information back and forth between client/server 20x/sec, making for instant message delivery.
b. User authentication - on account creation, user's password is salted and then the salted password is hashed. The salted hash is what's sent over the socket connection and stored on the server side, so the plaintext password is never sent over the net. For login, the salt is sent back to the client, the client computes the hash and sends it over, and the server checks the salted hash against the stored salted hash. If they match, authentication is successful and the user can log in.
c. Database Management - user information is indexed via unique primary keys, and these keys are linked to other tables as foreign keys to ensure data integrity.
d. Maven Dependency Management - server-side of the program uses maven to integrate JUnit and the H2 Embedded Server, so the relied-upon API's are always up to date without hassle for the software developer.
2. **Hungr** - mobile development android app which recommends restaurants to its users. Uses geolocation services along with optional zip code, restaurant filtering is available, and users can retrieve contact information right from the app. Using the Yelp API.
3. **Cyber Security Password Management System** - system built on JavaFX and employs the H2 embedded database which allows users to securely store passwords.
4. **Custom Hash Table Data Lookup** - hash table built using a custom LinkedList implementation. Allows for the lookup of city coordinates and calculations of distance between cities using command line entries.
5. **Djikstra's Algorithm Implementation** - takes in a weighted graph as input with cities as nodes and calculates the minimum travel cost between two of them.
6. **Heavy Duty Graph Node Experiment** - takes in a graph as input and runs a game where some nodes are chosen at random and marked as "capital nodes". Nodes of greatest influence are then deleted incrementally, and the game ends when a capital node is either deleted or completely disconnected from the rest of the graph. Data for each iteration is output and can be used to analyze best practices to ensure the the non-deletion/non-disconnection of important nodes.
7. **OS Simulation** - simulates an operating system's flow of job execution using a round-robin algorithm.
8. **Simple Client Server Architecture** - sets up a client/server where the client is enabled to download files from the server's space. Multitenancy version available, and both versions come with signal handlers to gracefully terminate download interruption.

