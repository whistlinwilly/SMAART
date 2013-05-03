// Table.cpp : Defines the entry point for the console application.
//

#include "Initialization.h"
#include "Table.h"
#include "ColorChanging.h"


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

		for (int i=0; i<NUM_PROJECTORS; i++){
		tableInit->readInitPattern(i, tableBGsub, tCam, sn);
		tableInit->computePerspective(i, sn);
		sn->sendToAll("0,", 2, i);
		sn->receiveData(i, recvbuf);
	}

	//send every projector their perspectives
	tableInit->sendPerspectives(sn);

	sn->sendToAll("3,",2,0);
	sn->receiveData(0, recvbuf);
	sn->sendToAll("3,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("4,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("4,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("5,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("5,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("6,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("6,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("7,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("7,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("8,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("8,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("9,",2,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("9,",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("10,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("10,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("11,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("11,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("12,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("12,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("13,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("13,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("14,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("14,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("15,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("15,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("16,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("16,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("17,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("17,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("18,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("18,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("19,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("19,",3,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("20,",3,0);
	sn->receiveData(0, recvbuf);

	sn->sendToAll("20,",3,1);
	sn->receiveData(1,recvbuf);

	//CODE BELOW FOR STUCCO HOUSE
	/*sn->sendToAll("6",2,0);
	sn->receiveData(0, recvbuf);
	sn->sendToAll("6",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("7",2,0);
	sn->receiveData(0, recvbuf);
	sn->sendToAll("7",2,1);
	sn->receiveData(1,recvbuf);

	sn->sendToAll("5",2,0);
	sn->receiveData(0, recvbuf);
	sn->sendToAll("5",2,1);
	sn->receiveData(1,recvbuf);*/
	//END STUCCO HOUSE


}

void Table::run(){
	//ColorChanging* cc = new ColorChanging(tCam, cp, sn);
}



