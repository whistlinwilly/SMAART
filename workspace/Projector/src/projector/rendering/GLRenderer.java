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
   
   private final GLCircle bigCircle = new GLCircle(0,0,2,100);
   private final GLCircle smallCircle = new GLCircle(0,3,0.5f,100);
   
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
   
   private long startTime;
   private long fpsStartTime;
   private long numFrames;
   
   private int numObjects = 0;
   
   private ArrayList<Object> objects = new ArrayList<Object>();
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
   public float eyeZ = 20.0f;
   public float centerX = 0.0f;
   public float centerY = 0.0f;
   public float centerZ = -1.0f;
   public float upX  = 0.0f;
   public float upY = 1.0f;
   public float upZ  = 0.0f;
   public NetClient netClient;
   
   float animationPoint = 0.0f;
   float animationDuration = 4.0f;
	
   private GL10 gl;
	
   private int numTextures = 0;
	
   ObjectFactory factory = new ObjectFactory("/Objects");
   AnimationFactory ani = new AnimationFactory("/Animations");
	
   /** The texture pointer */
   private int[] textures = new int[10];
   public MainActivity mainActivity;
   private int numAnimations = 0;
   
   

   GLRenderer(Context context) {
      this.context = context;
   }

   public float[] parseData(){
		String receiveValsString[];
		float[] receiveVals = new float[10];
		
		if (mainActivity.stage == MainActivity.RENDER_MAPPED){
			receiveValsString = netClient.inString.split(",");
			Log.i(TAG, "Vals Received: " + receiveValsString[0] + "," + receiveValsString[1]  + "," + receiveValsString[2]  + "," + receiveValsString[3]  + "," + receiveValsString[4]  + "," + receiveValsString[5]);
			for (int i=0; i < 10; i++) {
				receiveVals[i] = Float.parseFloat(receiveValsString[i]);
				Log.i(TAG, "FLOAT VALUE[" + i + "]: " + receiveVals[i]);
			}
		}
		
		return receiveVals;
	}
   
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      
      // ...
      
      
      
      boolean SEE_THRU = false;
      this.gl = gl;
      
      startTime = System.currentTimeMillis();
      fpsStartTime = startTime;
      numFrames = 0;
      

      
      // Define the lighting
      float lightAmbient[] = new float[] { 1.0f, 1.0f, 1.0f, 1 };
      float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
      float[] lightPos = new float[] { 0, 2, 3, 1 };
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
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

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
      gl.glEnable(GL10.GL_TEXTURE_2D);
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
      
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      float ratio = (float) width / height;
      
      
      if(mainActivity.stage == MainActivity.IDLE || mainActivity.stage == MainActivity.RENDER_CIRCLES){
		   
		   gl.glDisable(GL10.GL_TEXTURE_2D);
		  // GLU.gluOrtho2D(gl, 0, width, 0, height);
		   //used to be 17.5
		   GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 
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
				  mainActivity.stage = 4;
		      }
	   }
	   else if(mainActivity.stage == MainActivity.RENDER_MAPPED || mainActivity.stage == MainActivity.FINAL_RENDERING){
		   if (mainActivity.stage == MainActivity.RENDER_MAPPED) setValues(parseData());
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
		   gl.glEnable(GL10.GL_TEXTURE_2D);
		   GLU.gluPerspective(gl, 17.0f, ratio, 0.1f, 1000f); 	      
		   GLU.gluLookAt(gl, 10.0f, -10.0f, 12.0f, -2.0f, 2.0f, 0.0f, 0.0f, 0.0f, 1.0f);

		   // Clear the screen to black
		   gl.glClear(GL10.GL_COLOR_BUFFER_BIT
				   | GL10.GL_DEPTH_BUFFER_BIT);

		   // Position model so we can see it
		   gl.glMatrixMode(GL10.GL_MODELVIEW);
		   gl.glLoadIdentity();
      
		   for(Object curObject: objects){
			   if(curObject.draw){
				   gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[curObject.texNum]);
				   curObject.draw(gl);
			   }
		   }
	   }
      
      netClient.messageReady = false;
   }
   
   public void setValues(float[] vals){
	   if (vals != null && vals.length > 5){
		   eyeX = vals[1];
		   eyeY = vals[2];
		   eyeZ = vals[3];
		   centerX = vals[4];
		   centerY = vals[5];
		   centerZ = vals[6];
		   upX = vals[7];
		   upY = vals[8];
		   upZ = vals[9];
	   }
   }
   
   public int loadTexture(String fileName){
	   Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Textures/" + fileName);
	   
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
		
		return numTextures++;
   }
   
   public int loadObject(String fileName){
	      try {
	    	  objects.add(factory.loadObject(fileName, context));
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
   
   public void playAnimation(int objNum, int aniNum, int times, int duration){
	   Object obj;
	   if(objNum < numObjects){
		   obj = objects.get(objNum);
		   obj.aniNum = aniNum;
		   obj.aniTimesToPlay = -1;
		   obj.aniDuration = 10.0f;
	   }
   }
}
