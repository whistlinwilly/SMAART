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
	public static final int SITEMAP = 3;
	public static final int SITEMAPHIGHLIGHTED = 4;
	public static final int SITEMAPBARS = 5;
	public static final int SITEMAPZOOM = 6;
	public static final int SITEMAPRIVER = 7;
	public static final int SITEMAPSTREET = 8;
	public static final int MODELBASE = 9;
	public static final int NONE = 10;
	public static final int HEADHOUSE = 11;
	public static final int CONTOURS = 12;
	public static final int CICULATION = 13;
	public static final int SINGLE = 14;
	public static final int DOUBLE = 15;
	public static final int TRIPLE = 16;
	public static final int HOUSEKEEPING = 17;
	public static final int SKYLIGHT = 18;
	public static final int ANIMATION = 19;
	public static final int MATERIALS = 20;
	public static final int SUN = 21;
	public static final int STOP = 22;
	
	
	public static final int REINIT = 150;

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
      netClient = new NetClient("10.0.1.3", 6881);
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
	   
	   r.siteTex	=	r.loadTexture("site.bmp");
	   r.siteHighlightTex = r.loadTexture("sitehighlight.bmp");
	   r.siteBarsTex = r.loadTexture("sitebars.bmp");
	   r.siteRiverTex = r.loadTexture("siteriver.bmp");
	   r.siteStreetTex = r.loadTexture("siteroad.bmp");
	   
	   //load objects
	   r.siteMap = r.loadObject("sitemap.obj");
	   r.siteBase = r.loadObject("base.obj");
	   r.pCirculation = r.loadObject("program_circulation.obj");
	   r.pContours = r.loadObject("program_contours_small.obj");
	   r.pDoubles = r.loadObject("program_double.obj");
	   r.pHeadHouse = r.loadObject("program_headhouse.obj");
	   r.pHousekeeping = r.loadObject("program_housekeeping.obj");
	   r.pSingles = r.loadObject("program_single.obj");
	   r.pSkylight = r.loadObject("program_skylight.obj");
	   r.pTriples = r.loadObject("program_triple.obj");
	   r.pAnimationShell = r.loadObject("aniDec5.obj");
	   
	   //load object animations
	   r.aSiteZoom = r.loadAnimation("sitezoom2.dae");
	   
	   
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
	   r.whiteTex = r.loadTexture("white.bmp");
	   r.greyTex = r.loadTexture("lightGrey.bmp");
	   
	   //load frames for animation
	   r.pFrame1 = r.loadTexture("waterDiagram_joinedAnimationFrames_00000.bmp");
	   r.pFrame2 = r.loadTexture("waterDiagram_joinedAnimationFrames_00001.bmp");
	   r.pFrame3 = r.loadTexture("waterDiagram_joinedAnimationFrames_00002.bmp");
	   r.pFrame4 = r.loadTexture("waterDiagram_joinedAnimationFrames_00003.bmp");
	   r.pFrame5 = r.loadTexture("waterDiagram_joinedAnimationFrames_00004.bmp");
	   r.pFrame6 = r.loadTexture("waterDiagram_joinedAnimationFrames_00005.bmp");
	   r.pFrame7 = r.loadTexture("waterDiagram_joinedAnimationFrames_00006.bmp");
	   r.pFrame8 = r.loadTexture("waterDiagram_joinedAnimationFrames_00007.bmp");
	   r.pFrame9 = r.loadTexture("waterDiagram_joinedAnimationFrames_00008.bmp");
	   r.pFrame10 = r.loadTexture("waterDiagram_joinedAnimationFrames_00009.bmp");
	   r.pFrame11 = r.loadTexture("waterDiagram_joinedAnimationFrames_00010.bmp");
	   r.pFrame12 = r.loadTexture("waterDiagram_joinedAnimationFrames_00011.bmp");
	   r.pFrame13 = r.loadTexture("waterDiagram_joinedAnimationFrames_00012.bmp");
	   r.pFrame14 = r.loadTexture("waterDiagram_joinedAnimationFrames_00013.bmp");
	   r.pFrame15 = r.loadTexture("waterDiagram_joinedAnimationFrames_00014.bmp");
	   r.pFrame16 = r.loadTexture("waterDiagram_joinedAnimationFrames_00015.bmp");
	   
	   if(r.pAnimationShell >= 0){
		   r.setObjectTexture(r.pAnimationShell, r.pFrame1);
	   }
	   if(r.siteMap >= 0){
		   r.show(r.siteMap);
		   r.setObjectTexture(r.siteMap, r.siteTex);
	   }
	   if(r.siteBase >= 0){
		   r.setObjectTexture(r.siteBase, r.whiteTex);
	   }
	   if(r.pCirculation >= 0){
		  // r.show(r.pCirculation);
		   r.setObjectTexture(r.pCirculation, r.pCirculationTex);
	   }
	   if(r.pContours >= 0){
		//   r.show(r.pContours);
		   r.setObjectTexture(r.pContours, r.pContoursTex);
	   }
	   if(r.pDoubles >= 0){
		//   r.show(r.pDoubles);
		   r.setObjectTexture(r.pDoubles, r.pDoublesTex);
	   }
	   if(r.pHeadHouse >= 0){
		//   r.show(r.pHeadHouse);
		   r.setObjectTexture(r.pHeadHouse, r.pHeadHouseTex);
	   }
	   if(r.pHousekeeping >= 0){
		 //  r.show(r.pHousekeeping);
		   r.setObjectTexture(r.pHousekeeping, r.pHousekeepingTex);
	   }
	   if(r.pSingles >= 0){
		 //  r.show(r.pSingles);
		   r.setObjectTexture(r.pSingles, r.pSinglesTex);
	   }
	   if(r.pSkylight >= 0){
		//   r.show(r.pSkylight);
		   r.setObjectTexture(r.pSkylight, r.pSkylightTex);
	   }
	   if(r.pTriples >= 0){
		 //  r.show(r.pTriples);
		   r.setObjectTexture(r.pTriples, r.pTriplesTex);
	   }
	   

	     
   }

public void playSound(String fileName){
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

}
