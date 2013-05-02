#include "ColorChanging.h"
#include <vector>
#include <cmath>
#include <algorithm>


using namespace cv;
using namespace std;

ColorChanging::ColorChanging(Camera tCam, CameraPerspective tCp, ServerNetwork* tSn){
	appSn = tSn;
	curTrianglePos = (int*)malloc(2*sizeof(int));
	appCam = tCam;
	appCp = tCp;
	running = 1;
	appBGsub = new BackgroundSubtractorMOG2(0, 200, false);
	appFrame = tCam.grabFrameWithPerspective(appCp);
	appBGsub->operator()(appFrame, appForeground, 0.01);
	appFrame = tCam.grabFrameWithPerspective(appCp);
	appBGsub->operator()(appFrame, appForeground, 0.01);
	curTriangle = 0;
	curSquare = 0;
	ColorChanging::run();
	
}

//This method scans the foreground image, finds where the pixels are black (covered by hand or object),
//then sends info to projectors to render cool shit on the blocked area.
void ColorChanging::run(){
	recvbuf = new char[100];
	std::list<int>::iterator it;
	char* stringToSend;
	int ret;
	while (running){ 
		curTriangle = 0;
		curSquare = 0;
		//set background subtractor
		appFrame = appCam.grabFrameWithPerspective(appCp);
		appBGsub->operator()(appFrame, appForeground, 0.001);
		Mat test = Mat(appForeground);
		namedWindow("Test", CV_WINDOW_AUTOSIZE);
		imshow("Test", appForeground);
		waitKey(10);

		while (curTriangle < NUM_TRIANGLES){
			curTrianglePos = getCurTriangleCenter();
			Point2d p = Point2d(curTrianglePos[0], curTrianglePos[1]);
			ret = appForeground.at<uchar>(p);
			if (ret > 250){
				blockedTriangles.push_back(curTriangle);
			}
  			curTriangle++;
			curSquare = curTriangle/2;
		}
		stringToSend = buildSendString();

	for (int i=0; i<NUM_PROJECTORS; i++){
			appSn->sendToAll(stringToSend, strlen(stringToSend),i);
			//appSn->receiveData(i, recvbuf);
		}
		blockedTriangles.erase(blockedTriangles.begin(), blockedTriangles.end());
	}
}	

int* ColorChanging::getCurTriangleCenter(){
	int squareWidth = TABLE_X/NUM_SQUARES;
	int squareHeight = TABLE_Y/NUM_SQUARES;

	int triPos[2];
	int anchorX = (curSquare % NUM_SQUARES)*squareWidth;
	int anchorY = (curSquare/NUM_SQUARES)*squareHeight;
	if (curTriangle == 0){
		triPos[0] = anchorX + squareWidth/4;
		triPos[1] = anchorY + 3*squareHeight/4;
	}
	else if ((curTriangle % 2) == 0){
		triPos[0] = anchorX + squareWidth/4;
		triPos[1] = anchorY + 3*squareWidth/4;
	}
	else if ((curTriangle % 2) == 1){
		triPos[0] = anchorX + 3*squareWidth/4;
		triPos[1] = anchorY + squareWidth/4;
	}
	return triPos;
}

char* ColorChanging::buildSendString(){
	char* ret;
	std::list<int>::iterator it;
	std::stringstream ss (std::stringstream::in | std::stringstream::out);
	std::string s;
	it = blockedTriangles.begin();
	s = "3";
	s.append(",");
	while (it != blockedTriangles.end()){
		ss << *it;
		s.append(ss.str());
		s.append(",");
		it++;
		ss.str(std::string());
	}
	ret = new char[s.length() + 1];
	strcpy(ret, s.c_str());
	return ret;
}