package projector.client;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.microedition.khronos.opengles.GL10;

import projector.main.MainActivity;
import projector.rendering.GLRenderer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class NetClient extends AsyncTask<MainActivity, MainActivity, MainActivity>{

    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 1000000;
    
    private static final String TAG = "NetClient";
	   
    
    public volatile Socket socket = null;
    public String host = null;
    public int port;
    public volatile int command;
    public boolean connected;
    public byte[] inBuffer = new byte[BUFFER_SIZE];
    public volatile String inString = null;
    public String outString;
    public byte[] outBuffer = new byte[1];
    
    public volatile boolean isListening;
    public volatile boolean messageReady;
    private boolean waitingToReceive = true;
    
    private float[] receiveVals = new float[10];
    private String[] receiveValsString;
	private int bytesReceived;
	GLRenderer glr;
    /**
     * Constructor with Host, Port and MAC Address
     * @param host
     * @param port
     * @param macAddress
     * @throws InterruptedException 
     */
    public NetClient(String host, int port){
        this.host = host;
        this.port = port;
        connected = false;
        isListening = false;
        
    }

    public void connectWithServer(MainActivity activity) throws InterruptedException, IOException {
    		try {	
    			InetAddress serverAddr = InetAddress.getByName(this.host);
            
	            //connect to server (table) socket
	            socket = new Socket(serverAddr, this.port);
	            socket.setTcpNoDelay(true);
	            
	            
	            if(socket != null){
	            	connected = true;
	            	glr = activity.view.renderer;
	            	Log.i(TAG, "Socket Created");
	            }
	            
	            //remove when making network sweep
	            else System.exit(0);
	
	        } catch (Exception e) {
	        	//couldn't connect, so retry
				socket.close();
				connected = false;
	        }
 
    }

    
    public void parseReceived(MainActivity activity){
    	
    	String[] slody = inString.split(",");
    	String commandString = slody[0];
    	String application = "";
    	command = Integer.parseInt(commandString);
    	if (slody.length > 1 && command == MainActivity.RUN){
    		application = slody[1];
    	}
    	Log.w("slody!", "" + slody[0] + "inString is" + inString);
    	

    	//if we are running our current app
		if (activity.stage == MainActivity.RUN){
			
			//run colorchanging demo
			if (application.equals("colorChange")) glr.application = GLRenderer.COLORCHANGING;
			
			//run house demo
			else if (application.equals("houseDemo")) glr.application = GLRenderer.HOUSE;
			
			//run alex demo
			else if (application.equals("alexDemo")) glr.application = GLRenderer.ALEX;
			
			//RUN INIT AGAIN
			else if (application.equals("INIT")) {
				glr.eyeX = 0.0f;
				glr.eyeY = 0.0f;
				glr.eyeZ = 24.0f;
				glr.centerX = 0.0f;
				glr.centerY = 0.0f;
				glr.centerZ = 0.0f;
				glr.upX = 0.0f;
				glr.upY = 1.0f;
				glr.upZ = 0.0f;
				glr.perspectiveSet = false;
				activity.stage = MainActivity.RENDER_CIRCLES;
			}
			
			//QUIT EVERYTHING
			else if (application.equals("QUIT")){
	       		if (connected) {
	    			Log.i(TAG, "Socket is closing...");
	    			try {
						socket.close();
						isListening = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    		else {
	    			Log.e(TAG, "Socket was already closed!");
	    		}
	       		System.exit(0);
	    	}
			
			//CLEAR CURRENT APPLICATION
			else if (application.equals("CLEAR")){
				glr.application = -1;
			}
			
		}
    	else {
    		glr.application = -1;
    		activity.stage = command;
    	}
    	
    	
    		
    }
    
    public void receiveMessageFromServer(MainActivity activity){
    	try {
			if((bytesReceived = socket.getInputStream().read(inBuffer)) != 0){
				
				//set waiting to receive to false
				waitingToReceive = false;
				Log.i("BytesReceived", "" + bytesReceived);
				//get the received string and tell the main thread its ready
				inString = "";
				inString = new String(inBuffer, 0, bytesReceived);
				
				parseReceived(activity);
			}
			else {
				Log.i(TAG, "Waiting on message...");
			}
			
			
    	} catch (Exception e) {
    	}
    }
    
    
    
    public void sendMessageToServer(String outString){
    	Log.i(TAG, "Sending to Server: " + outString);
    	this.outBuffer = outString.getBytes();
    	try {
			socket.getOutputStream().write(outBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i(TAG, "Output sent to server!");
    }

/*	@Override
	public void run() {
		try {
			connectWithServer();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

    @Override
	protected void onPostExecute(MainActivity params){
    }

	@Override
	protected MainActivity doInBackground(MainActivity... params) {
			try {
				connectWithServer(params[0]);
				
		    	//while connected to server
				isListening = true;
		        while (isListening) {
		        	if (socket.isConnected()){
		        		if(!waitingToReceive){
		        			sendMessageToServer("CONFIRM");
		        			waitingToReceive = true;
		        		}
		        		else
		        		receiveMessageFromServer(params[0]);
		        	}
		        }
		        
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return params[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return params[0];
	}


}

