
#define DEFAULT_BUFLEN 512
#define DEFAULT_PORT "6881" 
#define MAX_PACKET_SIZE 10
#define MAX_REC_BUF (20)



 
#pragma comment (lib, "Ws2_32.lib")

#include "NetworkServices.h"
#include <WinSock2.h>
#include <map>
#include "DefinedObjects.h"

#ifndef _SN_H_
#define _SN_H_
class ServerNetwork
{

public:
	char* recvbuf;
    ServerNetwork(char* ip, int port);
    ~ServerNetwork();

    // Socket to listen for new connections
    SOCKET ListenSocket;

    // Socket to give to the clients
    SOCKET ClientSocket;

    // for error checking return values
    int iResult;

    // table to keep track of each client's socket
    std::map<unsigned int, SOCKET> sessions; 

	bool acceptNewClient(unsigned int & id);

	// send data to client
    void sendToClient(char * packets, int totalSize, int client_id);

	// send data to all clients & receive
    void sendToAllReceive(char* packets, int totalSize);

	// receive incoming data
    int receiveData(unsigned int client_id, char * recvbuf);

	//parse received data
	void parseReceived(char *string);
};

#endif