#include <WinSock2.h>

#ifndef _NS_H_
#define _NS_H_
class NetworkServices
{

public:

static int sendMessage(SOCKET curSocket, char * message, int messageSize);
static int receiveMessage(SOCKET curSocket, char * buffer, int bufSize);

};

#endif