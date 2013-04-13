#include "Mapper.h"

#define _USE_MATH_DEFINES

#include <math.h>
#include <vector>
#include <cmath>
#include <algorithm>
#include <opencv\cv.h>
#include <opencv\highgui.h>
#include <opencv2\opencv.hpp>



Mapper::Mapper(){
	cDat = new circleData();

}
float Mapper::xToInches(float pixels){
	return ((pixels/TABLE_X)*TABLE_WIDTH);
}

float Mapper::yToInches(float pixels){
	return ((pixels/TABLE_Y)*TABLE_WIDTH);
}

float Mapper::xToPixels(float inches){
	return ((inches/TABLE_WIDTH)*TABLE_X);
}

float Mapper::yToPixels(float inches){
	return ((inches/TABLE_HEIGHT)*TABLE_Y);
}

float Mapper::toRads(float degrees){
	return ((degrees/360.0)*2*M_PI);
}

float Mapper::toDegrees(float radians){
	return ((radians/(2*M_PI))*360.0);
}

void Mapper::loadProjectorData(int projNum){
	float deltX;
	float deltY;
	float distFromCenter;
	float eyeDeltaX;
	float eyeDeltaY;
	float distanceFromTable;
	float incidentAngle;
	float twist;
	float rotation;
	float eyeX, eyeY, eyeZ;
	float centerX, centerY, centerZ;
	float up[3];
	float rotationAdd;


			//billys crap
	float correctionValue = 1.0f;
	float correctedCenter[2];
	float correctionVector[2];
	float correctionRatio;
	float modRot;
	rotationAdd = 0;

	if (cDat->c1w > cDat->c2w){
		if (cDat->c1x > cDat->c2x){
				rotationAdd = 180.00;
		}
		centerX = cDat->c1x;
		centerY = cDat->c1y;
		centerZ = 0;




		distanceFromTable = xToInches((cDat->c1w) / 2.5) * DEFAULT_DISTANCE;
		//subtract from 360 and subtract 90 to make rotation starting from +x
		rotation = 360.0 - (cDat->c1r + 90) + rotationAdd;
		if (rotation > 360.0) rotation -= 360.0;
		incidentAngle = 360.0*(asin(cDat->c1w/cDat->c1h)/(2*M_PI));

		modRot = (rotation + 180.00);
		if (modRot > 360.0) modRot -= 360.0;

		eyeX = (centerX - (TABLE_X / 2.0)) / TABLE_X * TABLE_WIDTH;
		eyeY = ((TABLE_Y / 2.0) - centerY) / TABLE_Y * TABLE_HEIGHT;
		eyeZ = distanceFromTable*(sin(toRads(incidentAngle)));
		deltX = cos(toRads(rotation));
		deltY = sin(toRads(rotation));
		distFromCenter = distanceFromTable * cos(toRads(incidentAngle));
		eyeDeltaX = distFromCenter * deltX;
		eyeDeltaY = distFromCenter * deltY;
		centerX = eyeX;
		centerY = eyeY;

		//focal shift correction
		correctionVector[0] = cos(toRads(modRot));
		correctionVector[1] = sin(toRads(modRot));
		correctionRatio = 1.0 - sin(toRads(incidentAngle));
		correctedCenter[0] = centerX - correctionVector[0] * correctionValue * correctionRatio;
		correctedCenter[1] = centerY - correctionVector[1] * correctionValue * correctionRatio;
		centerX = correctedCenter[0];
		centerY = correctedCenter[1];

		eyeX = eyeX + eyeDeltaX;
		eyeY = eyeY + eyeDeltaY;

		if (cDat->c1y > cDat->c2y){
			twist = 90 - toDegrees(atan((cDat->c2x - cDat->c1x)/(cDat->c1y - cDat->c2y)));
	
		}
		else {
			twist = 270 - toDegrees(atan((cDat->c2x - cDat->c1x)/(cDat->c1y - cDat->c2y)));
		}
		twist -= 180.00;
		twist -= rotation;
	
		
	}
	else {
		if (cDat->c2x > cDat->c1x){
				rotationAdd = 180.00;
		}
		centerX = cDat->c2x;
		centerY = cDat->c2y;
		centerZ = 0;
		distanceFromTable = xToInches((cDat->c2w) / 2.5) * DEFAULT_DISTANCE;
		//subtract from 360 and subtract 90 to make rotation starting from +x
		rotation = 360.0 - (cDat->c2r + 90.0) + rotationAdd;
		if (rotation > 360.0) rotation -= 360.0;

		modRot = rotation + 180.00;
		if (modRot > 360.0) modRot -= 360.0;

		incidentAngle = 360*(asin(cDat->c2w/cDat->c2h)/(2*M_PI));
		eyeX = (centerX - (TABLE_X / 2.0)) / TABLE_X * TABLE_WIDTH;
		eyeY = ((TABLE_Y / 2.0) - centerY) / TABLE_Y * TABLE_HEIGHT;
		eyeZ = distanceFromTable*(sin(toRads(incidentAngle)));
		deltX = cos(toRads(rotation));
		deltY = sin(toRads(rotation));
		distFromCenter = distanceFromTable * cos(toRads(incidentAngle));
		eyeDeltaX = distFromCenter * deltX;
		eyeDeltaY = distFromCenter * deltY;
		centerX = eyeX;
		centerY = eyeY;

		//focal shift correction
		correctionVector[0] = cos(toRads(modRot));
		correctionVector[1] = sin(toRads(modRot));
		correctionRatio = 1.0 - sin(toRads(incidentAngle));
		correctedCenter[0] = centerX - correctionVector[0] * correctionValue * correctionRatio;
		correctedCenter[1] = centerY - correctionVector[1] * correctionValue * correctionRatio;
		centerX = correctedCenter[0];
		centerY = correctedCenter[1];

		eyeX = eyeX + eyeDeltaX;
		eyeY = eyeY + eyeDeltaY;

		if (cDat->c2y > cDat->c1y){
			twist = 90 - toDegrees(atan((cDat->c1x - cDat->c2x)/(cDat->c2y - cDat->c1y)));
	
		}
		else {
			twist = 270 - toDegrees(atan((cDat->c1x - cDat->c2x)/(cDat->c2y - cDat->c1y)));
		}
		twist -= 180.00;
		twist -= rotation;
	}

	upFromTwist(centerX, centerY, eyeX, eyeY, eyeZ, twist, up);

	//load up proDat
	proDat[projNum].centerX = centerX;
	proDat[projNum].centerY = centerY;
	proDat[projNum].centerZ = centerZ;
	proDat[projNum].eyeX = eyeX;
	proDat[projNum].eyeY = eyeY;
	proDat[projNum].eyeZ = eyeZ;
	proDat[projNum].upX = up[0];
	proDat[projNum].upY = up[1];
	proDat[projNum].upZ = up[2];


}


