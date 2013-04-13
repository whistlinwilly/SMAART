#include "Camera.h"
#include <opencv\cv.h>
#include <opencv\highgui.h>

CameraPerspective myObj;

using namespace std;
using namespace cv;



Camera::Camera(){
}

bool intersection(Point2f o1, Point2f p1, Point2f o2, Point2f p2,
                      Point2f* r)
{
    Point2f x = o2 - o1;
    Point2f d1 = p1 - o1;
    Point2f d2 = p2 - o2;

    float cross = d1.x*d2.y - d1.y*d2.x;
    if (abs(cross) < /*EPS*/1e-3)
        return false;

    double t1 = (x.x * d2.y - x.y * d2.x)/cross;
    *r = (Point2f) (o1 + d1 * t1);
    return true;
}

Mat Camera::init(void){

	VideoCapture cap;
	Mat cam, grey, gat, ht, dht, out, gauss, can, newMat;
	int thresh = 1;
	Point2f corners[4];
	Point2f cornerSquare[4] = {Point2f(0,0),Point2f(0,480),Point2f(640,480),Point2f(640,0)};
	int found = 0;

	try{
	cap.open(0);
	}
	catch(Exception e){
		return Mat();
	}

	cap>>cam;
	ht = cam;

	imwrite("Orig0.jpg", cam);
//	waitKey();

	cvtColor( cam, grey, CV_RGB2GRAY);

	imwrite("Orig1_grey.jpg", grey);
//	waitKey();

	GaussianBlur( grey, gauss, Size( 3, 3 ), 0, 0 );
	imwrite("Orig2_gauss.jpg", gauss);
//	waitKey();

	Canny(gauss, can, 10, 250, 3);
	imwrite("Orig3_can.jpg", can);
//	waitKey();

	vector<vector<Point> > contours;
	findContours(can, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	RotatedRect box = minAreaRect(contours[0]);

	Mat rot_mat = getRotationMatrix2D(box.center,box.angle,1);

	Mat rotated;

	warpAffine(cam, rotated, rot_mat, cam.size(), INTER_CUBIC);

  imwrite("Rot0.jpg", rotated);
//	waitKey();

	cvtColor( rotated, grey, CV_RGB2GRAY);

	imwrite("Rot1_grey.jpg", grey);
//	waitKey();

	GaussianBlur( grey, gauss, Size( 3, 3 ), 0, 0 );
	imwrite("Rot2_gauss.jpg", gauss);
//	waitKey();

	Canny(gauss, can, 10, 250, 3);
	imwrite("Rot3_can.jpg", can);
//	waitKey();

	vector<Vec2f> lines;
	HoughLines(can, lines, 1, CV_PI/180, 25, 0, 0 );

	float rho;
	float theta;
	double a, b;
	double x0, y0;
	Point2f pt1, pt2, pt3, pt4;
	Point2f pt5[4];

	for(size_t i = 0; i < 4; i++){
		
		rho = lines[i][0];
		theta = lines[i][1];
		a = cos(theta);
		b = sin(theta);
		x0 = a * rho;
		y0 = b * rho;
		pt1 = Point(x0 + 1000*(-b), y0 + 1000*(a));
		pt2 = Point(x0 - 1000*(-b), y0 - 1000*(a));

		for(size_t j = i + 1; j < 4; j++){
			rho = lines[j][0];
			theta = lines[j][1];
			a = cos(theta);
			b = sin(theta);
			x0 = a * rho;
			y0 = b * rho;
			pt3 = Point(x0 + 1000*(-b), y0 + 1000*(a));
			pt4 = Point(x0 - 1000*(-b), y0 - 1000*(a));

			if(intersection(pt1, pt2, pt3, pt4, &corners[found]) == true){
				found++;
				if(found >=3)
					break;
			}
			
		}
	}

	//the below is a super shitty method of positioning the corners in the array so
	//corners[0] is top left, corners[1] bottom left, corners[2] bottom right, corners[3] top right

	int min = 500;
	int max = -500;
	int minI = 0;
	int maxI = 0;

	for(int i = 0; i < 4; i++){
		if(corners[i].x + corners[i].y < min){
			min = corners[i].x + corners[i].y;
			minI = i;
		}
		if(corners[i].x + corners[i].y > max){
			max = corners[i].x + corners[i].y;
			maxI = i;
		}
	}

	Point2f temp = corners[0];
	corners[0] = corners[minI];
	corners[minI] = temp;

	temp = corners[2];
	corners[2] = corners[maxI];
	corners[maxI] = temp;

	if(corners[1].y < corners[1].x){
		temp = corners[1];
		corners[1] = corners[3];
		corners[3] = temp;
	}

	for(int i=0; i<4;i++){
	circle(rotated,corners[i],5,Scalar(255,0,0),3);
//	imshow("Test", rotated);
//	waitKey();
	}


	Mat homey, nullMat;

	homey = getPerspectiveTransform(corners,cornerSquare);

	warpPerspective(rotated,out,homey,rotated.size());

	imwrite("Rot4_final.jpg", out);


}

CameraPerspective Camera::tryRotation(void){

	CameraPerspective nullCP;
	Mat cam, grey, gat, ht, dht, out, gauss, can, newMat, thre;
	int thresh = 1;
	float i = 20.0;
	Point2f corners[4];
	Point2f cornerSquare[4] = {Point2f(0,0),Point2f(0,TABLE_Y),Point2f(TABLE_X,TABLE_Y),Point2f(TABLE_X,0)};
	int found = 0;

	cam = grabFrame();
	

	ht = cam;

	cvtColor( cam, grey, CV_RGB2GRAY);

	GaussianBlur( grey, gauss, Size( 3, 3 ), 0, 0 );
	namedWindow("new", CV_WINDOW_AUTOSIZE);

		imshow("new", gauss);
	waitKey();

	Canny(gauss, can, 20, 60, 3);

	imshow("new", can);
	waitKey();


	vector<vector<Point> > contours;
	findContours(can, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	if(contours.size() <1)
		exit(0);

	RotatedRect box = minAreaRect(contours[0]);

	if(box.angle < -10)
		box.angle = 90 + box.angle;

	Mat rot_mat = getRotationMatrix2D(box.center,box.angle,1);

	myObj.angle = box.angle;
	myObj.center = box.center;

	Mat rotated;

	warpAffine(cam, rotated, rot_mat, cam.size(), INTER_CUBIC);


	cvtColor( rotated, grey, CV_RGB2GRAY);

	GaussianBlur( grey, gauss, Size( 1, 1 ), 0, 0 );

	i = 10.0;

	threshold( gauss, thre, i, 255.0, THRESH_BINARY);
	//imshow("new", thre);
	//waitKey();
		imshow("new",thre);
	while(waitKey() != 121){
		i+=2.0;
		threshold( gauss, thre, i, 255.0, THRESH_BINARY);
		imshow("new",thre);
	};
	Canny(thre, can, 10, 250, 3);
		//imshow("new", can);
	//waitKey();
	vector<Vec2f> lines;
	HoughLines(can, lines, 1, CV_PI/180, 25, 0, 0 );

	float rho;
	float theta;
	double a, b;
	double x0, y0;
	Point2f pt1, pt2, pt3, pt4;
	Point2f pt5[4];

	for(size_t i = 0; i < 4; i++){
		
		rho = lines[i][0];
		theta = lines[i][1];
		a = cos(theta);
		b = sin(theta);
		x0 = a * rho;
		y0 = b * rho;
		pt1 = Point(x0 + 1000*(-b), y0 + 1000*(a));
		pt2 = Point(x0 - 1000*(-b), y0 - 1000*(a));
		myObj.points[2*i] = pt1;
		myObj.points[(2*i)+1] = pt2;
		line(rotated,pt1,pt2,Scalar(255,0,0),1);
	}
	myObj.pic = rotated;

	return myObj;
}

CameraPerspective Camera::findCorners(void){

	Point2f corners[4];
	int found = 0;
	Point2f cornerSquare[4] = {Point2f(0,0),Point2f(0,TABLE_Y),Point2f(TABLE_X,TABLE_Y),Point2f(TABLE_X,0)};

	for(int i = 0; i < 8; i +=2){
		for(int j = i + 2; j < 8; j+=2){
			if(intersection(myObj.points[i], myObj.points[i+1], myObj.points[j], myObj.points[j + 1], &(corners[found])) == true){
				if((corners[found].x > TABLE_X + X_INTERSECT_ERROR_MARGIN) || (corners[found].x < -X_INTERSECT_ERROR_MARGIN) || (corners[found].y > TABLE_Y + Y_INTERSECT_ERROR_MARGIN) || (corners[found].y < -Y_INTERSECT_ERROR_MARGIN))
					continue; 
				found++;
				if(found >= 4)
					break;
			}
		}
		if(found >= 4)
			break;
	}

	//the below is a super shitty method of positioning the corners in the array so
	//corners[0] is top left, corners[1] bottom left, corners[2] bottom right, corners[3] top right

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

	for(int i=0; i<4;i++){
		circle(myObj.pic,corners[i],5,Scalar(255,0,0),3);
	}


	Mat homey, nullMat;

	homey = getPerspectiveTransform(corners,cornerSquare);

	warpPerspective(myObj.pic,nullMat,homey,myObj.pic.size());

	Rect roi = Rect(0,0,TABLE_X,TABLE_Y);

	myObj.pic = nullMat(roi);
	myObj.perspectiveWarp = homey;
	myObj.tCorners[0] = corners[0];
	myObj.tCorners[1] = corners[1];
	myObj.tCorners[2] = corners[2];
	myObj.tCorners[3] = corners[3];

	imshow("camRotation", myObj.pic);
	//waitKey();
	return myObj;
}

Point2f Camera::findPoint(Mat bg, CameraPerspective cp){
	return Point2f(0.0,0.0);
}

int Camera::initFastCam(int camNumber){
	try{
		fastCam.open(camNumber);
	}
	catch(Exception e){
		exit(0);
	}
}

Mat Camera::grabFrame(){
	Mat frame;
	for(int i = 0; i < 5; i++)
	fastCam >> frame;
	return frame.clone();
}

Mat Camera::grabFrameWithPerspective(CameraPerspective cp){
	Mat m, rot, warp, mirror, roiMat;

	m = grabFrame();

	Mat rot_mat = getRotationMatrix2D(cp.center,cp.angle,1);
	warpAffine(m, rot, rot_mat, m.size(), INTER_CUBIC);
	warpPerspective(rot,warp,myObj.perspectiveWarp,rot.size());

	Rect roi = Rect(0,0,TABLE_X,TABLE_Y);

	roiMat = warp(roi);

	flip(roiMat, mirror, 1);

	

	return mirror.clone();
}

CameraPerspective Camera::getBackground(CameraPerspective cp){
	Mat m, rot, warp, mirror;

	m = grabFrame();

	Mat rot_mat = getRotationMatrix2D(cp.center,cp.angle,1);
	warpAffine(m, rot, rot_mat, m.size(), INTER_CUBIC);
	warpPerspective(rot,warp,myObj.perspectiveWarp,rot.size());

	flip(warp, mirror, 1);

	//IplImage* warpI = &IplImage(warp);
	//IplImage* mirrorI = &IplImage(warp);
	//cvConvertImage(warpI,mirrorI,CV_CVTIMG_FLIP);

	//imshow("FLIPPED", Mat(mirrorI));
	//waitKey();
	//mirror = Mat(mirrorI);

	cp.background = mirror.clone();
	return cp;
}


RotatedRect Camera::extractPoint(CameraPerspective cp){
	Mat bg, grey, thresh, can;
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	CameraPerspective test;
	RotatedRect myRect;
	float i = 50.0;
	test = getBackground(cp);
	cvtColor( test.background, grey, CV_RGB2GRAY );

	while(contour.size() < 500){
		i+=15.0;
		threshold( grey, thresh, i, 255.0, THRESH_BINARY);
	Canny(thresh,can,10, 250, 3);
	findContours( can, contour, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );
	if(contour.size() == 0)
		return RotatedRect(Point2f(0.0,0.0),Size2f(1.0,1.0),1.0);
	}

		while(contour.size() < 1000){
		i+=8.0;
		threshold( grey, thresh, i, 255.0, THRESH_BINARY);
	Canny(thresh,can,10, 250, 3);
	findContours( can, contour, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );
	if(contour.size() == 0)
		return RotatedRect(Point2f(0.0,0.0),Size2f(1.0,1.0),1.0);
	}


	while(contour.size() > 2){
		i+=3.0;
		threshold( grey, thresh, i, 255.0, THRESH_BINARY);
	Canny(thresh,can,10, 250, 3);
	findContours( can, contour, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );
	if(contour.size() == 0)
		return RotatedRect(Point2f(0.0,0.0),Size2f(1.0,1.0),1.0);
	};
	//	drawContours(test.background,contour,0,Scalar(255.0,0.0,0.0),0.5);
	//imshow("camRotation",test.background);
	//	waitKey();

		if(contour[0].size() < 10)
			return RotatedRect(Point2f(0.0,0.0),Size2f(1.0,1.0),1.0);

	myRect = fitEllipse(Mat(contour[0]));
	if(myRect.size.width * myRect.size.height < 15.0)
		return RotatedRect(Point2f(0.0,0.0),Size2f(1.0,1.0),1.0);

		return myRect;
 		
	
}

Point3f Camera::findCircle(CameraPerspective cp){

	float coX = 7.0;
	float coY = 7.0;
	CameraPerspective test;
	Mat hls, thresh, can;
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	RotatedRect myRect;

	float centerX = 0;
	float rot = 0;
	float centerY = 0;
	float num = 0;

	test = getBackground(cp);

	cvtColor(test.background, hls, CV_RGB2HLS_FULL);

		IplImage rgb = hls;




	IplImage* h = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );
	IplImage* l = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );
	IplImage* s = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );

	cvSplit(&rgb,h,l,s,NULL);

	Mat hue = h;
	Mat luminance = l;
	Mat saturation = s;

