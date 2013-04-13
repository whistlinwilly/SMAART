#include "Initialization.h"
#include <opencv\cv.h>
#include <opencv2\opencv.hpp>
#include <opencv\highgui.h>
#include <algorithm>

using namespace cv;
using namespace std;


Initialization::Initialization(){
	mapper = new Mapper();
	projectorsConnected = 0;
	recvbuf = (char*)malloc(MAX_REC_BUF*sizeof(char));
}

void Initialization::connectToProjectors(ServerNetwork* sn){

	//connects to all projectors
	while (projectorsConnected < NUM_PROJECTORS){
		sn->acceptNewClient(projectorsConnected);
		projectorsConnected++;
	}

	//sends init number to projectors to test connection
	for (int i=0; i<NUM_PROJECTORS; i++){
		sn->sendToAll("0",5,i);
		sn->receiveData(i,recvbuf);
	}
}

CameraPerspective Initialization::camRotation(Camera tCam){
	int keyPressed;

	while (1){
		curWindow = "camRotation";
		namedWindow(curWindow, CV_WINDOW_AUTOSIZE);
		CameraPerspective test = tCam.tryRotation();
		imshow(curWindow, test.pic);
		keyPressed = waitKey();
		if (keyPressed == 121){
			test = tCam.findCorners();
			cp = test;
			break;
		}
		else if (keyPressed == 110)
			continue;
		else
			exit(0);
	}
	return cp;
}

void Initialization::bgSubtraction(BackgroundSubtractorMOG2* BGsub, Camera tCam){
	bgFrame = tCam.grabFrameWithPerspective(cp);
	imshow("FRAME GRABBED", bgFrame);
	BGsub->operator()(bgFrame, bgForeground, 0.001);
}

void Initialization::readInitPattern(int projNum, BackgroundSubtractorMOG2* BGsub, Camera tCam, ServerNetwork *sn){
	Mat bThresh, bCan;
	Mat twoCircles;
	

	sn->sendToAll("1",5,projNum);
	sn->receiveData(projNum,recvbuf);

	waitKey(100);
	bgSubtraction(BGsub, tCam);

	imshow("BACKGROUND FRAME", bgFrame);
	imshow("BACKGROUND MODEL", bgForeground);

	threshold(bgForeground,bThresh, 245.0, 255.0, THRESH_BINARY);
	imshow("BACKGROUND THRESHOLD", bThresh);

	Canny(bThresh, bCan, 100, 250, 5);
	imshow("BACKGROUND CANNY", bCan);

	twoCircles = ImageManip::extractDoubleCircleData(mapper->cDat, bCan, 20, 100, 15, bgFrame);

	imshow("CIRCLES", twoCircles);
	waitKey(100);
}

void Initialization::computePerspective(int projNum, ServerNetwork* sn){
	mapper->loadProjectorData(projNum);
} 

void Initialization::sendPerspectives(ServerNetwork* sn){
	char* coordString;
	for (int i=0; i<NUM_PROJECTORS; i++){
		
		//build the perspective string
		coordString = mapper->buildString(i);

		sn->sendToAll(coordString, strlen(coordString),i);
		sn->receiveData(i,recvbuf);
		waitKey(1000);

		//send the command to render the mapping
		sn->sendToAll("3",5,i);
		sn->receiveData(i,recvbuf);
		waitKey(1000);

	}

}