void Mapper::upFromTwist(float centerX, float centerY, float eyeX, float eyeY, float eyeZ, float twist, float *up){
	/*
	* this part of the code calculates the "up" vector for the opengl camera position
	* first we find the vector from the camera to the focal point in the table plane
	* by crossing this vector with a (0,0,1) vector rotated about the first vector by twist degrees
	* we obtain a vector pointing out the right side, perpendicular to the look at vector
	* finally we cross this third right side vector with the original "look at" vector to obtain one
	* perpendicular to them both - that is the "up" vector we need
	*/

	float upX, upY, upZ;
	float vecX = centerX - eyeX;
	float vecY = centerY - eyeY;
	float vecZ = 0.0 - eyeZ;

	// normalized look at vector
	float newVecX = vecX / sqrt(pow(vecX, 2) + pow(vecY, 2) + pow(vecZ, 2));
	float newVecY = vecY / sqrt(pow(vecX, 2) + pow(vecY, 2) + pow(vecZ, 2));
	float newVecZ = vecZ / sqrt(pow(vecX, 2) + pow(vecY, 2) + pow(vecZ, 2));

	vecX = newVecX;
	vecY = newVecY;
	vecZ = newVecZ;

	// upwards z vector
	float zVecX = 0.0;
	float zVecY = 0.0;
	float zVecZ = 1.0;

	float cosTheta = (float)cos(toRads(-twist));
	float sinTheta = (float)sin(toRads(-twist));

	// x position of the new rotated vector
	float x   = (cosTheta + (1 - cosTheta) * vecX * vecX)		* zVecX;
	x  += ((1 - cosTheta) * vecX * vecY - vecZ * sinTheta)	* zVecY;
	x  += ((1 - cosTheta) * vecX * vecZ + vecY * sinTheta)	* zVecZ;

	// y position of the new rotated vector
	float y  = ((1 - cosTheta) * vecX * vecY + vecZ * sinTheta)	* zVecX;
	y += (cosTheta + (1 - cosTheta) * vecY * vecY)		* zVecY;
	y += ((1 - cosTheta) * vecY * vecZ - vecX * sinTheta)	* zVecZ;

	// z position of the new rotated vector
	float z  = ((1 - cosTheta) * vecX * vecZ - vecY * sinTheta)	* zVecX;
	z  += ((1 - cosTheta) * vecY * vecZ + vecX * sinTheta)	* zVecY;
	z  += (cosTheta + (1 - cosTheta) * vecZ * vecZ)		* zVecZ;

	newVecX = x / sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
	newVecY = y / sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
	newVecZ = z / sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));

	x = newVecX;
	y = newVecY;
	z = newVecZ;

	//lookat X zUp

	float rightX = vecY*z - vecZ*y;
	float rightY = vecZ*x - vecX*z;
	float rightZ = vecX*y - vecY*x;

	newVecX = rightX / sqrt(pow(rightX, 2) + pow(rightY, 2) + pow(rightZ, 2));
	newVecY = rightY / sqrt(pow(rightX, 2) + pow(rightY, 2) + pow(rightZ, 2));
	newVecZ = rightZ / sqrt(pow(rightX, 2) + pow(rightY, 2) + pow(rightZ, 2));

	rightX = newVecX;
	rightY = newVecY;
	rightZ = newVecZ;


	//right X lookat

	upX = rightY*vecZ - rightZ*vecY;
	upY = rightZ*vecX - rightX*vecZ;
	upZ = rightX*vecY - rightY*vecX;

	newVecX = upX / sqrt(pow(upX, 2) + pow(upY, 2) + pow(upZ, 2));
	newVecY = upY / sqrt(pow(upX, 2) + pow(upY, 2) + pow(upZ, 2));
	newVecZ = upZ / sqrt(pow(upX, 2) + pow(upY, 2) + pow(upZ, 2));

	up[0] = upX;
	up[1] = upY;
	up[2] = upZ;

}

char* Mapper::buildString(int projNum){
	stringstream ss (stringstream::in | stringstream::out);
	std::string s;
	char *coordString;
	
	ss << "2";

	s = ss.str();

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].eyeX;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].eyeY;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].eyeZ;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].centerX;

	s.append(ss.str());
	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].centerY;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].centerZ;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].upX;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].upY;

	s.append(ss.str());

	s.append(",");

	ss.str("");
	ss.clear();

	ss << proDat[projNum].upZ;

	s.append(ss.str());

	s.append(",");

	coordString = new char[s.length() + 1];
	strcpy(coordString, s.c_str());

	return coordString;
}
