#include "HouseDemo.h"
#include <opencv\highgui.h>
//#include <vector>
//#include <cmath>
//#include <algorithm>

using namespace cv;
using namespace std;

HouseDemo::HouseDemo(ServerNetwork* tSn){
	appSn = tSn;
	running = 1;
	HouseDemo::run();
}

void HouseDemo::run(){
	int keyPressed;
	running = 1;

	while(running){
		keyPressed = waitKey();
		// signal to "rise"
		if(keyPressed == 114){
			appSn->sendToAllReceive("3,5", 3);	
		}

		// signal to "storm"
		else if(keyPressed == 116){
			appSn->sendToAllReceive("3,6", 3);
		}

		//signal to stop
		else if (keyPressed == 115){
			running = 0;
		}
	}
}