//	imshow("capture", luminance);
//	waitKey();

	Rect roi = Rect(coX,coY,640 - 2 * coX, 480 - 2 * coY);
	Mat roiImg = luminance(roi);
//	imshow("capture", roiImg);
//	waitKey();

	threshold(roiImg, thresh, 40.0, 255.0,THRESH_BINARY_INV);
//	imshow("capture", thresh);
//	waitKey();

	Canny(thresh, can, 100, 250, 3);

//	imshow("capture", can);

//	waitKey();

	findContours( can, contour, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0) );

	for(int i=0; i<contour.size(); i++){
		if(contour[i].size() > 150){
			myRect = minAreaRect(contour[i]);
		if(myRect.size.area() > 2000.0){
			centerX += myRect.center.x + coX;
			centerY += myRect.center.y + coY;
			rot += myRect.angle;
			num++;
			drawContours(test.background,contour,i,Scalar(0.0,255.0,0.0),0.5,NULL,NULL,NULL,Point2f(coX,coY));
		}
		//	drawContours(test.background,contour,i,Scalar(0.0,255.0,0.0),0.5);
		}
	}



	imshow("capture", test.background);
	waitKey();

 	return Point3f(centerX / num , centerY / num, rot / num);

}

void Camera::extractPattern2(CameraPerspective cp){
	
	CameraPerspective test;
	Mat hls, thresh, can;
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	RotatedRect myRect;
	Point2f points[4];

	float centerX1 = 0;
	float centerY1 = 0;
	float centerX2 = 0;
	float centerY2 = 0;
	float num = 0;

	test = getBackground(cp);

	IplImage rgb = test.background;
	
	IplImage* r = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );
	IplImage* g = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );
	IplImage* b = cvCreateImage( cvGetSize(&rgb), rgb.depth,1 );

	cvSplit(&rgb,b,g,r,NULL);

	Mat red = r;
	Mat green = g;
	Mat blue = b;

	imshow("capture2", green);
	waitKey();
	//red thresh
