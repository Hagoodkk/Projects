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

pid_t pid = NULL;

void signal_handler(int no) {
	// Catch kill signal
	if (no == SIGINT) {
		// Close file if not done so already
		if (file != NULL) {
			fclose(file);
			file = NULL;
		}
		// Boolean keeps track of connection status
		if (connected) {
			// Alert the client of server termination, and exit
			if (write(client, "cmsc257SIGINT\0", 14) < 0) {
				printf("Could not alert client to forced closure.\n");
			} else printf("Alerted client to forced closure\n");
			// Remove any write blocks on client side
			read(client, buffer, 50);
			close(client);
			printf("Shutting down server.\n");
		}
		printf("Child with pid %i and parent %i shutting down.\n", getpid(), getppid());
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

	// Begin connection setup
	uint32_t value, inet_len;
	struct sockaddr_in saddr, caddr;

	char* line;
	

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
	// End connection setup. Should now be listening
	while (1) {
		inet_len = sizeof(caddr);
		
		if ((client = accept(server, (struct sockaddr *)&caddr, &inet_len)) == -1) {
			printf("Error on client accept.\nExiting program.\n");
			close(server);
			return -1;
		}
		// If a new connection is made, branch to a child process
		pid = fork();
		if (pid == 0) {
			printf("Server new client connection [%s\%d].\n", inet_ntoa(caddr.sin_addr), caddr.sin_port);
			printf("I am child with pid %i and parent %i.\n", getpid(), getppid());
			connected = 1;
	
			// Try to read in what file the client is requesting
			if (read(client, buffer, 50) < 0) {
				printf("Error writing network data.\nExiting program.\n");
				close(server);
				return -1;
			}
			// Alerts the server to client termination
			if (strstr(buffer, "cmsc257") != NULL) {
				connected = 0;
				printf("Client aborted connection. Terminating upload.\n");
			}
			
			if (connected && debug) printf("Received a message: [%s]\n", buffer);
		
			// Check to see if requested file is present
			if (connected) file = fopen(buffer, "r");
			// If the file isn't present, alert the client
			if (file == NULL && connected) {
				if (write(client, "File not found on server.\0", 26) < 0) {
					printf("Error writing network data.\nExiting program.\n");
					return -1;
				}
				if (debug) printf("Sent a message: [%s]\n", "File not found on server.");
			} else if (connected && file != NULL) {
				// Otherwise, alert the client that the file is present
				if (write(client, "File found.\0", 12) < 0) {
					printf("Error writing network data.\nExiting program.\n");
					return -1;
				}
				if (debug) printf("Sent a message: [%s]\n", "File found.");
				
				// Iterate through the file and send chunks of data
				while (!feof(file) && connected) {
					// Acknowledge the client's response
					read(client, buffer, 50);
					// Alerts the server of client termination
					if (strstr(buffer, "cmsc257") != NULL) {
						connected = 0;
						printf("Client aborted connection. Terminating upload.\n");
					}
					// Send chunks of data to client
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
			// Cleanup and exit
			if (file != NULL) {
				fclose(file);
				file = NULL;
			}	
			close(client);
			printf("File transfer complete.\n");
			connected = 0;
			printf("Child with pid %i and parent %i now exiting.\n", getpid(), getppid());
			exit(0);
		}
	}
	return 0;
}
