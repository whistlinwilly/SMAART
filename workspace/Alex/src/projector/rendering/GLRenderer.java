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
import android.widget.Toast;

public class GLRenderer implements GLSurfaceView.Renderer {
   private static final String TAG = "GLRenderer";
   private final Context context;
   
   private final GLCircle bigCircle = new GLCircle(0,0,2,100);
   private final GLCircle smallCircle = new GLCircle(0,3,0.5f,100);
   
   boolean stoopidBoolean = false;
   boolean sillyCodyBoolean = false;
   
   float[] lightPathX = {26.452854f,26.532384f,26.550999f,26.509127f,26.407747f,26.248329f,26.032768f,25.763292f,25.442382f,25.072684f,24.656931f,24.197887f,23.698285f,23.160792f,22.587976f,21.982289f,21.346051f,20.681447f,19.990527f,19.275202f,18.537257f,17.778351f,17.000028f,16.203725f,15.39078f,14.562437f,13.71986f,12.864135f,11.996281f,11.117256f,10.22796f,9.329247f,8.421926f,7.506769f,6.584513f,5.655867f,4.721516f,3.782125f,2.838341f,1.890803f,0.940137f,-0.013033f,-0.968086f,-1.924394f,-2.881327f,-3.838243f,-4.794485f,-5.749377f,-6.702219f,-7.652287f,-8.598818f,-9.541015f,-10.478035f,-11.408984f,-12.332911f,-13.248798f,-14.155556f,-15.052015f,-15.936911f,-16.808881f,-17.666452f,-18.508026f,-19.331877f,-20.136135f,-20.918779f,-21.677628f,-22.41034f,-23.114407f,-23.787161f,-24.425788f,-25.027347f,-25.5888f,-26.107064f,-26.579061f,-27.001791f,-27.372422f,-27.688372f,-27.947403f,-28.147712f,-28.288001f};	   
   
   float[] lightPathY = {5.746264f,5.057488f,4.361776f,3.661976f,2.960881f,2.261167f,1.565337f,0.875679f,0.194244f,-0.477162f,-1.136985f,-1.783903f,-2.416815f,-3.03482f,-3.63719f,-4.223351f,-4.792858f,-5.345379f,-5.880669f,-6.39856f,-6.898942f,-7.381753f,-7.846971f,-8.294596f,-8.724652f,-9.137178f,-9.532218f,-9.909822f,-10.270041f,-10.612924f,-10.938514f,-11.246846f,-11.537949f,-11.811838f,-12.068521f,-12.307988f,-12.530221f,-12.735183f,-12.922823f,-13.093077f,-13.245859f,-13.381071f,-13.498594f,-13.598293f,-13.680011f,-13.743573f,-13.788787f,-13.815435f,-13.823281f,-13.812068f,-13.781517f,-13.731325f,-13.661168f,-13.570702f,-13.459559f,-13.327351f,-13.17367f,-12.998091f,-12.800171f,-12.579459f,-12.335494f,-12.067815f,-11.775965f,-11.459507f,-11.118029f,-10.751165f,-10.358608f,-9.940135f,-9.495634f,-9.025124f,-8.528794f,-8.007034f,-7.460464f,-6.889969f,-6.29672f,-5.68219f,-5.048165f,-4.396724f,-3.730211f,-3.0512f};
   
