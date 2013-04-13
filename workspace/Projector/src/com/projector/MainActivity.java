package com.projector;

import java.io.IOException;

import projector.client.NetClient;
import projector.rendering.GLView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
	
	public volatile GLView mGLView;
	public boolean mainGotMessage;
	private NetClient netClient;
	private static final String TAG = "NetClient";
	public volatile int stage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//initialize things
		stage = 0;
		
		//start client networking
		try {
			//starts the networking thread
			netClient = new NetClient("10.0.1.187", 6881);
			
			mGLView = new GLView(this, this, netClient);
			mGLView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			mGLView.setKeepScreenOn(true);
			setContentView(mGLView);
			
			netClient.execute(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		   


		 int aniNum = mGLView.renderer.loadAnimation("test2.dae");
	    int siteObj = mGLView.renderer.loadObject("3cube_uv2.obj");
		//int siteObj = view.renderer.loadObject("SportsCube.obj");
	     int siteTex = mGLView.renderer.loadTexture("ninesprite.bmp");
	     int siteTex2 = mGLView.renderer.loadTexture("Site2.bmp");
	     int siteTex3 = mGLView.renderer.loadTexture("Site3.bmp");
	     if(siteObj >= 0){
	    	 mGLView.renderer.setObjectTexture(siteObj, siteTex);
	   	  	 mGLView.renderer.show(siteObj);
	     }
	   
	 
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mGLView.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
	}

}