//	threshold(green, thresh, 110.0, 255.0,THRESH_BINARY);
	threshold(green, thresh, 120.0, 255.0, THRESH_BINARY);
	imshow("capture2", thresh);
	waitKey();

	Canny(thresh, can, 100, 250, 3);

	imshow("capture2", can);

	waitKey();

	findContours( can, contour, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0) );

	for(int i=0; i<contour.size(); i++){
		if(contour[i].size() > 300){
			myRect = fitEllipse(contour[i]);
		if(myRect.size.area() > 900.0){
			if(num == 0){
				centerX1 += myRect.center.x;
				centerY1 += myRect.center.y;
				num++;
			}
			else{
				if(abs(myRect.center.x - centerX1) < 10.0){
					centerX1 = centerX1 * num + myRect.center.x;
					centerX1 /= ++num;
					centerY1 = centerY1 * num + myRect.center.y;
					centerY1 /= num;
				}
				else{
					centerX2 = centerX2 * num + myRect.center.x;
					centerX2 /= ++num;
					centerY2 = centerY2 * num + myRect.center.y;
					centerY2 /= num;
				}
			}
			drawContours(test.background,contour,i,Scalar(255.0,0.0,0.0),0.5);
		}
		//	drawContours(test.background,contour,i,Scalar(0.0,255.0,0.0),0.5);
		}
	}



	imshow("capture2", test.background);
	waitKey();

	points[0] = Point2f(centerX1, centerY1);
	points[1] = Point2f(centerX2, centerY2);

	imshow("capture2", blue);
	waitKey();

	threshold(blue, thresh, 156.0, 255.0,THRESH_BINARY);
	imshow("capture2", thresh);
	waitKey();

	Canny(thresh, can, 100, 250, 3);

	imshow("capture2", can);

	waitKey();

	centerX1 = 0;
	centerX2 = 0;
	centerY1 = 0;
	centerY2 = 0;
	num = 0;

	findContours( can, contour, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0) );

	for(int i=0; i<contour.size(); i++){
		if(contour[i].size() > 300){
			myRect = fitEllipse(contour[i]);
		if(myRect.size.area() > 900.0){
			if(num == 0){
				centerX1 += myRect.center.x;
				centerY1 += myRect.center.y;
				num++;
			}
			else{
				if(abs(myRect.center.x - centerX1) < 10.0){
					centerX1 = centerX1 * num + myRect.center.x;
					centerX1 /= ++num;
					centerY1 = centerY1 * num + myRect.center.y;
					centerY1 /= num;
				}
				else{
					centerX2 = centerX2 * num + myRect.center.x;
					centerX2 /= ++num;
					centerY2 = centerY2 * num + myRect.center.y;
					centerY2 /= num;
				}
			}
			drawContours(test.background,contour,i,Scalar(255.0,0.0,0.0),0.5);
		}
		//	drawContours(test.background,contour,i,Scalar(0.0,255.0,0.0),0.5);
		}
	}



	imshow("capture2", test.background);
	waitKey();
	return;
};