   float[] lightPathZ = {1.370194f,2.038106f,2.703301f,3.363135f,4.015095f,4.656861f,5.286339f,5.901692f,6.501346f,7.083988f,7.648556f,8.194213f,8.720332f,9.22646f,9.712299f,10.177676f,10.622525f,11.046861f,11.450767f,11.834373f,12.197849f,12.541391f,12.865211f,13.169532f,13.45458f,13.720582f,13.967759f,14.196326f,14.406485f,14.59843f,14.772339f,14.928378f,15.066696f,15.187428f,15.290692f,15.376589f,15.445205f,15.496607f,15.530846f,15.547957f,15.547957f,15.530846f,15.496607f,15.445205f,15.376589f,15.290692f,15.187428f,15.066696f,14.928378f,14.772339f,14.59843f,14.406485f,14.196326f,13.967759f,13.720582f,13.45458f,13.169532f,12.865211f,12.541391f,12.197849f,11.834373f,11.450766f,11.046861f,10.622525f,10.177676f,9.712299f,9.22646f,8.720332f,8.194214f,7.648555f,7.083988f,6.501346f,5.901692f,5.286338f,4.656861f,4.015095f,3.363135f,2.703302f,2.038106f,1.370194f};
   
   
   
   
   //objects
   public int pCirculation;
   public int pContours;
   public int pDoubles;
   public int pSingles;
   public int pTriples;
   public int pHeadHouse;
   public int pHousekeeping;
   public int pSkylight;
   public int pAnimationShell;
   public int siteMap;
   public int siteBase;
   
   public int awnings;
   public int glazing;
   public int grass;
   public int contours;
   public int rooms;
   public int skylights;
   
   public int awnTex;
   public int glazTex;
   public int grassTex;
   public int contoursTex;
   public int roomTex;
   public int skylightTex;
   
   //animations
   public int aSiteZoom;
   
   public int pFrame1;
   public int pFrame2;
   public int pFrame3;
   public int pFrame4;
   public int pFrame5;
   public int pFrame6;
   public int pFrame7;
   public int pFrame8;
   public int pFrame9;
   public int pFrame10;
   public int pFrame11;
   public int pFrame12;
   public int pFrame13;
   public int pFrame14;
   public int pFrame15;
   public int pFrame16;
   
   //textures
   public int pCirculationTex;
   public int pContoursTex;
   public int pDoublesTex;
   public int pSinglesTex;
   public int pTriplesTex;
   public int pHeadHouseTex;
   public int pHousekeepingTex;
   public int pSkylightTex;
   public int blackTex;
   public int siteTex;
   public int siteHighlightTex;
   public int siteBarsTex;
   public int siteRiverTex;
   public int siteStreetTex;
   public int whiteTex;
   public int greyTex;
   
   
   int numTriangles = 80;
   public int[] colorValues = new int[numTriangles * numTriangles * 2];
   
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
   private long elapsedTime;
   
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
   
   public float lightX = 0.0f;
   public float lightY = 0.0f;
   public float lightZ = -20.0f;
   public float lightBrightness = 0.0f;
   public float lightTheta = 0.0f;
   public volatile int lightStage = -1;
   
   float theta = 0.0f;
   
   float animationPoint = 0.0f;
   float animationDuration = 4.0f;
	
   private GL10 gl;
	
   private int numTextures = 0;
	
   ObjectFactory factory = new ObjectFactory("/Objects");
   AnimationFactory ani = new AnimationFactory("/Animations");
	
   /** The texture pointer */
   public int[] textures = new int[100];
   public MainActivity mainActivity;
   private int numAnimations = 0;
   
   int thisProjector;
   ViewFrustrum[] projectors;
private float alwaysFOV = 17.0f;
private float alwaysNearPlane = 2.0f;
private float alwaysFarPlane = 60.0f;
private int globalColor;
   
	public boolean perspectiveSet = false;
	private float globalScaleX = 1.0f;
	private float globalScaleY = 1.0f;
	private int scaleCounter = 0;
	private int lightCounter = 0;

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
      mainActivity.runOnUiThread(new Runnable() {
    	    public void run() {
    	        Toast.makeText(mainActivity, "FINISHED LOADING!!!!!", Toast.LENGTH_SHORT).show();
    	        mainActivity.playSound("ding.mp3");
    	    }
    	});
      
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
      
      elapsedTime = System.currentTimeMillis() - startTime;
      startTime = System.currentTimeMillis();
      
