#include <opencv\cv.h>
#include <opencv\highgui.h>
#include "DefinedObjects.h"

class ImageManip
{
public:
	static void findCircles(cv::Point2f* points, cv::Mat image, int numCircles, int minContourLength, int minCircleSize, float minRadialDifference);
	static cv::Mat extractDoubleCircleData(circleData* cdat, cv::Mat image, int minContourLength, int minCircleSize, float minRadialDifference, cv::Mat toReturn);
	static void orderCorners(cv::Point2f* corners);
};