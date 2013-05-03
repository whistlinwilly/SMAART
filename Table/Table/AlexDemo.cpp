#include "AlexDemo.h"
#include <vector>
#include <cmath>
#include <algorithm>


using namespace cv;
using namespace std;

AlexDemo::AlexDemo(ServerNetwork* tSn){
	appSn = tSn;
	running = 1;
	AlexDemo::run();
	
}

void AlexDemo::run(){
	running = 1;
	while (1){
		waitKey(100);
		appSn->sendToAllReceive("3,all", 5);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,none", 6);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,headhouse", 11);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,contours", 10);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,circulation", 13);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,single", 8);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,double", 8);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,triple", 8);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,housekeeping", 14);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
		appSn->sendToAllReceive("3,skylight", 10);
		keyPressed = waitKey();
		if (keyPressed == 115) break;
	}
}