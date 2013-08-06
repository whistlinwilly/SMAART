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

public class FastObjectFactory {
	
	// Number of objects inflated by this factory
	// also used to set uid of new objects
	int numObjects;
	//int totalVertices = 0;
	
	String state, dir;
	
	List<Float> vertices, textures, normals;
	List<Surface> newSurfaces;
	List<Object> allObjects;

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
	
	public FastObjectFactory(String defaultDir) {
		
		numObjects = 0;
		state = Environment.getExternalStorageState();
		dir = defaultDir;
		vertices = new ArrayList<Float>();
		textures = new ArrayList<Float>();
		normals = new ArrayList<Float>();
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

							
							vertices.add(Float.parseFloat(parts.nextToken()));
							vertices.add(Float.parseFloat(parts.nextToken()));
							vertices.add(Float.parseFloat(parts.nextToken()));
							
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
							}
							

							String face = parts.nextToken();
							String numbers[] = face.split("/");
							vertexIndex1 = Integer.parseInt(numbers[0]);
							textureIndex1 = Integer.parseInt(numbers[1]);
							normalIndex1 = Integer.parseInt(numbers[2]);
							
							face = parts.nextToken();
							numbers = face.split("/");
							vertexIndex2 = Integer.parseInt(numbers[0]);
							textureIndex2 = Integer.parseInt(numbers[1]);
							normalIndex2 = Integer.parseInt(numbers[2]);
							
							face = parts.nextToken();
							numbers = face.split("/");
							vertexIndex3 = Integer.parseInt(numbers[0]);
							textureIndex3 = Integer.parseInt(numbers[1]);
							normalIndex3 = Integer.parseInt(numbers[2]);
							
							//Log.w("Object Factory", "Found New Tri Face " + vertexIndex1 + "/" + vertexIndex2 + "/" + vertexIndex3);
							
							vertexIndex1--;
							vertexIndex2--;
							vertexIndex3--;
							
							float x1 = vertices.get(3 * vertexIndex1);
							float y1 = vertices.get(3 * vertexIndex1 + 1);
							float z1 = vertices.get(3 * vertexIndex1 + 2);
							float x2 = vertices.get(3 * vertexIndex2);
							float y2 = vertices.get(3 * vertexIndex2 + 1);
							float z2 = vertices.get(3 * vertexIndex2 + 2);
							float x3 = vertices.get(3 * vertexIndex3);
							float y3 = vertices.get(3 * vertexIndex3 + 1);
							float z3 = vertices.get(3 * vertexIndex3 + 2);
							
							float avgX = (x1 + x2 + x3) / 3.0f;
							float avgY = (y1 + y2 + y3) / 3.0f;
							float avgZ = (z1 + z2 + z3) / 3.0f;
							
							newSurface = new Surface(avgX, avgY, avgZ);
							
					/*		ByteBuffer bb = ByteBuffer.allocateDirect(3 * 3 * 4); //3 points * 3 floats each * sizeof(float)
							bb.order(ByteOrder.nativeOrder());
							newSurface.vertices = bb.asFloatBuffer();
							

							
							newSurface.vertices.put(x1);
							newSurface.vertices.put(y1);
							newSurface.vertices.put(z1);
							newSurface.vertices.put(x2);
							newSurface.vertices.put(y2);
							newSurface.vertices.put(z2);
							newSurface.vertices.put(x3);
							newSurface.vertices.put(y3);
							newSurface.vertices.put(z3);
							newSurface.vertices.position(0);
							
							*/
							
				/*			ByteBuffer tbb = ByteBuffer.allocateDirect(2 * 3 * 4);
							tbb.order(ByteOrder.nativeOrder());
							newSurface.textures = tbb.asFloatBuffer();
					*/		
							textureIndex1--;
							textureIndex2--;
							textureIndex3--;
							
							Float test1 = textures.get(2 * textureIndex1);
							Float test2 = textures.get(2 * textureIndex1 + 1);
							Float test3 = textures.get(2 * textureIndex2);
							Float test4 = textures.get(2 * textureIndex2 + 1);
							Float test5 = textures.get(2 * textureIndex3);
							Float test6 = textures.get(2 * textureIndex3 + 1);
							Float poop = test1 + test2 + test3 + test4 + test5 + test6;
							
				/*			newSurface.textures.put(textures.get(2 * textureIndex1));
							newSurface.textures.put(textures.get(2 * textureIndex1 + 1));
							newSurface.textures.put(textures.get(2 * textureIndex2));
							newSurface.textures.put(textures.get(2 * textureIndex2 + 1));
							newSurface.textures.put(textures.get(2 * textureIndex3));
							newSurface.textures.put(textures.get(2 * textureIndex3 + 1));
							newSurface.textures.position(0);
							*/
							
					/*		ByteBuffer nbb = ByteBuffer.allocateDirect(3 * 3 * 4);
							nbb.order(ByteOrder.nativeOrder());
							newSurface.normals = nbb.asFloatBuffer();
						*/	
							normalIndex1--;
							normalIndex2--;
							normalIndex3--;
							
							x1 = normals.get(3 * normalIndex1);
							y1 = normals.get(3 * normalIndex1 + 1);
							z1 = normals.get(3 * normalIndex1 + 2);
							x2 = normals.get(3 * normalIndex2);
							y2 = normals.get(3 * normalIndex2 + 1);
							z2 = normals.get(3 * normalIndex2 + 2);
							x3 = normals.get(3 * normalIndex3);
							y3 = normals.get(3 * normalIndex3 + 1);
							z3 = normals.get(3 * normalIndex3 + 2);
							
							avgX = (x1 + x2 + x3) / 3.0f;
							avgY = (y1 + y2 + y3) / 3.0f;
							avgZ = (z1 + z2 + z3) / 3.0f;
							
							float normalize = (float) Math.sqrt(Math.pow(avgX,2) + Math.pow(avgY, 2) + Math.pow(avgZ, 2));
							
							avgX /= normalize;
							avgY /= normalize;
							avgZ /= normalize;
							
							newSurface.setNormals(avgX, avgY, avgZ);
				/*			
							newSurface.normals.put(x1);
							newSurface.normals.put(y1);
							newSurface.normals.put(z1);
							newSurface.normals.put(x2);
							newSurface.normals.put(y2);
							newSurface.normals.put(z2);
							newSurface.normals.put(x3);
							newSurface.normals.put(y3);
							newSurface.normals.put(z3);
							newSurface.normals.position(0);

							newSurface.index = ByteBuffer.allocateDirect(indexArray.length);
							newSurface.index.put(indexArray);
							newSurface.index.position(0);
						*/
						newSurfaces.add(newSurface);
//						vertexIndex1 = 0;
//						vertexIndex2 = 0;
//						vertexIndex3 = 0;
//						textureIndex1 = 0;
//						textureIndex2 = 0;
//						textureIndex3 = 0;
//						normalIndex1 = 0;
//						normalIndex2 = 0;
//						normalIndex3 = 0;
						
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
				Log.w("OBJECT FACTORY", "Model Loading Time: " + (System.currentTimeMillis() - timeElapsed));
				
				return newObj;
						
			 
			}
		}
		return null;
		
	}

}
