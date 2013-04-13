#include "DefinedObjects.h"

using namespace std;

class Mapper
{
public:
	Mapper();

	ProDat proDat[NUM_PROJECTORS];
	circleData* cDat;


	float xToInches(float pixels);
	float yToInches(float pixels);
	float xToPixels(float inches);
	float yToPixels(float inches);
	float toRads(float degrees);
	float toDegrees(float radians);

	void loadProjectorData(int projNum);
	void upFromTwist(float centerX, float centerY, float eyeX, float eyeY, float eyeZ, float twist, float *up);
	char* buildString(int projNum);
};