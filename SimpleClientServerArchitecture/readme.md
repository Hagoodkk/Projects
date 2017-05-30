# Simple Client Server Architecture for Unix

*Simple client/server architecture for Unix which allows files to be downloaded from the server. Supports multitenancy and enables signal handlers for graceful download termination.*

1. To compile each segment, navigate to its direction and type "make".
2. Run the server using the command "./server"
3. Request a file from the server by running the client program with the requested file as a command line argument. As an example, "./client sample_file.c".
4. For successful file transfer, the requested file must be located in the same directory as the server.
