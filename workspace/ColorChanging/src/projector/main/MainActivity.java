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
	   
	 view.renderer.triangle1 = view.renderer.loadObject("tri1.obj");
	 view.renderer.triangle2 = view.renderer.loadObject("tri2.obj");
	 view.renderer.bird = view.renderer.loadObject("bird.obj");
	 view.renderer.house = view.renderer.loadObject("stuccohouse.obj");
	 view.renderer.grass = view.renderer.loadObject("complexgrass.obj");
	 view.renderer.path = view.renderer.loadObject("path.obj");
	 view.renderer.birdTex = view.renderer.loadTexture("bird.bmp");
	 view.renderer.houseTex = view.renderer.loadTexture("stuccosprite.bmp");
	 view.renderer.grassTex = view.renderer.loadTexture("grass.bmp");
	 view.renderer.rainGrassTex1 = view.renderer.loadTexture("raingrass.bmp");
	 view.renderer.rainGrassTex2 = view.renderer.loadTexture("raingrass2.bmp");
	 view.renderer.rainGrassTex3 = view.renderer.loadTexture("raingrass3.bmp");
	 view.renderer.pathTex = view.renderer.loadTexture("brick.bmp");
	 view.renderer.whiteTex = view.renderer.loadTexture("white.bmp");
	 view.renderer.blackTex = view.renderer.loadTexture("black.bmp");
	 view.renderer.birdAni = view.renderer.loadAnimation("birdani.dae");
	 view.renderer.setObjectTexture(view.renderer.triangle1, -1);
	 view.renderer.setObjectTexture(view.renderer.triangle2, -1);
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
