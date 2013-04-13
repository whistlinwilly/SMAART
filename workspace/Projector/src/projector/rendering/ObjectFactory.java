package projector.rendering;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ObjectFactory {
	
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
	
	public ObjectFactory(String defaultDir) {
		
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
		
		//SD Card is mounted
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			//File is .obj file
			if(fileName.contains(".obj")){
				Scanner input = new Scanner(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir,fileName));
				Log.w("Object Factory", "Opened File, Beginning to Parse");
				while(input.hasNextLine()){
					if(input.hasNext(Pattern.compile("v"))){
						if(!readingVertices){
							readingVertices = true;
							hasVertices = true;
							Log.w("Object Factory", "Found Vertex Section, Now Parsing");
						}
						//input.useDelimiter(" ");
						input.next();
						float x = Float.parseFloat(input.next());
						float y = Float.parseFloat(input.next());
						float z = Float.parseFloat(input.next());
						
						vertices.add(x);
						vertices.add(y);
						vertices.add(z);
						
						Log.w("Object Factory", "Added new vertex (" + x + "," + y + "," + z + ")");
					}
					else if(input.hasNext(Pattern.compile("vt"))){
						if(!readingTextures && readingVertices){
							Log.w("Object Factory", "Found Texture Section, Now Parsing");
							readingTextures = true;
							hasTextures = true;
						}
						input.next();
						
						float x = Float.parseFloat(input.next());
						float y = 1.0f - Float.parseFloat(input.next());
						
						textures.add(x);
						textures.add(y);
						
						Log.w("Object Factory", "Added new texture vertex (" + x + "," + y + ")");
					}
					else if(input.hasNext(Pattern.compile("vn"))){
						if(!readingNormals && readingVertices){
							Log.w("Object Factory", "Found Normal Section, Now Parsing");
							readingNormals = true;
							hasNormals = true;
						}
						input.next();
						
						float x = Float.parseFloat(input.next());
						float y = Float.parseFloat(input.next());
						float z = Float.parseFloat(input.next());
						
						normals.add(x);
						normals.add(y);
						normals.add(z);
						
						Log.w("Object Factory", "Added new normal vertex (" + x + "," + y + "," + z + ")");
					}
					else if(input.hasNext(Pattern.compile("f"))){
						if(!readingFaces){
							Log.w("Object Factory", "Found Face Section, Now Parsing");
							readingFaces = true;
						}
						
						input.next();
						String face = input.next();
						String numbers[] = face.split("/");
						if(numbers[0].length() > 0)
							vertexIndex1 = Integer.parseInt(numbers[0]);
						if(numbers[1].length() > 0)
							textureIndex1 = Integer.parseInt(numbers[1]);
						if(numbers[2].length() > 0)
							normalIndex1 = Integer.parseInt(numbers[2]);
						face = input.next();
						numbers = face.split("/");
						if(numbers[0].length() > 0)
							vertexIndex2 = Integer.parseInt(numbers[0]);
						if(numbers[1].length() > 0)
							textureIndex2 = Integer.parseInt(numbers[1]);
						if(numbers[2].length() > 0)
							normalIndex2 = Integer.parseInt(numbers[2]);
						face = input.next();
						numbers = face.split("/");
						if(numbers[0].length() > 0)
							vertexIndex3 = Integer.parseInt(numbers[0]);
						if(numbers[1].length() > 0)
							textureIndex3 = Integer.parseInt(numbers[1]);
						if(numbers[2].length() > 0)
							normalIndex3 = Integer.parseInt(numbers[2]);
						
						Log.w("Object Factory", "Found New Tri Face " + vertexIndex1 + "/" + vertexIndex2 + "/" + vertexIndex3);
						
						newSurface = new Surface();
						
						ByteBuffer bb = ByteBuffer.allocateDirect(3 * 3 * 4); //3 points * 3 floats each * sizeof(float)
						bb.order(ByteOrder.nativeOrder());
						newSurface.vertices = bb.asFloatBuffer();
						
						vertexIndex1--;
						vertexIndex2--;
						vertexIndex3--;
						
						newSurface.vertices.put(vertices.get(3 * vertexIndex1));
						newSurface.vertices.put(vertices.get(3 * vertexIndex1 + 1));
						newSurface.vertices.put(vertices.get(3 * vertexIndex1 + 2));
						newSurface.vertices.put(vertices.get(3 * vertexIndex2));
						newSurface.vertices.put(vertices.get(3 * vertexIndex2 + 1));
						newSurface.vertices.put(vertices.get(3 * vertexIndex2 + 2));
						newSurface.vertices.put(vertices.get(3 * vertexIndex3));
						newSurface.vertices.put(vertices.get(3 * vertexIndex3 + 1));
						newSurface.vertices.put(vertices.get(3 * vertexIndex3 + 2));
						newSurface.vertices.position(0);
						
						ByteBuffer tbb = ByteBuffer.allocateDirect(2 * 3 * 4);
						tbb.order(ByteOrder.nativeOrder());
						newSurface.textures = tbb.asFloatBuffer();
						
						textureIndex1--;
						textureIndex2--;
						textureIndex3--;
						
						newSurface.textures.put(textures.get(2 * textureIndex1));
						newSurface.textures.put(textures.get(2 * textureIndex1 + 1));
						newSurface.textures.put(textures.get(2 * textureIndex2));
						newSurface.textures.put(textures.get(2 * textureIndex2 + 1));
						newSurface.textures.put(textures.get(2 * textureIndex3));
						newSurface.textures.put(textures.get(2 * textureIndex3 + 1));
						newSurface.textures.position(0);

						newSurface.index = ByteBuffer.allocateDirect(indexArray.length);
						newSurface.index.put(indexArray);
						newSurface.index.position(0);
					
					newSurfaces.add(newSurface);
					vertexIndex1 = 0;
					vertexIndex2 = 0;
					vertexIndex3 = 0;
					textureIndex1 = 0;
					textureIndex2 = 0;
					textureIndex3 = 0;
					normalIndex1 = 0;
					normalIndex2 = 0;
					normalIndex3 = 0;
					}
					else if(input.hasNext(Pattern.compile("o"))){
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
							input.next();
						}
						else{
							Log.w("Object Factory", "Found starting object!");
							newObj = new Object(numObjects++);
							input.next();
						}
//						

					}
					else if(input.hasNext()){
						lineIsThis = input.next();
						Log.w("Object Factory", "GOT UNKNOWN LINE:" + lineIsThis);
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
					
					newObj.surfaces = new Surface[newSurfaces.size()];
					
					int i = 0;
					for(Surface curSurface: newSurfaces)
						newObj.surfaces[i++] = curSurface;
						break;
					}
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
				
				return newObj;
						
			 
			}
		}
		return null;
		
	}

}
