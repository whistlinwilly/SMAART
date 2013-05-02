#include "ServerNetwork.h"
#include "DefinedObjects.h"

#ifndef _HOUSEDEMO_H_
#define _HOUSEDEMO_H_

class HouseDemo{

	public:
		ServerNetwork* appSn;
		int running;
	
		HouseDemo(ServerNetwork* tSn);
		void run(void);
	
};
#endif