#include <opencv2\opencv.hpp>
#include <list>
#include <malloc.h>
#include "Camera.h"
#include "ServerNetwork.h"
#include "DefinedObjects.h"

#ifndef _ALEXDEMO_H_
#define _ALEXDEMO_H_
using namespace cv;

class AlexDemo{

public:

	ServerNetwork* appSn;
	char* recvbuf;
	int running;
	int keyPressed;
	AlexDemo(ServerNetwork* tSn);
	void run(void);
	
};
#endif