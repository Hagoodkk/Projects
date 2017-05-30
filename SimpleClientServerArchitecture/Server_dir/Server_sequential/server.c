#include <stdio.h>
#include <signal.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <string.h>

FILE *file = NULL;
int server, client;
char buffer[50];
int connected = 0;
int debug = 0;

void signal_handler(int no) {
	// If server kill signal was input
	if (no == SIGINT) {
		if (file != NULL) {
			fclose(file);
			file = NULL;
		}
		// Boolean keeps track of connection status
		if (connected) {
			// Send this message to let the client know server
			// is terminating
			if (write(client, "cmsc257SIGINT\0", 14) < 0) {
				printf("Could not alert client to forced closure.\n");
			} else printf("Alerted client to forced closure\n");
			// Remove any write blocks on client side
			read(client, buffer, 50);
			// Shut it down
			close(client);
			printf("Shutting down server.\n");
		}
		exit(0);
	}
	return;
}

void cleanup_handler(int no) {
	return;
}

int main() {
	// Setup signal handler
	struct sigaction new_action, old_action;
	new_action.sa_handler = signal_handler;
	new_action.sa_flags = SA_NODEFER | SA_ONSTACK;
	sigaction(SIGINT, &new_action, &old_action);
	signal(SIGHUP, signal_handler);
	signal(SIGTERM, cleanup_handler);

	uint32_t value, inet_len;
	struct sockaddr_in saddr, caddr;

	char* line;
	
	// Begin connection setup
	saddr.sin_family = AF_INET;
	saddr.sin_port = htons(2500);
	saddr.sin_addr.s_addr = htonl(INADDR_ANY);

	server = socket(PF_INET, SOCK_STREAM, 0);
	if (server == -1) {
		printf("Error on socket creation.\nExiting program.\n");
		return -1;
	}	
	if (bind(server, (struct sockaddr *)&saddr, sizeof(struct sockaddr)) == -1) {
		printf("Error on socket bind.\nExiting program.\n");
		return -1;
	}
	if (listen(server, 1) == -1) {
		printf("Error on socket listen.\nExiting program.\n");
		return -1;
	}
	// End connection setup, now listening

	// Listen indefinitely until server killed
	while (1) {
		inet_len = sizeof(caddr);
		
		// Notice new connection	
		if ((client = accept(server, (struct sockaddr *)&caddr, &inet_len)) == -1) {
			printf("Error on client accept.\nExiting program.\n");
			close(server);
			return -1;
		}
		printf("Server new client connection [%s\%d].\n", inet_ntoa(caddr.sin_addr), caddr.sin_port);
		connected = 1;

		// Read in the filename requested
		if (read(client, buffer, 50) < 0) {
			printf("Error writing network data.\nExiting program.\n");
			close(server);
			return -1;
		}
		// This alerts the server that the client is shutting down
		// Connection flag lets server skip steps if client is disconnected
		if (strstr(buffer, "cmsc257") != NULL) {
			connected = 0;
			printf("Client aborted connection. Terminating upload.\n");
		}
		
		if (connected && debug) printf("Received a message: [%s]\n", buffer);
	
		// Check if the file requested is present
		if (connected) file = fopen(buffer, "r");
		// If file is not present, alert the client
		if (file == NULL && connected) {
			if (write(client, "File not found on server.\0", 26) < 0) {
				printf("Error writing network data.\nExiting program.\n");
				return -1;
			}
			if (debug) printf("Sent a message: [%s]\n", "File not found on server.");
		} else if (connected && file != NULL) {
			// Otherwise, alert the client the file was found
			if (write(client, "File found.\0", 12) < 0) {
				printf("Error writing network data.\nExiting program.\n");
				return -1;
			}
			if (debug) printf("Sent a message: [%s]\n", "File found.");
			
			// While not at the end of the file and client is still connected,
			// send chunks of data
			while (!feof(file) && connected) {
				// Clear up any write blocks on client side
				read(client, buffer, 50);
				// Alerts the server whether the client has terminated
				if (strstr(buffer, "cmsc257") != NULL) {
					connected = 0;
					printf("Client aborted connection. Terminating upload.\n");
				}
				// Take in 50 characters of the file at a time
				if (connected && fgets(buffer, 50, file) != NULL) {
					if (write(client, buffer, 50) < 0) {
						printf("Error writing network data.\nExiting program.\n");
						return -1;
					}		
					if (debug) printf("Sent a message: [%s]\n", buffer);
				}
			}
			// Let the client know the transfer is complete
			if (connected && write(client, "cmsc257\0", 8) < 0) {
				printf("Error writing network data.\nExiting program.\n");
				return -1;
			}
			if (connected && debug) printf("Sent a message: [%s]\n", "cmsc257"); 
		}
		// Cleanup and close connection
		if (file != NULL) {
			fclose(file);
			file = NULL;
		}	
		close(client);
		connected = 0;
		printf("File transfer complete.\n");
	}
	return 0;
}
