package projector.rendering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SingleAllocationFastObjectFactory {
	
	// Number of objects inflated by this factory
	// also used to set uid of new objects
	int numObjects;
	//int totalVertices = 0;
	
	String state, dir;
	
	List<Float> vertices, textures, normals;
	List<Surface> newSurfaces;
	List<Object> allObjects;
	List<Short> indices;

	boolean readingVertices = false;
	boolean readingTextures = false;
	boolean readingNormals = false;
	boolean readingFaces = false;
	
	boolean hasVertices  = false;
	boolean hasTextures = false;
	boolean hasNormals = false;
	
	byte[] indexArray = new byte[3];
	
	int numFaces = 0;
	
	
	boolean DEBUG = true;
	
	Object newObj;

	private String lineIsThis;
	
	private long timeElapsed;
	private long sinceLast = 0;
	private int numVertices = 0;
	private int numTexCoords = 0;
	private int numNormals = 0;
	public int numIndices = 0;
	
	
	public SingleAllocationFastObjectFactory(String defaultDir) {
		
		numObjects = 0;
		state = Environment.getExternalStorageState();
		dir = defaultDir;
		vertices = new ArrayList<Float>();
		textures = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		indices = new ArrayList<Short>();
		newSurfaces = new ArrayList<Surface>();
		allObjects = new ArrayList<Object>();
		indexArray[0] = (byte) 0;
		indexArray[1] = (byte) 1;
		indexArray[2] = (byte) 2;
	}
	
	public Object loadObject(String fileName, Context context) throws FileNotFoundException{
		
		String line = null;
		int vertexIndex1 = 0, vertexIndex2 = 0, vertexIndex3 = 0;
		int textureIndex1 = 0, textureIndex2 = 0, textureIndex3 = 0;
		int normalIndex1 = 0, normalIndex2 = 0, normalIndex3 = 0;
		Surface newSurface = null;
		timeElapsed = System.currentTimeMillis();
		sinceLast = System.currentTimeMillis();
		int numTokens = 0;
		
		//SD Card is mounted
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			//File is .obj file
			if(fileName.contains(".obj")){
				
				BufferedReader input = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir,fileName)));
				
				Log.w("Object Factory", "Opened File, Beginning to Parse");
				try {
					while((line = input.readLine()) != null) {
						
						if(line.length() == 0 || line.charAt(0) == '#')
							continue;
						
						StringTokenizer parts = new StringTokenizer(line, " ");
						numTokens = parts.countTokens();
						
						if(numTokens == 0)
							continue;
						
						String type = parts.nextToken();
						
						if(type.equals("v")){
							if(!readingVertices){
								readingVertices = true;
								hasVertices = true;
								//Log.w("Object Factory", "Found Vertex Section, Now Parsing");
							}

							numVertices++;
							
							float x = Float.parseFloat(parts.nextToken());
							float y = Float.parseFloat(parts.nextToken());
							float z = Float.parseFloat(parts.nextToken());
							
							vertices.add(x);
							vertices.add(z);
							vertices.add(y);
							
						//	Log.w("Object Factory", "Added new vertex (" + x + "," + y + "," + z + ")");
						}
						else if(type.equals("vt")){
							if(!readingTextures && readingVertices){
								sinceLast = System.currentTimeMillis() - sinceLast;
								Log.w("OBJECT FACTORY","Time to parse vertices: " + sinceLast);
								sinceLast = System.currentTimeMillis();
							//	Log.w("Object Factory", "Found Texture Section, Now Parsing");
								readingTextures = true;
								hasTextures = true;
							}
							
							numTexCoords++;
							textures.add(Float.parseFloat(parts.nextToken()));
							textures.add(1.0f - Float.parseFloat(parts.nextToken()));
							
						//Log.w("Object Factory", "Added new texture vertex (" + x + "," + y + ")");
						 
						 
						}
						else if(type.equals("vn")){
							if(!readingNormals && readingVertices){
								sinceLast = System.currentTimeMillis() - sinceLast;
								Log.w("OBJECT FACTORY","Time to parse textures: " + sinceLast);
								sinceLast = System.currentTimeMillis();
							//	Log.w("Object Factory", "Found Normal Section, Now Parsing");
								readingNormals = true;
								hasNormals = true;
							}
							
							numNormals++;
							normals.add(Float.parseFloat(parts.nextToken()));
							normals.add(Float.parseFloat(parts.nextToken()));
							normals.add(Float.parseFloat(parts.nextToken()));
							
							//Log.w("Object Factory", "Added new normal vertex (" + x + "," + y + "," + z + ")");

							 
						}
						else if(type.equals("f")){
							if(!readingFaces){
								sinceLast = System.currentTimeMillis() - sinceLast;
								Log.w("OBJECT FACTORY","Time to parse normals: " + sinceLast);
								sinceLast = System.currentTimeMillis();
							//	Log.w("Object Factory", "Found Face Section, Now Parsing");
								readingFaces = true;
								ByteBuffer bb = ByteBuffer.allocateDirect(numVertices * 3 * 4); //3 points * 3 floats each * sizeof(float)
								bb.order(ByteOrder.nativeOrder());
								newObj.vertices = bb.asFloatBuffer();
								
								for(int i = 0; i < numVertices; i++)
									newObj.vertices.put(vertices.get(i));
								
								newObj.vertices.position(0);
							}
							

							String face = parts.nextToken();
							String numbers[] = face.split("/");
							vertexIndex1 = Integer.parseInt(numbers[0]);
							
							face = parts.nextToken();
							numbers = face.split("/");
							vertexIndex2 = Integer.parseInt(numbers[0]);
							
							face = parts.nextToken();
							numbers = face.split("/");
							vertexIndex3 = Integer.parseInt(numbers[0]);
							
							//Log.w("Object Factory", "Found New Tri Face " + vertexIndex1 + "/" + vertexIndex2 + "/" + vertexIndex3);
							
							vertexIndex1--;
							vertexIndex2--;
							vertexIndex3--;
							
							
							newSurface = new Surface(0.0f, 0.0f, 0.0f);
							
							indices.add((short) vertexIndex1);
							indices.add((short) vertexIndex2);
							indices.add((short) vertexIndex3);		
							
							numIndices +=3;
			
						
						newSurfaces.add(newSurface);

						
						}
						else if(type.equals("o")){
							if(readingFaces){
//FOR MULTI OBJECT
//							Log.w("Object Factory", "Reached next group object:" + numObjects);
//							
//						    //Push vertices into buffer for opengl
//							ByteBuffer bb = ByteBuffer.allocateDirect(vertices.size() * 4);
//							bb.order(ByteOrder.nativeOrder());
//							newObj.vertices = bb.asFloatBuffer();
//							
//							
//						for(int i = 0; i < vertices.size(); i++)
//							newObj.vertices.put(vertices.get(i));
//						
//						newObj.vertices.flip();
//						
//						newObj.surfaces = new Surface[newSurfaces.size()];
//						
//						int i = 0;
//						for(Surface curSurface: newSurfaces)
//							newObj.surfaces[i++] = curSurface;
//						
//							allObjects.add(newObj);
//							
//						//Clean up for next object
//							vertices.clear();
//							textures.clear();
//							normals.clear();
//							newSurfaces.clear();
//							readingVertices = false;
//							readingTextures = false;
//							readingNormals = false;
//							readingFaces = false;
//							newObj = new Object(numObjects++);
//							
//							input.next();
//							
////							if(numObjects > 3)
////								break;
//													Log.w("Object Factory", "Reached next group object:" + numObjects);
								numObjects++;
								readingVertices = false;
								readingTextures = false;
								readingNormals = false;
								readingFaces = false;
								//input.next();
							}
							else{
								Log.w("Object Factory", "Found starting object!");
								newObj = new Object(numObjects++);
								//input.next();
							}
//						

						}
						else{
							//OLD VERSION OF FACE PARSING
//						Log.w("Object Factory", "Finished Parsing...");
//						ByteBuffer bb = ByteBuffer.allocateDirect(vertices.size() * 4);
//						bb.order(ByteOrder.nativeOrder());
//						newObj.vertices = bb.asFloatBuffer();
//						
//						
//					for(int i = 0; i < vertices.size(); i++)
//						newObj.vertices.put(vertices.get(i).floatValue());
//					newObj.vertices.position(0);
//					
//					
//					ByteBuffer tbb = ByteBuffer.allocateDirect(textures.size() * 4);
//					tbb.order(ByteOrder.nativeOrder());
//					newObj.textures = tbb.asFloatBuffer();
//					
//					
//					for(int i = 0; i < textures.size(); i++)
//						newObj.textures.put(textures.get(i).floatValue());
//					newObj.textures.position(0);
						
					//	newObj.textures.flip();
						
						}
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//For MULTI OBJECT
//				Log.w("Object Factory", "Found a total of " + allObjects.size() + " objects");
//				
//				Object[] toReturn = new Object[allObjects.size()];
//				
//				int j = 0;
//				
//				for(Object curObj: allObjects)
//					toReturn[j++] = curObj;
				
				newObj.surfaces = new Surface[newSurfaces.size()];
				
				int i = 0;
				for(Surface curSurface: newSurfaces)
					newObj.surfaces[i++] = curSurface;
				
				vertices = new ArrayList<Float>();
				textures = new ArrayList<Float>();
				normals = new ArrayList<Float>();
				newSurfaces = new ArrayList<Surface>();
				numFaces = 0;
				lineIsThis = null;
				readingVertices = false;
				readingTextures = false;
				readingNormals = false;
				readingFaces = false;
				
				hasVertices  = false;
				hasTextures = false;
				hasNormals = false;
				

				sinceLast = System.currentTimeMillis() - sinceLast;
				Log.w("OBJECT FACTORY","Time to parse faces: " + sinceLast);
				
				
				//COMMENTED OUT BECAUSE OF ERRORS
//				ByteBuffer dlb = ByteBuffer.allocateDirect(
//				        // (# of coordinate values * 2 bytes per short)
//				                numIndices * 2);
//				        dlb.order(ByteOrder.nativeOrder());
//				        newObj.indexBuffer = dlb.asShortBuffer();
//				        
//				        for(int x = 0; x < numIndices; x++)
//				        	newObj.indexBuffer.put(indices.get(x));
//				        
//				        newObj.indexBuffer.position(0);
//				
//				
//				Log.w("OBJECT FACTORY", "Model Loading Time: " + (System.currentTimeMillis() - timeElapsed));
//				
//				newObj.numIndices = numIndices;
				
				return newObj;
						
			 
			}
		}
		return null;
		
	}

}
