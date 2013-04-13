// Table.cpp : Defines the entry point for the console application.
//

#include "Initialization.h"
#include "Table.h"


Table::Table(char* ip, int port, int camNum){
	sn = new ServerNetwork(ip, port);
	tCam.initFastCam(camNum);
	recvbuf = (char*)malloc(MAX_REC_BUF*sizeof(char));
	tableBGsub = new BackgroundSubtractorMOG2(0, 400, true);
	projectorsConnected = 0;
}

void Table::initialize(){
	Initialization* tableInit = new Initialization();

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
		sn->sendToAll("0", 5, i);
		sn->receiveData(i, recvbuf);
	}

	//send every projector their perspectives
	tableInit->sendPerspectives(sn);

	sn->sendToAll("4",5,0);
	sn->receiveData(0, recvbuf);
	sn->sendToAll("4",5,1);
	sn->receiveData(1,recvbuf);
}

void Table::run(){
}



