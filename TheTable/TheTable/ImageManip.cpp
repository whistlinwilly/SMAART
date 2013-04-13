#include "ImageManip.h"
#include <opencv\cv.h>
#include <opencv\highgui.h>

using namespace cv;

void ImageManip::findCircles(Point2f* points, Mat image, int numCircles, int minContourLength, int minCircleSize, float minRadialDifference){
	int avg[] = {1,1,1,1};
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	RotatedRect myRect;
	int cirNum = 0;
	bool found;

	findContours( image, contour, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0) );

	for(int i=0; i<contour.size(); i++){
		found = false;
		if(contour[i].size() > minContourLength){
			myRect = fitEllipse(contour[i]);
				if(myRect.size.area() > minCircleSize){
					if(cirNum == 0){
						points[cirNum].x = myRect.center.x;
						points[cirNum].y = myRect.center.y;
						cirNum++;
					}
					else{
						for(int j = 0; j < cirNum; j++){
							if(sqrt(pow(myRect.center.x - points[j].x, 2) + pow(myRect.center.y - points[j].y,2)) < minRadialDifference){
								points[j].x = (points[j].x * avg[j] + myRect.center.x) / j+1;
								points[j].y = (points[j].y * avg[j] + myRect.center.y) / j+1;
								avg[j]++;
								found = true;
								break;
							}
						}
						if(!found && cirNum < numCircles){
							points[cirNum].x = myRect.center.x;
							points[cirNum].y = myRect.center.y;
							cirNum++;
						}
					}
				}
		}
	}
	return;
}

Mat ImageManip::extractDoubleCircleData(circleData* cdat, Mat image, int minContourLength, int minCircleSize, float minRadialDifference, Mat toReturn){
	int avg[] = {1,1,1,1};
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	RotatedRect myRect;
	int cirNum = 0;
	bool found;

	findContours( image, contour, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0) );

	for(int i=0; i<contour.size(); i++){
		found = false;
		if(contour[i].size() > minContourLength){
			myRect = fitEllipse(contour[i]);
				if(myRect.size.area() > minCircleSize){
					if(cirNum == 0){
						
						cdat->c1x = myRect.center.x;
						cdat->c1y = myRect.center.y;
						cdat->c1h = myRect.size.height;
						cdat->c1w = myRect.size.width;
						cdat->c1r = myRect.angle;
						ellipse(toReturn, myRect.center, cv::Size(myRect.size.width/2, myRect.size.height/2), myRect.angle, 0.0, 360.0, Scalar(0.0,0.0,255.0),2.0);
						cirNum++;
					}
					else{
						for(int j = 0; j < cirNum; j++){
							if(sqrt(pow(myRect.center.x - cdat->c1x, 2) + pow(myRect.center.y - cdat->c1y,2)) < minRadialDifference){
								found = true;
								break;
							}
						}
						if(!found && cirNum < 2){ //2 is number of circles to find
							cdat->c2x = myRect.center.x;
							cdat->c2y = myRect.center.y;
							cdat->c2h = myRect.size.height;
							cdat->c2w = myRect.size.width;
							cdat->c2r = myRect.angle;
							ellipse(toReturn, myRect.center, cv::Size(myRect.size.width/2, myRect.size.height/2), myRect.angle, 0.0, 360.0, Scalar(0.0,0.0,255.0),2.0);
							cirNum++;
						}
					}
				}
		}
	}
	return toReturn.clone();
}

void ImageManip::orderCorners(Point2f* corners){
	int min = 10000;
	int max = 0;
	int minI = 0;
	int maxI = 0;

	for(int i = 0; i < 4; i++){
		if(corners[i].x + corners[i].y < min){
			min = corners[i].x + corners[i].y;
			minI = i;
		}
	}

	Point2f temp = corners[0];
	corners[0] = corners[minI];
	corners[minI] = temp;

	for(int i = 0; i < 4; i++){
		if(corners[i].x + corners[i].y > max){
			max = corners[i].x + corners[i].y;
			maxI = i;
		}
	}

	temp = corners[2];
	corners[2] = corners[maxI];
	corners[maxI] = temp;

	if(corners[1].y < corners[1].x){
		temp = corners[1];
		corners[1] = corners[3];
		corners[3] = temp;
	}
};