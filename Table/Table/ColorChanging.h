#include <opencv2\opencv.hpp>
#include <list>
#include <malloc.h>
#include "Camera.h"
#include "ServerNetwork.h"
#include "DefinedObjects.h"

#ifndef _COLORCHANGING_H_
#define _COLORCHANGING_H_
using namespace cv;

class ColorChanging{

public:

	ServerNetwork* appSn;
	BackgroundSubtractorMOG2* appBGsub;
	Mat appForeground, appFrame;
	Camera appCam;
	CameraPerspective appCp;
	char* recvbuf;
	std::list<int> blockedTriangles;
	int running;
	int* curTrianglePos;
	int curTriangle;
	int curSquare;
	int* getCurTriangleCenter(void);
	char* buildSendString(void);
	ColorChanging(Camera tCam, CameraPerspective tCp, ServerNetwork* tSn);
	void run(void);
	
};
#endif