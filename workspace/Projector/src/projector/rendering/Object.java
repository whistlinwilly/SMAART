package projector.rendering;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Object {
	
	int uid;
	int classifiedType;
	float x, y, theta;
	Surface[] surfaces;
	FloatBuffer vertices, normals;
	ByteBuffer indexBuffer;
	//ByteBuffer tempIndex;
	FloatBuffer textures;
	boolean draw = false;
	int texNum;
	int aniNum;
	int aniTimesToPlay;
	float aniDuration;

	public Object(int uid) {
		this.uid = uid;
	      // Setup index-array buffer. Indices in byte.

	}
	
//	public void drawAllSurfaces(GL10 gl){
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
//		
//		
//		
//		for(Surface curSurface: surfaces){
//		//	indexBuffer = ByteBuffer.allocateDirect(curSurface.indices.length);
//		 //   indexBuffer.put(curSurface.indices);
//		    indexBuffer.position(0);
//		      
//		    gl.glDrawElements(GL10.GL_LINE_STRIP, curSurface.indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
//		}
//	//	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
//	//	tempIndex = ByteBuffer.allocateDirect()
//	}
	
	public void draw(GL10 gl) { 
		
		

		
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Set the face rotation
	//	gl.glFrontFace(GL10.GL_CW);
		

		
		
	      
	    //  gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textures);
	      
	      for(int i = 0; i < surfaces.length; i++){
	    	  
	  		// Point to our buffers
	  		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, surfaces[i].vertices);
	  		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, surfaces[i].textures);
	    	  
	    	  gl.glDrawElements(GL10.GL_TRIANGLES, 3, GL10.GL_UNSIGNED_BYTE, surfaces[i].index);
	      
	      }
	      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	     gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

	      
	     // byte[] indices = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
	     // byte[] indices = { 0, 6, 4, 0, 2, 6, 0, 3, 2, 0, 1, 3, 2, 7, 6, 2, 3, 7, 4, 6, 7, 4, 7, 5, 0, 4, 5, 0, 5, 1, 1, 5, 7, 1, 7, 3};
	      //byte[] indices = new byte[vertices.capacity()];
	      
	      //int[] newIndex = new int[indices.length];
	      
	    //  indexBuffer = ByteBuffer.allocateDirect(indices.length);
	      
	    //  for(int i = 0; i < indices.length; i++)
	    //	  indexBuffer.put(indices);
	      
	      
	    //  indexBuffer.put(indices);
	      
	    //  indexBuffer.position(0);
	      
	    //  int x = indices.length;
	      
	      
	      

//	      gl.glColor4f(0, 0, 1, 1);
//	      gl.glNormal3f(0, 0, 1);
//	      gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertices.capacity());
	     
	   }
	
	public void setTexture(int x){
		texNum = x;
	}

}
