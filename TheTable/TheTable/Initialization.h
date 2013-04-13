#include <opencv\cv.h>
#include <opencv\highgui.h>
#include "ServerNetwork.h"
#include "Camera.h"
#include "Mapper.h"
#include <opencv2\opencv.hpp>

using namespace cv;

class Initialization
{
public:
	Initialization();
	Mapper *mapper;
	unsigned int projectorsConnected;
	char* curWindow;
	char* recvbuf;
	CameraPerspective cp;
	Mat bgFrame;
	Mat bgForeground;

	void connectToProjectors(ServerNetwork* sn);
	CameraPerspective camRotation(Camera tCam);
	void bgSubtraction(BackgroundSubtractorMOG2* BGsub, Camera tCam);
	void readInitPattern(int projNum, BackgroundSubtractorMOG2* BGsub, Camera tCam, ServerNetwork *sn);
	void computePerspective(int projNum, ServerNetwork* sn);
	void sendPerspectives(ServerNetwork* sn);
};

