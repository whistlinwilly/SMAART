package projector.rendering;

public class Animation {
	
	boolean hasLocation = false;
	boolean hasRotation = false;
	boolean hasScale = false;
	
	int frames;
	
	//LOCATION DATA FOR XYZ
	float[] xLocIn;
	float[] xLocOut;
	float[] xLocIntan;
	float[] xLocOuttan;
	float[] yLocIn;
	float[] yLocOut;
	float[] yLocIntan;
	float[] yLocOuttan;
	float[] zLocIn;
	float[] zLocOut;
	float[] zLocIntan;
	float[] zLocOuttan;
	
	//ROTATION DATA FOR XYZ
	float[] xRotIn;
	float[] xRotOut;
	float[] xRotIntan;
	float[] xRotOuttan;
	float[] yRotIn;
	float[] yRotOut;
	float[] yRotIntan;
	float[] yRotOuttan;
	float[] zRotIn;
	float[] zRotOut;
	float[] zRotIntan;
	float[] zRotOuttan;


	public float valueAtTime(float time, int type){
		
		int i = (int) Math.floor(time);
		time = time - i;

		float[] inArray = null, outArray = null, inTan = null, outTan = null;
		
		if(type == AnimationFactory.X_LOC){
			inArray = xLocIn;
			outArray = xLocOut;
			inTan = xLocIntan;
			outTan = xLocOuttan;
		}
		else if(type == AnimationFactory.Y_LOC){
			inArray = yLocIn;
			outArray = yLocOut;
			inTan = yLocIntan;
			outTan = yLocOuttan;
		}
		else if(type == AnimationFactory.Z_LOC){
			inArray = zLocIn;
			outArray = zLocOut;
			inTan = zLocIntan;
			outTan = zLocOuttan;
		}
		else if(type == AnimationFactory.X_ROT){
			inArray = xRotIn;
			outArray = xRotOut;
			inTan = xRotIntan;
			outTan = xRotOuttan;
		}
		else if(type == AnimationFactory.Y_ROT){
			inArray = yRotIn;
			outArray = yRotOut;
			inTan = yRotIntan;
			outTan = yRotOuttan;
		}
		else if(type == AnimationFactory.Z_ROT){
			inArray = zRotIn;
			outArray = zRotOut;
			inTan = zRotIntan;
			outTan = zRotOuttan;
		}
		
		float xStart = inArray[i];
		float xEnd = inArray[i + 1];
		
		float yStart = outArray[i];
		float yEnd = outArray[i + 1];
		
		float xStartTan = outTan[2 * i];
		float yStartTan = outTan[2 * i + 1];
		
		float xEndTan = inTan[2 * i + 2];
		float yEndTan = inTan[2 * i +3];
		
		float xA = (1-time) * xStart + time * xStartTan;
		float yA = (1-time) * yStart + time * yStartTan;
		
		float xB = (1-time) * xStartTan + time * xEndTan;
		float yB = (1-time) * yStartTan + time * yEndTan;
		
		float xC = (1-time) * xEndTan + time * xEnd;
		float yC = (1-time) * yEndTan + time * yEnd;
		
		float xP = (1-time) * xA + time * xB;
		float yP = (1-time) * yA + time * yB;
		
		float xQ = (1-time) * xB + time * xC;
		float yQ = (1-time) * yB + time * yC;
		
		float xAns = (1-time) * xP + time * xQ;
		float yAns = (1-time) * yP + time * yQ;
		
		
		return yAns;
	}

}