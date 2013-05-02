#include <opencv2\opencv.hpp>


using namespace cv;

#ifndef _TABLE_H_
#define _TABLE_H
class Table
{
public:
	Initialization* tableInit;
	Table(char* ip, int port, int camNum);
	ServerNetwork *sn;
	Camera tCam;
	BackgroundSubtractorMOG2 *tableBGsub;
	unsigned int projectorsConnected;
	char* recvbuf;

	char* curWindow;
	CameraPerspective cp;

	Mat foreground;
	Mat frame;

	void initialize();
	void run();

};

#endif