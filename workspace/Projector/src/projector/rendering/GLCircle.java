/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/

package projector.rendering;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

class GLCircle {
   private final IntBuffer mVertexBuffer;
   
   
   private final IntBuffer mTextureBuffer;
   
   private final int numPoints;

   
   public GLCircle(float x, float y, float radius, int points) {
	   
	   numPoints = points + 2;
      
      int one = 65536;
      int half = one / 2;
      int[] vertices = new int[numPoints * 3];
      double c, s;
      
      vertices[0] = (int)(x*one);
      vertices[1] = (int)(y*one);
      vertices[2] = 0;
      
      for(int i=1; i < numPoints; i++){
    	  
    	  
    	  c = 1.0 * (i % points) / points * (2*Math.PI);
    	  s = 1.0 * (i % points) / points * (2 * Math.PI);
    	  
    	  vertices[i*3] =  (int)(x * one + one * radius * Math.cos(c));
    	  vertices[i*3 + 1] = (int)(y * one + one * radius * Math.sin(s));
    	  vertices[i*3 + 2] = (int)(0.0f * one);
    	  
      }
      
/*      c = 1.0 / points * (2*Math.PI);
	  s = 1.0 / points * (2 * Math.PI);
	  
	  vertices[points] =  x + (int)(one * Math.cos(c));
	  vertices[points + 1] = y + (int)(one * Math.sin(s));
	  vertices[points +  2] = 0;*/

      
      
      int texCoords[] = {
            // FRONT
            0, one, one, one, 0, 0, one, 0,
            // BACK
            one, one, one, 0, 0, one, 0, 0,
            // LEFT
            one, one, one, 0, 0, one, 0, 0,
            // RIGHT
            one, one, one, 0, 0, one, 0, 0,
            // TOP
            one, 0, 0, 0, one, one, 0, one,
            // BOTTOM
            0, 0, 0, one, one, 0, one, one, };
      

      
      // Buffers to be passed to gl*Pointer() functions must be
      // direct, i.e., they must be placed on the native heap
      // where the garbage collector cannot move them.
      //
      // Buffers with multi-byte data types (e.g., short, int,
      // float) must have their byte order set to native order
      ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
      vbb.order(ByteOrder.nativeOrder());
      mVertexBuffer = vbb.asIntBuffer();
      mVertexBuffer.put(vertices);
      mVertexBuffer.position(0);
      

      
      // ...
      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
      tbb.order(ByteOrder.nativeOrder());
      mTextureBuffer = tbb.asIntBuffer();
      mTextureBuffer.put(texCoords);
      mTextureBuffer.position(0);
      
   }
   

   
   




public void draw(GL10 gl) { 
      gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
      
      
//      gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, mTextureBuffer);
      
      

      gl.glColor4f(0, 0, 1, 1);
      gl.glNormal3f(0, 0, 1);
      gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numPoints);
      /*gl.glNormal3f(0, 0, -1);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

      gl.glColor4f(1, 0, 1, 1);
      gl.glNormal3f(-1, 0, 0);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
      gl.glNormal3f(1, 0, 0);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

      gl.glColor4f(1, 0, 1, 1);
      gl.glNormal3f(0, 1, 0);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
      gl.glNormal3f(0, -1, 0);
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);*/
   }
   
   
   static void loadTexture(GL10 gl, Context context, int resource) {
      Bitmap bmp = BitmapFactory.decodeResource(
            context.getResources(), resource);
      GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
      gl.glTexParameterx(GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
      gl.glTexParameterx(GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
      bmp.recycle();
   }
   
}

