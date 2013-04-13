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

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NetClient extends AsyncTask<MainActivity, MainActivity, MainActivity>{

    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 100;
    
    private static final String TAG = "NetClient";
	   
    
    public volatile Socket socket = null;
    public String host = null;
    public int port;
    
    public boolean connected;
    public byte[] inBuffer= new byte[128];
    public volatile String inString = null;
    public String outString;
    public byte[] outBuffer = new byte[1];
    
    public volatile boolean isListening;
    public volatile boolean messageReady;
    private boolean waitingToReceive = true;
    
    private float[] receiveVals = new float[10];
    private String[] receiveValsString;
	
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

    
    public void parseReceived(String inString, MainActivity activity){
    	int command;
    	String commandString = new String(inString.substring(0, 1));
    	command = Integer.parseInt(commandString);
    	
    	if (command != MainActivity.STOP){
    		activity.stage = command;
    	}
    		
    	//stop this projector (should exit thread and close socket)
    	else { 
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
			if(socket.getInputStream().read(inBuffer) != 0){
				
				//set waiting to receive to false
				waitingToReceive = false;
				
				//get the received string and tell the main thread its ready
				inString = new String(inBuffer, 0, inBuffer.length);
				parseReceived(inString, activity);
				
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