      //Log.i("STAGE", "" + mainActivity.stage);
      
      
      if(mainActivity.stage == MainActivity.IDLE || mainActivity.stage == MainActivity.RENDER_CIRCLES){
    	  if(mainActivity.stage == MainActivity.IDLE && sillyCodyBoolean == false){
    		  setProjector();
    		  sillyCodyBoolean = true;
    	  }

		  // GLU.gluOrtho2D(gl, 0, width, 0, height);
		   //used to be 17.5
    	  
    	//  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	  
	      float matAmbient[] = new float[] { 1, 1, 1, 1 };
	      float matDiffuse[] = new float[] { 1, 1, 1, 1 };
	      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
	            matAmbient, 0);
	      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
	            matDiffuse, 0);
    	  
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
	   else{
		   
		   if(mainActivity.stage == MainActivity.MATERIALS){
			    // Define the lighting
			   float lightAmbient[];
			   float lightDiffuse[];
			   float lightPos[];
			   
			   
			   if(lightCounter > 10){
			   		  lightAmbient = new float[]{0.05f, 0.05f, 0.05f, 1 };
				      lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1 };
			   }
			   else if(lightCounter > 70){
				   lightAmbient = new float[] { (80 - lightCounter) / 200.0f, (80 - lightCounter) / 200.0f, (80 - lightCounter) / 200.0f, 1 };
				   lightDiffuse = new float[] { (80 - lightCounter) / 10.0f, (80 - lightCounter) / 10.0f, (80 - lightCounter) / 10.0f, 1 };
			   }
			   else {
			      lightAmbient = new float[] { lightCounter / 200.0f, lightCounter / 200.0f, lightCounter / 200.0f, 1 };
			      lightDiffuse = new float[] { lightCounter / 10.0f, lightCounter / 10.0f, lightCounter / 10.0f, 1 };
			   }
			   
			   
			   
			      lightPos = new float[] { lightPathX[lightCounter], lightPathY[lightCounter], lightPathZ[lightCounter], 1 };
			      
			
			      gl.glEnable(GL10.GL_LIGHTING);
			      gl.glEnable(GL10.GL_LIGHT0);
			      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
			      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
			      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
			   
			   if(lightCounter < 79)
				   lightCounter++;
		   }
		   else{
		   
		    // Define the lighting
		      float lightAmbient[] = new float[] { 1.0f, 1.0f, 1.0f, 1 };
		      float lightDiffuse[] = new float[] { lightBrightness, lightBrightness, lightBrightness, 1 };
		      float[] lightPos = new float[] { lightX, lightY, lightZ, 1 };
		      gl.glEnable(GL10.GL_LIGHTING);
		      gl.glEnable(GL10.GL_LIGHT0);
		      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		   }
		      
			   if(mainActivity.stage == MainActivity.SITEMAPZOOM){
				   if(scaleCounter  < 60){
					   globalScaleX += 0.05f;
					   globalScaleY += 0.05f;
					   scaleCounter++;
				   }
			   }
			   if(mainActivity.stage == MainActivity.MODELBASE){
				   if(scaleCounter  > 0){
					   globalScaleX -= 0.05f;
					   globalScaleY -= 0.05f;
					   scaleCounter--;
				   }
			   }
		      

		      
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
   

		      
		   for(Object curObject: objects){
			   curObject.updateAnimationValues(elapsedTime);
			   if(curObject.draw){
				   gl.glActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
				   gl.glClientActiveTexture(GL10.GL_TEXTURE0 + curObject.texNum);
				   gl.glEnable(GL10.GL_TEXTURE_2D);
				   gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[curObject.texNum]);
				   gl.glPushMatrix();
				   
				      gl.glTranslatef(curObject.x,curObject.y, curObject.z);
				      gl.glRotatef(curObject.theta,0.0f,0.0f,1.0f);
				      gl.glScalef(globalScaleX, globalScaleY, 1.0f);
				      
				   curObject.draw(gl);
				   gl.glPopMatrix();
				   gl.glDisable(GL10.GL_TEXTURE_2D);
			   }
		   }
	   }
   
   netClient.messageReady = false;
   }
   
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
