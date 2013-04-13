#include <opencv\cv.h>

#define NUM_PROJECTORS (1)

//Table X and Y length on image plane
#define TABLE_X (480)
#define TABLE_Y (480)

#define DEFAULT_DISTANCE 15.75

//Error in pixels that corner of table can be off image plane
#define X_INTERSECT_ERROR_MARGIN (200)
#define Y_INTERSECT_ERROR_MARGIN (200)



//Width of table in inches
#ifndef TABLE_WIDTH
#define TABLE_WIDTH (19.5)
#endif 

//Height of table in inches
#ifndef TABLE_HEIGHT
#define TABLE_HEIGHT (19.5)
#endif 



#ifndef CAMERA_PERSPECTIVE
#define CAMERA_PERSPECTIVE
typedef struct{
	float x;
	float y;
	cv::Point2f tCorners[4];
	cv::Point2f points[8];
	cv::Point2f center;
	cv::Mat background;
	cv::Mat pic;
	float angle;
	cv::Mat perspectiveWarp;
} CameraPerspective;
#endif

#ifndef CIRCLE_DATA
#define CIRCLE_DATA
typedef struct{
	float c1x;
	float c1y;
	float c2x;
	float c2y;
	float c1w;
	float c1h;
	float c2w;
	float c2h;
	float c1r;
	float c2r;
}circleData;
#endif

#ifndef PRODAT
#define PRODAT
typedef struct{
	float eyeX, eyeY, eyeZ;
	float centerX, centerY, centerZ;
	float upX, upY, upZ;
	
}ProDat;
#endif