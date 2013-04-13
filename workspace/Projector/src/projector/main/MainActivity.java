package projector.main;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import projector.client.NetClient;
import projector.rendering.GLView;

import android.app.Activity;
import android.os.Bundle;
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

	public volatile GLView view;
	public boolean mainGotMessage;
	private NetClient netClient;
	private static final String TAG = "NetClient";
	public volatile int stage;
	
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      //initialize things
      stage = 0;
      netClient = new NetClient("10.0.1.187", 6881);
      view = new GLView(this);
      view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	  view.setKeepScreenOn(true);
      setContentView(view);
      view.renderer.mainActivity = this;
      view.renderer.netClient = this.netClient;
      
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
   }
   
   @Override
   protected void onPause() {
       super.onPause();
       view.onPause();
   }

   @Override
   protected void onResume() {
       super.onResume();
       view.onResume();
   }
   
   
   public void init(){
	   
	 int aniNum = view.renderer.loadAnimation("test2.dae");
	 int siteObj = view.renderer.loadObject("3cube_uv.obj");
	//int siteObj = view.renderer.loadObject("SportsCube.obj");
     int siteTex = view.renderer.loadTexture("ninesprite.bmp");
     int siteTex2 = view.renderer.loadTexture("Site2.bmp");
     int siteTex3 = view.renderer.loadTexture("Site3.bmp");
     if(siteObj >= 0){
    	 view.renderer.setObjectTexture(siteObj, siteTex);
   	  	 view.renderer.show(siteObj);
     }
   }
}