cv::Mat Camera::extractCircles(CameraPerspective cp){
	
	CameraPerspective test;
	Mat hls, thresh, can, grey, homey;
	vector<Vec4i> hierarchy;
	std::vector<std::vector<cv::Point> > contour;
	RotatedRect myRect;
	Point2f points[4];
	Point2f idealPts[4] = {Point2f(-8 + cp.x, 6 + cp.y),Point2f(-8 + cp.x, -6 + cp.y),Point2f(8 + cp.x, -6 + cp.y ),Point2f(8 + cp.x, 6 + cp.y)};

	//Point2f idealPts[4] = {Point2f((TABLE_WIDTH / 2.0 - X_DIST), (TABLE_HEIGHT / 2.0 - Y_DIST)),
	//						Point2f((TABLE_WIDTH / 2.0 - X_DIST), (TABLE_HEIGHT / 2.0 + Y_DIST)),
	//						Point2f((TABLE_WIDTH / 2.0 + X_DIST), (TABLE_HEIGHT / 2.0 + Y_DIST)),
	//						Point2f((TABLE_WIDTH / 2.0 + X_DIST), (TABLE_HEIGHT / 2.0 - Y_DIST))};


	float centerX1 = 0;
	float centerY1 = 0;
	float centerX2 = 0;
	float centerY2 = 0;
	float num = 0;

	test = getBackground(cp);

	cvtColor( test.background, grey, CV_RGB2GRAY );
	imshow("capture2", grey);
	waitKey();

	threshold(grey, thresh, 210.0, 255.0, THRESH_BINARY);
	imshow("capture2", thresh);
	waitKey();

	Canny(thresh, can, 100, 250, 3);

	imshow("capture2", can);

	waitKey();

	ImageManip::findCircles(points, can, 4, 150, 2500, 100);

	ImageManip::orderCorners(points);

	circle(test.background, points[0], 1, Scalar(255.0,0.0,0.0), 2);
	circle(test.background, points[1], 1, Scalar(255.0,0.0,0.0), 2);
	circle(test.background, points[2], 1, Scalar(0.0,255.0,0.0), 2);
	circle(test.background, points[3], 1, Scalar(0.0,255.0,0.0), 2);

	imshow("CIRCLES", test.background);
	waitKey();

	homey = getPerspectiveTransform(points, idealPts);
	//warpPerspective(test.background, hls ,homey, test.background.size());


	//imshow("FIXED", hls);
	//waitKey();

	return homey.clone();

};

