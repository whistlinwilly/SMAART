#include "Initialization.h"
#include "Table.h"


int main(int argc, char* argv[])
{
	//construct a table with (ip, port, cameraNumber);
	Table* table = new Table("192.168.1.4", 6881, 0);

	//run the initialization
	table->initialize();

	//run whatever we want to run
	table->run();
	return 0;
}