package projector.applications;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import projector.client.NetClient;
import projector.main.MainActivity;
import projector.rendering.GLRenderer;
import projector.rendering.Object;

public class AlexDemo{
	
	private float eyeX, eyeY, eyeZ;
	private float centerX, centerY, centerZ;
	private float upX, upY, upZ;
	private NetClient netClient;
	private float lightX = 0.0f;
	private float lightY = 0.0f;
	private float lightZ = -20.0f;
	private float lightBrightness = 0.0f;
	private float lightTheta = 0.0f;
	private GLRenderer glr;
	
	public AlexDemo(float eyeX, float eyeY, float eyeZ, 
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
	
	public void run(GL10 gl, float ratio, MainActivity activity){
		glr = activity.view.renderer;
		String[] args = netClient.inString.split(",");
		always(gl, ratio);
		if (args.length > 1 && !args[1].equals("alexDemo")){
			if (args[1].equals("none")) showNone();
			else if (args[1].equals("all")) showAll();
			else if (args[1].equals("headhouse")) showHeadHouse();
			else if (args[1].equals("contours")) showContours();
			else if (args[1].equals("circulation")) showCirculation();
			else if (args[1].equals("single")) showSingle();
			else if (args[1].equals("double")) showDouble();
			else if (args[1].equals("triple")) showTriple();
			else if (args[1].equals("housekeeping")) showHousekeeping();
			else if (args[1].equals("skylight")) showSkylight();
		}
	}
	
	public void showAll(){
		glr.show(glr.pCirculation);
		glr.show(glr.pContours);
		glr.show(glr.pDoubles);
		glr.show(glr.pHeadHouse);
		glr.show(glr.pHousekeeping);
		glr.show(glr.pSingles);
		glr.show(glr.pSkylight);
		glr.show(glr.pTriples);
	}
	public void showNone(){
		glr.hide(glr.pCirculation);
		glr.hide(glr.pContours);
		glr.hide(glr.pDoubles);
		glr.hide(glr.pHeadHouse);
		glr.hide(glr.pHousekeeping);
		glr.hide(glr.pSingles);
		glr.hide(glr.pSkylight);
		glr.hide(glr.pTriples);
		
		
		int[] testAni = {glr.pFrame1, glr.pFrame2, glr.pFrame3,glr.pFrame4,  glr.pFrame5,glr.pFrame6,
				glr.pFrame7, glr.pFrame8, glr.pFrame9, glr.pFrame10, glr.pFrame11
		};
		
		float[] durations = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
		
		glr.show(glr.pAnimationShell);
		glr.playTextureAnimation(glr.pAnimationShell, testAni, durations, 20, 20.0f);
	}
	
	public void showHeadHouse(){
		glr.hide(glr.pAnimationShell);
		glr.show(glr.pCirculation);
		glr.show(glr.pContours);
		glr.show(glr.pDoubles);
		glr.show(glr.pHeadHouse);
		glr.show(glr.pHousekeeping);
		glr.show(glr.pSingles);
		glr.show(glr.pSkylight);
		glr.show(glr.pTriples);
		glr.setObjectTexture(glr.pCirculation, glr.blackTex);
		glr.setObjectTexture(glr.pContours, glr.blackTex);
		glr.setObjectTexture(glr.pDoubles, glr.blackTex);
		//glr.setObjectTexture(glr.pHeadHouse, glr.blackTex);
		glr.setObjectTexture(glr.pHousekeeping, glr.blackTex);
		glr.setObjectTexture(glr.pSingles, glr.blackTex);
		glr.setObjectTexture(glr.pTriples, glr.blackTex);
		glr.setObjectTexture(glr.pSkylight, glr.blackTex);
	}
	
	public void showContours(){
		glr.setObjectTexture(glr.pContours, glr.pContoursTex);
		glr.setObjectTexture(glr.pHeadHouse, glr.blackTex);
	}
	
	public void showCirculation(){
		glr.setObjectTexture(glr.pCirculation, glr.pCirculationTex);
		glr.setObjectTexture(glr.pContours, glr.blackTex);
	}
	
	public void showSingle(){
		glr.setObjectTexture(glr.pCirculation, glr.blackTex);
		glr.setObjectTexture(glr.pSingles, glr.pSinglesTex);
	}
	
	public void showDouble(){
		glr.setObjectTexture(glr.pSingles, glr.blackTex);
		glr.setObjectTexture(glr.pDoubles, glr.pDoublesTex);
	}
	
	public void showTriple(){
		glr.setObjectTexture(glr.pTriples, glr.pTriplesTex);
		glr.setObjectTexture(glr.pDoubles, glr.blackTex);
	}
	
	public void showHousekeeping(){
		glr.setObjectTexture(glr.pHousekeeping, glr.pHousekeepingTex);
		glr.setObjectTexture(glr.pTriples, glr.blackTex);
	}
	
	public void showSkylight(){
		glr.setObjectTexture(glr.pSkylight, glr.pSkylightTex);
		glr.setObjectTexture(glr.pHousekeeping, glr.blackTex);
	}
	
	public void always(GL10 gl, float ratio){
		// Define the lighting
	      float lightAmbient[] = new float[] { 1.0f, 1.0f, 1.0f, 1 };
	      float lightDiffuse[] = new float[] { lightBrightness, lightBrightness, lightBrightness, 1 };
	      float[] lightPos = new float[] { lightX, lightY, lightZ, 1 };
	      gl.glEnable(GL10.GL_LIGHTING);
	      gl.glEnable(GL10.GL_LIGHT0);
	      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
	      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
	      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
	   
	     
	      

	      
	   GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 	      
	   GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

	   // Clear the screen to black
	   gl.glClear(GL10.GL_COLOR_BUFFER_BIT
			   | GL10.GL_DEPTH_BUFFER_BIT);

	   // Position model so we can see it
	   gl.glMatrixMode(GL10.GL_MODELVIEW);
	   gl.glLoadIdentity();
	   
	      float matAmbient[] = new float[] { 1, 1, 1, 1 };
	      float matDiffuse[] = new float[] { 1, 1, 1, 1 };
	      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
	            matAmbient, 0);
	      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
	            matDiffuse, 0);


	      
	   for(Object curObject: glr.objects){
		   curObject.updateAnimationValues(glr.elapsedTime);
		   if(curObject.draw){
			   gl.glActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
			   gl.glClientActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
			   gl.glEnable(GL10.GL_TEXTURE_2D);
			   gl.glBindTexture(GL10.GL_TEXTURE_2D, glr.textures[curObject.texNum]);
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