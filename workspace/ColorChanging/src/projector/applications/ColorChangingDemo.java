package projector.applications;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import projector.client.NetClient;
import projector.rendering.Object;

public class ColorChangingDemo{
	
	public int numTriangles = 80;
	public int[] colorValues = new int[numTriangles * numTriangles * 2];	
	private float eyeX, eyeY, eyeZ;
	private float centerX, centerY, centerZ;
	private float upX, upY, upZ;
	private int globalColor;
	private NetClient netClient;
	
	public ColorChangingDemo(float eyeX, float eyeY, float eyeZ, 
							float centerX, float centerY, float centerZ, 
							float upX, float upY, float upZ, NetClient netClient){
		this.eyeX = eyeX;
		this.eyeY = eyeY;
		this.eyeZ = eyeZ;
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;
		this.netClient = netClient;
	}
	
	public void run(GL10 gl, float ratio, ArrayList<Object> objects){
	   
		updateColorArray();
	   	gl.glDisable(GL10.GL_LIGHT0);
	   	gl.glDisable(GL10.GL_LIGHTING);
	//   	gl.glDisable(GL10.GL_TEXTURE_2D);
	   
		   GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 	      
		   GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

		   // Clear the screen to black
		   gl.glClear(GL10.GL_COLOR_BUFFER_BIT
				   | GL10.GL_DEPTH_BUFFER_BIT);

		   // Position model so we can see it
		   gl.glMatrixMode(GL10.GL_MODELVIEW);
		   gl.glLoadIdentity();
		      
		   
		   float translationAmount = 19.875f / (numTriangles * 2);
		   float translation = -9.9375f + translationAmount;
		   
		   gl.glTranslatef(translation, -translation, 0.0f);

		   
		   
		   globalColor = 0;
		   
		   //vertical
		   for(int i = 0; i < numTriangles; i++){
			   gl.glPushMatrix();
			   gl.glTranslatef(0.0f, -translationAmount * 2 * i, 0.0f);
			   //horizontal
			   for(int j = 0; j < numTriangles; j++){
				   gl.glPushMatrix();
				   gl.glTranslatef(translationAmount * 2 * j, 0.0f, 0.0f);
				   gl.glScalef(1.0f / numTriangles, 1.0f / numTriangles, 1.0f);
				   float[] colors = getColorModel(globalColor++);
				   gl.glColor4f(colors[0], colors[1], colors[2], 1.0f);
				   objects.get(0).draw(gl);

				   colors = getColorModel(globalColor++);
				   gl.glColor4f(colors[0], colors[1], colors[2], 1.0f);
				   objects.get(1).draw(gl);
				   
				   gl.glPopMatrix();
			   }
			   gl.glPopMatrix();
		   }
		   
	//	   gl.glEnable(GL10.GL_TEXTURE_2D);
		   gl.glEnable(GL10.GL_LIGHTING);
		   gl.glEnable(GL10.GL_LIGHT0);
	}
	
	
	
	// helper functions only referenced in RUN STAGE for color changing demo
	private float[] getColorModel(int i) {
		   float[] values = new float[3];
		   float frequency = 0.008f;
		   float red   = (((float)Math.sin(frequency*colorValues[i] + 0) * 1.0f) * 127.0f + 128.0f) / 255.0f;
		   float green = (((float)Math.sin(frequency*colorValues[i] + 2) * 1.0f) * 127.0f + 128.0f) / 255.0f;
		   float blue  = (((float)Math.sin(frequency*colorValues[i] + 4) * 1.0f) * 127.0f + 128.0f) / 255.0f;
		   values[0] = red;
		   values[1] = green;
		   values[2] = blue;
		   return values;
	}
	
	private void updateColorArray() {
		String[] indices = netClient.inString.split(",");
		
//		for(int i = 0; i < activity.view.renderer.colorValues.length; i++)
//			if(activity.view.renderer.colorValues[i] > 0)
//				activity.view.renderer.colorValues[i]-=10;
		int j;
		String index;
		for(int n = 1; n < indices.length; n++){
			index = indices[n];
			j = Integer.parseInt(index);
			this.colorValues[j]+=80; 
		}
		
		for(int i = 0; i < colorValues.length; i++)
			if(colorValues[i] > 0)
				colorValues[i] -= 110;
			else 
				colorValues[i] = 0;
		
	//	inString = "";
		
		
	}
}