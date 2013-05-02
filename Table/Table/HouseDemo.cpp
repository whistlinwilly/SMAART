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
	char keyPressed;
	
	while(running){
		keyPressed = waitKey();
		// signal to "rise"
		if(keyPressed == 'r'){
			appSn->sendToAllReceive("3,5", 3);	
		}

		// signal to "set"
		if(keyPressed == 's'){
			appSn->sendToAllReceive("3,6", 3);
		}
	}
}