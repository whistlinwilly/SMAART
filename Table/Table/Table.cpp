// Table.cpp : Defines the entry point for the console application.
//

#include "Initialization.h"
#include "Table.h"
#include "ColorChanging.h"
#include "HouseDemo.h"

Table::Table(char* ip, int port, int camNum){
	sn = new ServerNetwork(ip, port);
	tCam.initFastCam(camNum);
	recvbuf = (char*)malloc(MAX_REC_BUF*sizeof(char));
	tableBGsub = new BackgroundSubtractorMOG2(0, 400, true);
	projectorsConnected = 0;
}

void Table::initialize(){
	tableInit = new Initialization();

	//connect all the projectors
	tableInit->connectToProjectors(sn);

	//make camera shit perfect and receive a cameraperspective
	cp = tableInit->camRotation(tCam);

	//set background subtractor
	frame = tCam.grabFrameWithPerspective(cp);
	tableBGsub->operator()(frame, foreground, 0.001);

	//read init pattern for each projector
	for (int i=0; i<NUM_PROJECTORS; i++){
		tableInit->readInitPattern(i, tableBGsub, tCam, sn);
		tableInit->computePerspective(i, sn);

		sn->sendToClient("0,", 2, i);
		sn->receiveData(i, recvbuf);
	}

	//send every projector their perspectives
	tableInit->sendPerspectives(sn);
}

void Table::run(){

	//declare all demos
	HouseDemo* hd;
	ColorChanging* cc;
	char keyPressed;
	keyPressed = waitKey();

	switch(keyPressed){
		case 'c':
			sn->sendToAllReceive("3,colorChange", 13);
			cc = new ColorChanging(tCam, cp, sn);
			break;
		case 'h':
			sn->sendToAllReceive("3,houseDemo00", 13);
			hd = new HouseDemo(sn);
			break;
		case 'a':
			sn->sendToAllReceive("3,alexDemo000", 13);
			break;
		case 's':
			sn->sendToAllReceive("3,spaceDemo00", 13);
			break;
		case 'i':
			sn->sendToAllReceive("3,INIT", 6);
			//read init pattern for each projector
			for (int i=0; i<NUM_PROJECTORS; i++){
				tableInit->readInitPattern(i, tableBGsub, tCam, sn);
				tableInit->computePerspective(i, sn);
				sn->sendToClient("0,", 2, i);
				sn->receiveData(i, recvbuf);
			}
			tableInit->sendPerspectives(sn);
			break;

	}
}



