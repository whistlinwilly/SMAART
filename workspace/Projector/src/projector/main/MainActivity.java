package projector.main;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import projector.client.NetClient;
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
	public static final int FINAL_RENDERING = 3;
	public static final int RUN = 4;
	public static final int STOP = 5;
	public static final int SUNRISE = 6;
	public static final int STORM = 7;

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
      netClient = new NetClient("10.0.1.2", 6881);
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
	   
	 int aniNum = view.renderer.loadAnimation("test2.dae");
	 int birdAni = view.renderer.loadAnimation("birdAni.dae");
	 int house = view.renderer.loadObject("stuccohouse.obj");
	 int grass = view.renderer.loadObject("complexgrass.obj");
	 int bird0 = view.renderer.loadObject("bird.obj");
	 int bird1 = view.renderer.loadObject("bird.obj");
	 int bird2 = view.renderer.loadObject("bird.obj");
	//int siteObj = view.renderer.loadObject("SportsCube.obj");
	 int blackTex = view.renderer.loadTexture("black.bmp");
	 int whiteTex = view.renderer.loadTexture("white.bmp");
     int houseTex = view.renderer.loadTexture("stuccosprite.bmp");
     int grassTex = view.renderer.loadTexture("grass.bmp");
     int rainGrassTex = view.renderer.loadTexture("raingrass.bmp");
     int rainGrass1Tex = view.renderer.loadTexture("raingrass2.bmp");
     int rainGrass2Tex = view.renderer.loadTexture("raingrass3.bmp");
     int birdTex = view.renderer.loadTexture("bird.bmp");
     
//     int siteTex2 = view.renderer.loadTexture("Site2.bmp");
//     int siteTex3 = view.renderer.loadTexture("Site3.bmp");
     if(house >= 0){
    	 view.renderer.setObjectTexture(house, houseTex);
   	  	 view.renderer.show(house);
     }
     if(grass >= 0){
    	 view.renderer.setObjectTexture(grass, grassTex);
    	 view.renderer.show(grass);
     }
     if(bird0 >=0){
    	 projector.rendering.Object oBird = view.renderer.objects.get(bird0);
    	 oBird.x = -5.0f;
    	 oBird.y = -2.0f;
    	 oBird.z = 0.1f;
    	 oBird.theta = 60.0f;
    	 view.renderer.setObjectTexture(bird0, birdTex);
     }
     if(bird1 >=0){
    	 projector.rendering.Object oBird = view.renderer.objects.get(bird1);
    	 oBird.x = 0.0f;
    	 oBird.y = -6.5f;
    	 oBird.z = 0.1f;
    	 view.renderer.setObjectTexture(bird1, birdTex);
     }
     if(bird2 >=0){
    	 projector.rendering.Object oBird = view.renderer.objects.get(bird2);
    	 oBird.x = 5.0f;
    	 oBird.y = -3.0f;
    	 oBird.z = 0.1f;
    	 oBird.theta = 90.0f;
    	 view.renderer.setObjectTexture(bird2, birdTex);
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
