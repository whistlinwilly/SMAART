package projector.rendering;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Surface {
	
	ByteBuffer index;
	int type;
	int QUAD = 0;
	int TRI = 1;
	FloatBuffer vertices, normals;
	FloatBuffer textures;
	float[] center = new float[3];
	float[] normal = new float[3];
	
	public Surface(float avgX, float avgY, float avgZ){
		center[0] = avgX;
		center[1] = avgY;
		center[2] = avgZ;
	}
	
	public void setNormals(float x, float y, float z){
		normal[0] = x;
		normal[1] = y;
		normal[2] = z;
	}

}
