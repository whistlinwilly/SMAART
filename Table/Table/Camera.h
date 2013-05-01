#include "DefinedObjects.h"
#include "ImageManip.h"

#ifndef _CAMERA_H_
#define _CAMERA_H_

class Camera
{

public:
	Camera();
	cv::Mat init(void);
	CameraPerspective tryRotation(void);
	CameraPerspective findCorners(void);
	cv::Point2f findPoint(cv::Mat bg, CameraPerspective cp);
	CameraPerspective getBackground(CameraPerspective cp);
	cv::RotatedRect extractPoint(CameraPerspective cp);
	cv::Point3f findCircle(CameraPerspective cp);
	void extractPattern2(CameraPerspective cp);
	cv::Mat extractCircles(CameraPerspective cp);
	cv::VideoCapture fastCam;
	int initFastCam(int camNumber);
	cv::Mat grabFrame();
	cv::Mat grabFrameWithPerspective(CameraPerspective cp);
};

#endif