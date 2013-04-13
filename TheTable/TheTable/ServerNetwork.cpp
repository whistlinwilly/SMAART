#include "ServerNetwork.h"

using namespace std; 
#pragma comment (lib, "Ws2_32.lib")


ServerNetwork::ServerNetwork(char* ip, int port)
{

    // create WSADATA object
    WSADATA wsaData;
	typedef unsigned long IPNumber;    // IP number typedef for IPv4
	const int SERVER_PORT = port;

    // our sockets for the server
    ListenSocket = INVALID_SOCKET;
    ClientSocket = INVALID_SOCKET;

	HOSTENT* hostent;
	SOCKADDR_IN sockAddr = {0};
    
    

    // Initialize Winsock
    iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
    if (iResult != 0) {
       // printf("WSAStartup failed with error: %d\n", iResult);
        exit(1);
    }

    // Create a SOCKET for connecting to server
    ListenSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (ListenSocket == INVALID_SOCKET) {
      //  printf("socket failed with error: %ld\n", WSAGetLastError());
        WSACleanup();
        exit(1);
    }

	// Get IP Address of website by the domain name, we do this by contacting(??) the Domain Name Server
    if ((hostent = gethostbyname(ip)) == NULL)  // "localhost"  www.google.com
    {
      //  printf("Failed to resolve website name to an ip address\n");
        WSACleanup();
        exit(1);
    }
	//printf("HostName: %s\n", hostent->h_name);
    sockAddr.sin_port             = htons(SERVER_PORT);
    sockAddr.sin_family           = AF_INET;
    sockAddr.sin_addr.S_un.S_addr = (*reinterpret_cast <IPNumber*> (hostent->h_addr_list[0]));

    if (iResult == SOCKET_ERROR) {
      //  printf("ioctlsocket failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
        exit(1);
    }

    // Setup the TCP listening socket
    iResult = bind( ListenSocket, (SOCKADDR*)(&sockAddr), sizeof(sockAddr));
	//printf("Listening Socket Bound to: addr: %s, port: %d\n", hostent->h_name, ntohs(sockAddr.sin_port));
	
    
	if (iResult == SOCKET_ERROR) {
      //  printf("bind failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
      //  exit(1);
    }


    // start listening for new clients attempting to connect
    iResult = listen(ListenSocket, SOMAXCONN);

    if (iResult == SOCKET_ERROR) {
      //  printf("listen failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
      //  exit(1);
    }
}

// accept new connections
bool ServerNetwork::acceptNewClient(unsigned int & id)
{
    // if client waiting, accept the connection and save the socket
    ClientSocket = accept(ListenSocket,NULL,NULL);
    if (ClientSocket != INVALID_SOCKET) 
    {
        //disable nagle on the client's socket
        char value = 1;
        setsockopt( ClientSocket, IPPROTO_TCP, TCP_NODELAY, &value, sizeof( value ) );

        // insert new client into session id table
        sessions.insert( pair<unsigned int, SOCKET>(id, ClientSocket) );

	//	printf("Connecting With New Client...\n");
        return true;
    }

	
    return false;
}

// receive incoming data
int ServerNetwork::receiveData(unsigned int client_id, char * recvbuf)
{
    if( sessions.find(client_id) != sessions.end() )
    {
        SOCKET currentSocket = sessions[client_id];
        iResult = NetworkServices::receiveMessage(currentSocket, recvbuf, MAX_PACKET_SIZE);
        if (iResult == 0)
        {
        //    printf("Connection closed\n");
            closesocket(currentSocket);
        }
		parseReceived(recvbuf);
        return iResult;
    }
    return 0;
} 

void ServerNetwork::parseReceived(char *string){
	int i = 0;
	char buffer[MAX_REC_BUF];
	while (((string[i] > 64 && string [i] < 91) || (string[i] > 96 && string[i] < 123)) && (i < MAX_REC_BUF)){
		buffer[i] = string[i];
		i++;
	}

	//cut off excess in string and add null terminator
	if (i < MAX_REC_BUF) string[i] = '\0';
	else printf("Receive Buffer is too small for incoming data!");
}

// send data to all clients
void ServerNetwork::sendToAll(char * packets, int totalSize, int client_id)
{
    SOCKET currentSocket;
    std::map<unsigned int, SOCKET>::iterator iter;
    int iSendResult;

	if (sessions.find(client_id) != sessions.end()){
        currentSocket = sessions[client_id];
        iSendResult = NetworkServices::sendMessage(currentSocket, packets, totalSize);
		packets = NULL;
        if (iSendResult == SOCKET_ERROR) 
        {
          //  printf("send failed with error: %d\n", WSAGetLastError());
            closesocket(currentSocket);
        }

    }
}