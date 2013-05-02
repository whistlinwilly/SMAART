/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/

package projector.rendering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import projector.client.NetClient;
import projector.main.MainActivity;
import projector.applications.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer {
   private static final String TAG = "GLRenderer";
   private final Context context;
   
   //application enums
   public static final int COLORCHANGING = 0;
   public static final int HOUSE = 1;
   public static final int ALEX = 2;
   public volatile int application = -1;
   
   
   
   private final GLCircle bigCircle = new GLCircle(0,0,2,100);
   private final GLCircle smallCircle = new GLCircle(0,3,0.5f,100);
   
   boolean stoopidBoolean = false;
   boolean sillyCodyBoolean = false;
   
 //North / West
   private final GLCircle circ00 = new GLCircle(0,0,0.5f,100);
   private final GLCircle circn20 = new GLCircle(-2,0,0.5f,100);
   private final GLCircle circn40 = new GLCircle(-4,0,0.5f,100);
   private final GLCircle circn22 = new GLCircle(-2,2,0.5f,100);
   private final GLCircle circn24 = new GLCircle(-2,4,0.5f,100);
   private final GLCircle circn42 = new GLCircle(-4,2,0.5f,100);
   private final GLCircle circn44 = new GLCircle(-4,4,0.5f,100);
   private final GLCircle circ02 = new GLCircle(0,2,0.5f,100);
   private final GLCircle circ04 = new GLCircle(0,4,0.5f,100);
   
   //North / East
   private final GLCircle circ20 = new GLCircle(2,0,0.5f,100);
   private final GLCircle circ40 = new GLCircle(4,0,0.5f,100);
   private final GLCircle circ22 = new GLCircle(2,2,0.5f,100);
   private final GLCircle circ24 = new GLCircle(2,4,0.5f,100);
   private final GLCircle circ42 = new GLCircle(4,2,0.5f,100);
   private final GLCircle circ44 = new GLCircle(4,4,0.5f,100);
   
   //South / East
   private final GLCircle circ0n2 = new GLCircle(0,-2,0.5f,100);
   private final GLCircle circ0n4 = new GLCircle(0,-4,0.5f,100);
   private final GLCircle circ2n2 = new GLCircle(2,-2,0.5f,100);
   private final GLCircle circ2n4 = new GLCircle(2,-4,0.5f,100);
   private final GLCircle circ4n2 = new GLCircle(4,-2,0.5f,100);
   private final GLCircle circ4n4 = new GLCircle(4,-4,0.5f,100);
   
   //South / West
   private final GLCircle circn2n2 = new GLCircle(-2,-2,0.5f,100);
   private final GLCircle circn4n2 = new GLCircle(-4,-2,0.5f,100);
   private final GLCircle circn2n4 = new GLCircle(-2,-4,0.5f,100);
   private final GLCircle circn4n4 = new GLCircle(-4,-4,0.5f,100);
   
   public long startTime;
   public long elapsedTime;
   
   private int numObjects = 0;
   
   public ArrayList<Object> objects = new ArrayList<Object>();
   private ArrayList<Animation> animations = new ArrayList<Animation>();

   public float xAngle = 0.0f;
   public float yAngle = 0;
   public float zAngle = 0;
   
   public float xDist = 0.0f;
   public float yDist = 0.0f;
   public float zDist = -10.0f;
   
   public float xTrans = 0.0f;
   public float yTrans = 0.0f;
   public float scale = 1.0f;
   
   private float width = 0.0f;
   private float height = 0.0f;
   public float eyeX = 0.0f;
   public float eyeY = 0.0f;
   public float eyeZ = 24.0f;
   public float centerX = 0.0f;
   public float centerY = 0.0f;
   public float centerZ = 0.0f;
   public float upX  = 0.0f;
   public float upY = 1.0f;
   public float upZ  = 0.0f;
   public NetClient netClient;
   
   float theta = 0.0f;
   
   float animationPoint = 0.0f;
   float animationDuration = 4.0f;
	
   private GL10 gl;
	
   private int numTextures = 0;
	
   ObjectFactory factory = new ObjectFactory("/Objects");
   AnimationFactory ani = new AnimationFactory("/Animations");
	
   /** The texture pointer */
   public int[] textures = new int[10];
   public MainActivity mainActivity;
   private int numAnimations = 0;
   
   int thisProjector;
   ViewFrustrum[] projectors;
   private float alwaysFOV = 17.0f;
   private float alwaysNearPlane = 2.0f;
   private float alwaysFarPlane = 60.0f;
   
   public boolean perspectiveSet = false;
   

   GLRenderer(Context context) {
      this.context = context;
   }

   public float[] parseData(){
	   
		String receiveValsString[];
		float[] receiveVals = new float[10];
		float[] eyeCoords = new float[3];
		float[] centerCoords = new float[3];
		float[] upCoords = new float[3];
		
			
			receiveValsString = netClient.inString.split(",");
			
			projectors = new ViewFrustrum[(receiveValsString.length - 1) / 10];
			
			for(int k=0; k < projectors.length; k++)
				projectors[k] = new ViewFrustrum();
			
			for(int i=0; i<receiveValsString.length - 1; i+=10){
				
				eyeCoords[0] = Float.parseFloat(receiveValsString[i + 1]);
				eyeCoords[1] = Float.parseFloat(receiveValsString[i + 2]);
				eyeCoords[2] = Float.parseFloat(receiveValsString[i + 3]);
				
				centerCoords[0] = Float.parseFloat(receiveValsString[i + 4]);
				centerCoords[1] = Float.parseFloat(receiveValsString[i + 5]);
				centerCoords[2] = Float.parseFloat(receiveValsString[i + 6]);
				
				upCoords[0] = Float.parseFloat(receiveValsString[i + 7]);
				upCoords[1] = Float.parseFloat(receiveValsString[i + 8]);
				upCoords[2] = Float.parseFloat(receiveValsString[i + 9]);
				
				ViewFrustrum crappy = projectors[i/10];
				
				crappy.initializeFrustrum(eyeCoords, centerCoords, upCoords, alwaysFOV, width/height, alwaysNearPlane, alwaysFarPlane);
				
				if( (i/10) == thisProjector)
					for(int j = 0; j < 9; j++) {
						if((j / 3) == 0)
							receiveVals[j] = eyeCoords[j];
						else if((j/3) == 1)
							receiveVals[j] = centerCoords[j-3];
						else
							receiveVals[j] = upCoords[j-6];
					}
				
			}
		
		perspectiveSet = true;
		return receiveVals;
	}
   
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      
      // ...
      
      
      
      boolean SEE_THRU = false;
      this.gl = gl;
      
      startTime = System.currentTimeMillis();
      


      // Define the lighting
      float lightAmbient[] = new float[] { 1.0f, 1.0f, 1.0f, 1 };
      float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
      float[] lightPos = new float[] { 5, 3, 10, 1 };
      gl.glEnable(GL10.GL_LIGHTING);
      gl.glEnable(GL10.GL_LIGHT0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);

      

      
      // What is the cube made of?
      float matAmbient[] = new float[] { 1, 1, 1, 1 };
      float matDiffuse[] = new float[] { 1, 1, 1, 1 };
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
            matAmbient, 0);
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
            matDiffuse, 0);
      

      
      // Set up any OpenGL options we need
      gl.glEnable(GL10.GL_DEPTH_TEST); 
      gl.glDepthFunc(GL10.GL_LEQUAL);
    //  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

      // Optional: disable dither to boost performance
      // gl.glEnable(GL10.GL_DITHER);
      

      
      // ...
      if (SEE_THRU) {
         gl.glDisable(GL10.GL_DEPTH_TEST);
         gl.glEnable(GL10.GL_BLEND);
         gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
      }
      
      gl.glEnable (GL10.GL_BLEND);
      gl.glBlendFunc (GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
      
     
      
      
      // Enable textures
 //     gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
  //    loadTexture(gl, "Site.bmp");
  //    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      // Load the cube's texture from a bitmap
   //   GLCube.loadTexture(gl, context, R.drawable.android);
      
      mainActivity.init();
      
   }
   
   

   
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      
      // ...
      
      
      // Define the view frustum
      gl.glViewport(0, 0, width, height);
      this.width = width;
      this.height = height;
      
      Log.w("MESSAGE", "width is " + width + " and height is " + height);
     // GLU.gluOrtho2D(gl, 0, width, 0, height);
      
   }
   

   
   
   
   public void onDrawFrame(GL10 gl) {
	   

	  elapsedTime = System.currentTimeMillis() - startTime;
	  startTime = System.currentTimeMillis();   
      
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      float ratio = (float) width / height;
      
      
      //Log.i("STAGE", "" + mainActivity.stage);
      
      
      if(mainActivity.stage == MainActivity.IDLE || mainActivity.stage == MainActivity.RENDER_CIRCLES){
    	  if(mainActivity.stage == MainActivity.IDLE && sillyCodyBoolean == false){
    		  setProjector();
    		  sillyCodyBoolean = true;
    	  }

		  // GLU.gluOrtho2D(gl, 0, width, 0, height);
		   //used to be 17.5
    	  
    	//  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	  
		   GLU.gluPerspective(gl, alwaysFOV, ratio, alwaysNearPlane, alwaysFarPlane); 
		   GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		      // Clear the screen to black
		      gl.glClear(GL10.GL_COLOR_BUFFER_BIT
		            | GL10.GL_DEPTH_BUFFER_BIT);
		      // Position model so we can see it
		      gl.glMatrixMode(GL10.GL_MODELVIEW);
		      gl.glLoadIdentity();
		      if (mainActivity.stage == MainActivity.RENDER_CIRCLES){
		    	  bigCircle.draw(gl);
				  smallCircle.draw(gl);
		      }
		      
		   //   gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	   }
	   else if(mainActivity.stage == MainActivity.RENDER_MAPPED){
		   if (!perspectiveSet) setValues(parseData());
		   GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 
		   GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		   gl.glClear(GL10.GL_COLOR_BUFFER_BIT
		            | GL10.GL_DEPTH_BUFFER_BIT);

		      // Position model so we can see it
		      gl.glMatrixMode(GL10.GL_MODELVIEW);
		      gl.glLoadIdentity();
		      
		  //    myObj.draw(gl);
		      
		      //CIRCLE GRID TEST PATTERN
		      float matAmbient[] = new float[] { 1, 0, 0, 1 };
		      float matDiffuse[] = new float[] { 1, 0, 0, 1 };
		      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
		            matAmbient, 0);
		      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
		            matDiffuse, 0);
		      
		      circ00.draw(gl);
		      
		      matAmbient = new float[] { 0, 0, 1, 1 };
		      matDiffuse = new float[] { 0, 0, 1, 1 };
		      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
		            matAmbient, 0);
		      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
		            matDiffuse, 0);
		      
		      circn20.draw(gl);
		      circn40.draw(gl);
		      circn22.draw(gl);
		      circn24.draw(gl);
		      circn42.draw(gl);
		      circn44.draw(gl);
		      circ02.draw(gl);
		      circ04.draw(gl);
		      circ20.draw(gl);
		      circ40.draw(gl);
		      circ22.draw(gl);
		      circ24.draw(gl);
		      circ42.draw(gl);
		      circ44.draw(gl);
		      circ0n2.draw(gl);
		      circ0n4.draw(gl);
		      circ2n2.draw(gl);
		      circ2n4.draw(gl);
		      circ4n2.draw(gl);
		      circ4n4.draw(gl);
		      circn2n2.draw(gl);
		      circn4n2.draw(gl);
		      circn2n4.draw(gl);
		      circn4n4.draw(gl);
	   
	   
	   }
	   else if (mainActivity.stage == MainActivity.RUN){
		   
		   //all demo instantiations
		   ColorChangingDemo colorChangingDemo = new ColorChangingDemo(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, netClient);
		   HouseDemo houseDemo = new HouseDemo(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, 
												upX, upY, upZ, netClient);
		   
		   //switch to tell when to run each demo
		   if (this.application == COLORCHANGING)
			   colorChangingDemo.run(gl, ratio, objects);
		   
		   else if (this.application == HOUSE)
			   houseDemo.run(gl, ratio, objects, animations, mainActivity, textures);
		   
		   //else if (this.application == ALEX)
			   //alexDemo.run();
	   }
      
      netClient.messageReady = false;
   }
   

