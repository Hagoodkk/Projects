#include <stdlib.h>
#include <stdio.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <string.h>
#include <sys/select.h>
#include <signal.h>

FILE* file = NULL;
int socket_fd;
char buffer[50];
int connected = 0;
int debug = 0;

void signal_handler(int no) {
	// Catch kill signal
	if (no == SIGINT) {
		// Close file if not already done
		if (file != NULL) {
			fclose(file);
			file = NULL;
		}
		// Boolean keeps track of whether a connection is open
		// during termination
		if (connected) {
			// Write alerts the server that client is terminating
			write(socket_fd, "cmsc257\0", 8);		
			close(socket_fd);
		}	
		printf("Exiting program. User termination.\n");
		exit(0);
	}
	return;
}

void cleanup_handler(int no) {
	return;
}

int main(int argc, char** argv) {
	uint32_t value;
	struct sockaddr_in caddr;
	char *ip = "127.0.0.1";

	// Setup signal handler
	struct sigaction new_action, old_action;
	new_action.sa_handler = signal_handler;
	new_action.sa_flags = SA_NODEFER | SA_ONSTACK;
	sigaction(SIGINT, &new_action, &old_action);
	signal(SIGHUP, signal_handler);
	signal(SIGTERM, cleanup_handler);

	// Look for filename and terminate if not present
	if (argc < 2) {
		printf("No filename present in arguments.\nExiting program.\n");
		return -1;
	}
	char* fileName = argv[1];

	// Check for filename over 50 characters
	if (strlen(fileName) > 50) {
		printf("Filename exceeds max length of 50 characters.\nExiting program.\n");
		return -1;
	}

	// Begin initial setup for connections
	caddr.sin_family = AF_INET;
	caddr.sin_port = htons(2500);


	if (inet_aton(ip, &caddr.sin_addr) == 0) {
		return -1;
	}

	socket_fd = socket(PF_INET, SOCK_STREAM, 0);
	if (socket_fd == -1) {
		printf("Error on socket creation.\nExiting program.\n");
		return -1;
	}

	if (connect(socket_fd, (const struct sockaddr *)&caddr, sizeof(struct sockaddr)) == -1) {
		printf("Error on socket connect.\nExiting program.\n");
		return -1;
	}
	// End initial setup for connection to server
	// Switch boolean to true if connection established
	connected = 1;	

	// Let the server know which file is needed
	if (write(socket_fd, fileName, strlen(fileName)+1) < 0) {
		printf("Error writing network data.\nExiting program.\n");
		return -1;
	}

	if (debug) printf("Sent message: [%s]\n", fileName);

	// Try to read server response
	if (read(socket_fd, buffer, 50) < 0) {
		printf("Error reading network data.\nExiting program.\n");
		return -1;
	}
	// This response alerts the client that the server has been killed
	if (strstr(buffer, "cmsc257SIGINT") != NULL) {
		printf("Server terminated. Aborting download.\n");
		close(socket_fd);
		exit(1);
	}

	if (debug) printf("Got message: [%s]\n", buffer);

	// If file isn't sound on the server, simply exit program
	if (strcmp(buffer, "File not found on server.\0") == 0) {
		printf("File not found on server.\nExiting program.\n");
		return -1;
	} else {
		// If it is found, create the file
		file = fopen(fileName, "w");
		fclose(file);
		file = NULL;
		
		// Then, acknowledge the server's response and read its next data transmission
		// to prime the buffer
		if (write(socket_fd, buffer, 50) < 0) {
			printf("Error writing network data.\nExiting program.\n");
			return -1;
		}
		if (read(socket_fd, buffer, 50) < 0) {
			printf("Error reading network data.\nExiting program.\n");
			return -1;
		}
		// Server terminated if this response is present
		if (strstr(buffer, "cmsc257SIGINT") != NULL) {
			printf("Server terminated. Aborting download.\n");
			close(socket_fd);
			exit(1);
		}
		// While the server hasn't indicated EOF
		while (strstr(buffer, "cmsc257") == NULL) {
			// Append what's in the buffer
			if (debug) printf("Got message: [%s]\n", buffer);
			file = fopen(fileName, "a");
			fputs(buffer, file); 
			if (debug) printf("Wrote line: [%s]\n", buffer);
			fclose(file);
			file = NULL;

			// Acknowledge the server's response, then prime the buffer
			if (write(socket_fd, buffer, 50) < 0) {
				printf("Error writing network data.\nExiting program.\n");
				return -1;
			}
			if (read(socket_fd, buffer, 50) < 0) {
				printf("Error reading network data.\nExiting program.\n");
				return -1;
			}
			// Server terminated if this response is present
			if (strstr(buffer, "cmsc257SIGINT") != NULL) {
				close(socket_fd);
				printf("Server terminated. Aborting download.\n");
				exit(1);
			}
		}
		// Alert the user that the transfer was successfully completed
		printf("File transfer complete.\n");
	}

	// Cleanup and exit
	close(socket_fd);
	connected = 0;
	return 0;
}













