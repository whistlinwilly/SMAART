package projector.applications;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import projector.rendering.Animation;
import projector.rendering.GLRenderer;
import projector.rendering.Object;
import android.opengl.GLU;

import projector.client.NetClient;
import projector.main.MainActivity;

public class HouseDemo {
	
	public static final int SUNRISE = 5;
	public static final int STORM = 6;
	private float lightBrightness = 0.0f;
	private float lightX = 0.0f;
	private float lightY = 0.0f;
	private float lightZ = -20.0f;
	private float lightTheta = 0.0f;
	private float eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ;
	boolean stoopidBoolean = false;
	private NetClient netClient;
	private volatile int lightStage = -1;
	
	public HouseDemo(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, 
			float upX, float upY, float upZ, NetClient netClient) {
		
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
	
	public void run(GL10 gl, float ratio, ArrayList<Object> objects, ArrayList<Animation> animations,
			MainActivity mainActivity, int[] passedInTextures) {
		int houseCommand = Integer.parseInt(netClient.inString.substring(2, netClient.inString.length()));
		
		if(houseCommand == SUNRISE){
			mainActivity.playSound("rooster.mp3", true);
			lightStage = 0;
			}
		else if(houseCommand == STORM){
			lightStage = 2;
		}
		
		//Define the lighting
		float lightAmbient[] = new float[] { 0.1f, 0.1f, 0.1f, 1 };
		float lightDiffuse[] = new float[] { lightBrightness, lightBrightness, lightBrightness, 1 };
		float[] lightPos = new float[] { lightX, lightY, lightZ, 1 };
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		
		if(lightStage == 0){
			lightY = (float) (-20 * Math.cos(lightTheta) - 10.0f);
			lightZ = (float) (30 * Math.sin(lightTheta));
			  
			lightTheta += 0.005f;
			lightBrightness += 0.005f;
			  
			if(lightTheta > 1.0f){
				lightStage = 1;
				mainActivity.playSound("birds.mp3", true);
				mainActivity.view.renderer.show(2);
				mainActivity.view.renderer.playAnimation(2, 1, 20, 20.0f);
				mainActivity.view.renderer.show(3);
				mainActivity.view.renderer.playAnimation(3, 1, 20, 18.0f);
				mainActivity.view.renderer.show(4);
				mainActivity.view.renderer.playAnimation(4, 1, 20, 22.0f);
			}
		}

		if(lightStage == 3){
			int textures[] = {2, 3, 2, 4, 2, 5};
			float textureLengths[] = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
			mainActivity.playSound("rain.mp3", true);
			mainActivity.view.renderer.playTextureAnimation(1, textures, textureLengths, 60, 1.0f);
			lightStage = 4;
		}

		if(lightStage == 2){
			lightBrightness -= 0.005f;
			  
			if(lightBrightness < 0.6f && !stoopidBoolean){
				int textures[] = {1, 0, 1, 0, 1};
				float textureLengths[] = {0.2f, 0.1f, 0.2f, 0.1f, 0.4f};
				mainActivity.view.renderer.hide(2);
				mainActivity.view.renderer.hide(3);
				mainActivity.view.renderer.hide(4);
				mainActivity.playSound("thunder.mp3", true);
				mainActivity.view.renderer.playTextureAnimation(0, textures, textureLengths, 2, 1.0f);
				stoopidBoolean = true;
			}
			  
			if(lightBrightness < 0.15f ){
				lightStage = 3;
			}
		}


		GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 	      
		GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

		// Clear the screen to black
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Position model so we can see it
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		float matAmbient[] = new float[] { 1, 1, 1, 1 };
		float matDiffuse[] = new float[] { 1, 1, 1, 1 };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);

		for(Object curObject: objects){
			curObject.updateAnimationValues(mainActivity.view.renderer.elapsedTime);
			if(curObject.draw){
				gl.glActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
				gl.glClientActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, passedInTextures[curObject.texNum]);
				gl.glPushMatrix();
				gl.glTranslatef(curObject.x,curObject.y, curObject.z);
				gl.glRotatef(curObject.theta,0.0f,0.0f,1.0f);
			      
				curObject.draw(gl);
				gl.glPopMatrix();
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
		}
	}
	   
}





