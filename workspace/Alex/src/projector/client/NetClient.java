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
    	
    	int command;
    	String[] slody = inString.split(",");
    	String commandString = slody[0];
    	command = Integer.parseInt(commandString);
    	
    	Log.w("slody!", "" + slody[0] + "inString is" + inString);
    	


    	if (command == MainActivity.STOP){
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
    	}
    	//stop this projector (should exit thread and close socket)
    	else if(command == MainActivity.NONE){
    		
    		glr.hide(glr.pCirculation);
    		glr.hide(glr.pContours);
    		glr.hide(glr.pDoubles);
    		glr.hide(glr.pHeadHouse);
    		glr.hide(glr.pHousekeeping);
    		glr.hide(glr.pSingles);
    		glr.hide(glr.pSkylight);
    		glr.hide(glr.pTriples);
    		
    		
    		int[] testAni = {glr.pFrame1, glr.pFrame2, glr.pFrame3,glr.pFrame4,  glr.pFrame5,glr.pFrame6,
    				glr.pFrame7, glr.pFrame8, glr.pFrame9, glr.pFrame10, glr.pFrame11
    		};
    		
    		float[] durations = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
    		
    		glr.show(glr.pAnimationShell);
    		glr.playTextureAnimation(glr.pAnimationShell, testAni, durations, 20, 20.0f);
    	}
    	else if(command == MainActivity.HEADHOUSE){
    		glr.hide(glr.pAnimationShell);
    		glr.show(glr.pCirculation);
    		glr.show(glr.pContours);
    		glr.show(glr.pDoubles);
    		glr.show(glr.pHeadHouse);
    		glr.show(glr.pHousekeeping);
    		glr.show(glr.pSingles);
    		glr.show(glr.pSkylight);
    		glr.show(glr.pTriples);
    		glr.setObjectTexture(glr.pCirculation, glr.blackTex);
    		glr.setObjectTexture(glr.pContours, glr.blackTex);
    		glr.setObjectTexture(glr.pDoubles, glr.blackTex);
    		//glr.setObjectTexture(glr.pHeadHouse, glr.blackTex);
    		glr.setObjectTexture(glr.pHousekeeping, glr.blackTex);
    		glr.setObjectTexture(glr.pSingles, glr.blackTex);
    		glr.setObjectTexture(glr.pTriples, glr.blackTex);
    		glr.setObjectTexture(glr.pSkylight, glr.blackTex);
    	}
    	else if(command == MainActivity.CICULATION){
    		glr.setObjectTexture(glr.pCirculation, glr.pCirculationTex);
    		glr.setObjectTexture(glr.pContours, glr.blackTex);
    	}
    	else if(command == MainActivity.CONTOURS){
    		glr.setObjectTexture(glr.pContours, glr.pContoursTex);
    		glr.setObjectTexture(glr.pHeadHouse, glr.blackTex);
    	}
    	else if(command == MainActivity.DOUBLE){
    		glr.setObjectTexture(glr.pSingles, glr.blackTex);
    		glr.setObjectTexture(glr.pDoubles, glr.pDoublesTex);
    	}
    	else if(command == MainActivity.SINGLE){
    		glr.setObjectTexture(glr.pCirculation, glr.blackTex);
    		glr.setObjectTexture(glr.pSingles, glr.pSinglesTex);
    	}
    	else if(command == MainActivity.SKYLIGHT){
    		glr.setObjectTexture(glr.pSkylight, glr.pSkylightTex);
    		glr.setObjectTexture(glr.pHousekeeping, glr.blackTex);
    	}
    	else if(command == MainActivity.TRIPLE){
    		glr.setObjectTexture(glr.pTriples, glr.pTriplesTex);
    		glr.setObjectTexture(glr.pDoubles, glr.blackTex);
    	}
    	else if(command == MainActivity.HOUSEKEEPING){
    		glr.setObjectTexture(glr.pHousekeeping, glr.pHousekeepingTex);
    		glr.setObjectTexture(glr.pTriples, glr.blackTex);
    	}
    		
    		
    		
    		
    		activity.stage = command;
    	
    	
    		
    }

    private void updateColorArray(MainActivity activity) {
		String[] indices = inString.split(",");
		
//		for(int i = 0; i < activity.view.renderer.colorValues.length; i++)
//			if(activity.view.renderer.colorValues[i] > 0)
//				activity.view.renderer.colorValues[i]-=10;
		int j;
		String index;
		for(int n = 1; n < indices.length; n++){
			index = indices[n];
			j = Integer.parseInt(index);
			activity.view.renderer.colorValues[j]+=80; 
		}
		
	//	inString = "";
		
		
	}

	public void parseData(String inString, MainActivity activity){
    	receiveValsString = inString.split(",");
		Log.i(TAG, "Vals Received: " + receiveValsString[0] + "," + receiveValsString[1]  + "," + receiveValsString[2]  + "," + receiveValsString[3]  + "," + receiveValsString[4]  + "," + receiveValsString[5]);
		for (int i=0; i < 9; i++) {
			receiveVals[i] = Float.parseFloat(receiveValsString[i]);
			Log.i(TAG, "FLOAT VALUE[" + i + "]: " + receiveVals[i]);
		}
		activity.view.renderer.setValues(receiveVals);
		waitingToReceive = false;
    }
    
    public void receiveMessageFromServer(MainActivity activity){
    	try {
    		int bytesReceived;
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

