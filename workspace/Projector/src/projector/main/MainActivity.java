package projector.main;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import projector.client.NetClient;
import projector.rendering.GLRenderer;
import projector.rendering.GLView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	//commands that will be received from table
	public static final int IDLE = 0;
	public static final int RENDER_CIRCLES = 1;
	public static final int RENDER_MAPPED = 2;
	public static final int RUN = 3;
	public static final int STOP = 4;

	public volatile GLView view;
	public boolean mainGotMessage;
	private NetClient netClient;
	private static final String TAG = "NetClient";
	public volatile int stage;
	public MediaPlayer mp;
	
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      //initialize things
      stage = -1;
      netClient = new NetClient("10.0.1.5", 6881);
      view = new GLView(this);
      view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	  view.setKeepScreenOn(true);
      setContentView(view);
      view.renderer.mainActivity = this;
      view.renderer.netClient = this.netClient;
      mp = new MediaPlayer();
      netClient.execute(this);
      
   }

   @Override
   public void onStop(){
	   super.onStop();
		if (netClient.socket.isClosed()){
			Log.i(TAG, "Socket is Closed, App is exiting");
		}
		else {
			try {
				Log.i(TAG, "Socket was not closed, closing now and exiting...");
				netClient.socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		netClient.isListening = false;
		
		mp.stop();
   }
   
   @Override
   protected void onPause() {
       super.onPause();
       view.onPause();
       mp.stop();
   }

   @Override
   protected void onResume() {
       super.onResume();
       view.onResume();
   }
   
   
   public void init(){
	 
	  GLRenderer r = view.renderer;
	  
	 //////////////////
	 //COLOR CHANGING//
	 //////////////////
	  
	 r.triangle1 = r.loadObject("tri1.obj");
	 r.triangle2 = r.loadObject("tri2.obj");
	 r.setObjectTexture(r.triangle1, -1);
	 r.setObjectTexture(r.triangle2, -1);
	 
	 /////////
	 //HOUSE//
	 /////////
	/* 
	 r.bird = r.loadObject("bird.obj");
	 r.house = r.loadObject("stuccohouse.obj");
	 r.grass = r.loadObject("complexgrass.obj");
	 r.path = r.loadObject("path.obj");
	 r.birdTex = r.loadTexture("bird.bmp");
	 r.houseTex = r.loadTexture("stuccosprite.bmp");
	 r.grassTex = r.loadTexture("grass.bmp");
	 r.rainGrassTex1 = r.loadTexture("raingrass.bmp");
	 r.rainGrassTex2 = r.loadTexture("raingrass2.bmp");
	 r.rainGrassTex3 = r.loadTexture("raingrass3.bmp");
	 r.pathTex = r.loadTexture("brick.bmp");
	 r.whiteTex = r.loadTexture("white.bmp");
	 r.birdAni = r.loadAnimation("birdani.dae");
	 */
	 ////////
	 //ALEX//
	 ////////

	//load objects
	   r.pCirculation = r.loadObject("program_circulation.obj");
	   r.pContours = r.loadObject("program_contours_small.obj");
	   r.pDoubles = r.loadObject("program_double.obj");
	   r.pHeadHouse = r.loadObject("program_headhouse.obj");
	   r.pHousekeeping = r.loadObject("program_housekeeping.obj");
	   r.pSingles = r.loadObject("program_single.obj");
	   r.pSkylight = r.loadObject("program_skylight.obj");
	   r.pTriples = r.loadObject("program_triple.obj");
	   r.pAnimationShell = r.loadObject("aniSurface.obj");
	   
	   playSound("ding.mp3", true);
	   
	   //load textures
	   r.pCirculationTex = r.loadTexture("circulation.bmp");
	   r.pContoursTex = r.loadTexture("contours.bmp");
	   r.pDoublesTex = r.loadTexture("double.bmp");
	   r.pHeadHouseTex = r.loadTexture("headhouse.bmp");
	   r.pHousekeepingTex = r.loadTexture("laundry.bmp");
	   r.pSinglesTex = r.loadTexture("single.bmp");
	   r.pSkylightTex = r.loadTexture("skylight.bmp");
	   r.pTriplesTex = r.loadTexture("triple.bmp");
	   r.blackTex	= r.loadTexture("black.bmp");
	   
	   playSound("ding.mp3", true);
	   
	   //load frames for animation
	   /*
	   r.pFrame1 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00000.bmp");
	   r.pFrame2 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00001.bmp");
	   r.pFrame3 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00002.bmp");
	   r.pFrame4 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00003.bmp");
	   r.pFrame5 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00004.bmp");
	   r.pFrame6 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00005.bmp");
	   r.pFrame7 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00006.bmp");
	   r.pFrame8 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00007.bmp");
	   r.pFrame9 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00008.bmp");
	   r.pFrame10 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00009.bmp");
	   r.pFrame11 = r.loadTexture("Make2D--visible--lines--water-waterPath_row1_00010.bmp");
	   */
	   r.loadAnimationTextures("Make2D--visible--lines--water-waterPath_row1_00000.bmp", 11);
	   
	   if(r.pAnimationShell >= 0){
		   r.setObjectTexture(r.pAnimationShell, r.pframes[0]);
	   }
	   if(r.pCirculation >= 0){
		   r.show(r.pCirculation);
		   r.setObjectTexture(r.pCirculation, r.pCirculationTex);
	   }
	   if(r.pContours >= 0){
		   r.show(r.pContours);
		   r.setObjectTexture(r.pContours, r.pContoursTex);
	   }
	   if(r.pDoubles >= 0){
		   r.show(r.pDoubles);
		   r.setObjectTexture(r.pDoubles, r.pDoublesTex);
	   }
	   if(r.pHeadHouse >= 0){
		   r.show(r.pHeadHouse);
		   r.setObjectTexture(r.pHeadHouse, r.pHeadHouseTex);
	   }
	   if(r.pHousekeeping >= 0){
		   r.show(r.pHousekeeping);
		   r.setObjectTexture(r.pHousekeeping, r.pHousekeepingTex);
	   }
	   if(r.pSingles >= 0){
		   r.show(r.pSingles);
		   r.setObjectTexture(r.pSingles, r.pSinglesTex);
	   }
	   if(r.pSkylight >= 0){
		   r.show(r.pSkylight);
		   r.setObjectTexture(r.pSkylight, r.pSkylightTex);
	   }
	   if(r.pTriples >= 0){
		   r.show(r.pTriples);
		   r.setObjectTexture(r.pTriples, r.pTriplesTex);
	   }
	   playSound("ding.mp3", true);
   }

   public void playSound(String fileName, boolean forceSound){
	   if (forceSound){
		   if(mp.isPlaying()){
			   mp.stop();
		   }
		   try {
			   mp.reset();
			   mp.setDataSource(Environment.getExternalStorageDirectory() + "/Sounds/" + fileName);
			   mp.prepare();
			   mp.start();
		   } catch (Exception e){
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	   }
	   else if (!mp.isPlaying()){
		   try {
			   mp.reset();
			   mp.setDataSource(Environment.getExternalStorageDirectory() + "/Sounds/" + fileName);
			   mp.prepare();
			   mp.start();
		   } catch (Exception e){
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	   }
   }
}
