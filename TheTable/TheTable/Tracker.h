#include <opencv\cv.h>

class Tracker
{
public:
	Tracker();
	void init();
	void runObjectTracking();
	cv::Mat classifyObject(std::vector<cv::Point> contour);
}