private void setProjector() {
	   String[] temp = netClient.inString.split(",");
	   thisProjector = Integer.parseInt(temp[1]);
   }


public void setValues(float[] vals){
	   if (vals != null && vals.length > 5){
		   eyeX = vals[0];
		   eyeY = vals[1];
		   eyeZ = vals[2];
		   centerX = vals[3];
		   centerY = vals[4];
		   centerZ = vals[5];
		   upX = vals[6];
		   upY = vals[7];
		   upZ = vals[8];
	   }
   }
   
   public int loadTexture(String fileName){
	   
	   
	   
	   
	   Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Textures/" + fileName);
	   
	   gl.glActiveTexture(GL10.GL_TEXTURE0 + numTextures);
	   gl.glClientActiveTexture(GL10.GL_TEXTURE0 + numTextures);
	   
	   gl.glEnable(GL10.GL_TEXTURE_2D);
	   
	   gl.glGenTextures(1, textures, numTextures);
	   
	   
	   
			// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[numTextures]);
			
			// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

		
			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			
			// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Clean up
		bitmap.recycle();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		return numTextures++;
   }
   
   public int loadObject(String fileName){
	      try {
	    	  objects.add(factory.loadObject(fileName, context));
	    	  objects.get(numObjects).setRenderer(this);
	    	  return numObjects++;
	  	} catch (FileNotFoundException e) {
	  		// TODO Auto-generated catch block
	  		e.printStackTrace();
	  	}
		return -1;  
   }
   
   public int loadAnimation(String fileName){
	      try {
	    	  animations.add(ani.loadAnimation(fileName));
	    	  return numAnimations ++;
	  	} catch (FileNotFoundException e) {
	  		// TODO Auto-generated catch block
	  		e.printStackTrace();
	  	} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;  
}
   
   public void show(int objNum){
	   Object obj;
	   if(objNum < numObjects){
		   obj = objects.get(objNum);
		   obj.draw = true;
	   }
	   
   }
   
   public void hide(int objNum){
	   Object obj;
	   if(objNum < numObjects){
		   obj = objects.get(objNum);
		   obj.draw = false;
	   }
	   
   }
   
   public void setObjectTexture(int objNum, int texNum){
	   Object obj;
	   if(objNum < numObjects){
		   obj = objects.get(objNum);
		   obj.texNum = texNum;
	   }
   }
   

   
   public void playAnimation(int objNum, int aniNum, int times, float duration){
	   Object obj;
	   if(objNum < numObjects){
		   if(aniNum < numAnimations){
			   obj = objects.get(objNum);
			   obj.a = animations.get(aniNum);
			   obj.aDuration = duration;
			   obj.aTimesToPlay = times;
			   obj.aPoint = 0.0f;
			   obj.aFrameLength = duration / (animations.get(aniNum).frames - 1);
		   }
	   }
   }
   
   public void playTextureAnimation(int objNum, int[] textures, float[] textureLengths, int times, float duration){
	   Object obj;
	   if(objNum < numObjects){
		   obj = objects.get(objNum);
		   obj.taPoint = 0.0f;
		   obj.taDuration = duration;
		   obj.taIndex = 0;
		   obj.taLengths = textureLengths;
		   obj.taTextures = textures;
		   obj.taTimesToPlay = times;
	   }
   }
   
   public boolean findProjector(Surface surface){
		
		int cornersInFrustrum = 0;
		int[] goodProjectors;
		
		float[] vertices = new float[9];
		surface.vertices.get(vertices, 0, 9);
		surface.vertices.position(0);
		
//NOT SURE THIS IS NEEDED		
		
//		if(projectors[thisProjector].pointInFrustrum(vertices[0], vertices[1], vertices[2]))
//			cornersInFrustrum++;
//		
//		if(projectors[thisProjector].pointInFrustrum(vertices[3], vertices[4], vertices[5]))
//			cornersInFrustrum++;
//		
//		if(projectors[thisProjector].pointInFrustrum(vertices[6], vertices[7], vertices[8]))
//			cornersInFrustrum++;
//		
//		if(cornersInFrustrum == 0)
//			return false;
		
		goodProjectors = findViewingFrustrums(vertices); //returns all valid viewing frustrums by index
		
		if(goodProjectors == null)
			return true;
		
		boolean hasThisProjector = false;
		
		for(int i: goodProjectors)
			if(i == thisProjector)
				hasThisProjector = true;
		
		if(hasThisProjector == false)
			return false;
		
		return (thisProjector == findBestProjByAngle(goodProjectors, surface)); // dots all the surfaces with projectors
		
	}
	
	public int[] findViewingFrustrums(float[] vertices){
		
		int cornersInFrustrum = 0;
		int vfNum = 0;
		ArrayList<Integer> oneCornered = new ArrayList<Integer>();
		ArrayList<Integer> twoCornered = new ArrayList<Integer>();
		ArrayList<Integer> threeCornered = new ArrayList<Integer>();
		
		for(ViewFrustrum vf: projectors){
			cornersInFrustrum = 0;
			
			if(vf.pointInFrustrum(vertices[0], vertices[1], vertices[2]))
				cornersInFrustrum++;
			
			if(vf.pointInFrustrum(vertices[3], vertices[4], vertices[5]))
				cornersInFrustrum++;
			
			if(vf.pointInFrustrum(vertices[6], vertices[7], vertices[8]))
				cornersInFrustrum++;
			
			if(cornersInFrustrum == 3)
				threeCornered.add(vfNum);
			if(cornersInFrustrum == 2)
				twoCornered.add(vfNum);
			if(cornersInFrustrum == 1)
				oneCornered.add(vfNum);
				
			vfNum++;
			
		}
		
		if(!threeCornered.isEmpty()){
			int[] ints = new int[threeCornered.size()];
			int n = 0;
			for(Integer i: threeCornered)
				ints[n++] = i;
			return ints;
		}
		
		if(!twoCornered.isEmpty()){
			int[] ints = new int[twoCornered.size()];
			int n = 0;
			for(Integer i: twoCornered)
				ints[n++] = i;
			return ints;
		}
		
		if(!oneCornered.isEmpty()){
			int[] ints = new int[oneCornered.size()];
			int n = 0;
			for(Integer i: oneCornered)
				ints[n++] = i;
			return ints;
		}
		
		return null;
	}
	
	public int findBestProjByAngle(int[] goodProjectors, Surface surface){
		
		ViewFrustrum vf;
		float bestAngle = 360.0f;
		int bestProj = 0;
		float[] surfaceToProjector = new float[3];
		
		for(int i = 0; i < goodProjectors.length; i++){
			vf = projectors[goodProjectors[i]];
			
			surfaceToProjector[0] = vf.point[0] - surface.center[0];
			surfaceToProjector[1] = vf.point[1] - surface.center[1];
			surfaceToProjector[2] = vf.point[2] - surface.center[2];
			
			float angle = cosFromDot(surfaceToProjector, surface.normal);
			
			if(angle < bestAngle){
				bestProj = goodProjectors[i];
				bestAngle = angle;
			}
		}
		
		return bestProj;
	}
	
	public float cosFromDot(float[] stp, float[] normal){
		
		float dot = stp[0] * normal[0] + stp[1] * normal[1] + stp[2] * normal[2];
		float stpMag = (float) Math.sqrt(Math.pow(stp[0],2) + Math.pow(stp[1], 2) + Math.pow(stp[2],2));
		float nMag = (float) Math.sqrt(Math.pow(normal[0],2) + Math.pow(normal[1], 2) + Math.pow(normal[2],2));
		float angle = (float) Math.acos(dot / (stpMag * nMag));
		
		return angle;
		
	}
   
}
