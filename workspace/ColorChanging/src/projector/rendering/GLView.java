/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/

package projector.rendering;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class GLView extends GLSurfaceView {
   public final GLRenderer renderer;
   public final int Z_DIST = 0;
   public final int X_ANGLE = 1;
   public final int Y_ANGLE = 2;
   public final int Z_ANGLE = 3;
   public final int X_EYE = 4;
   public final int Y_EYE = 5;
   public final int Z_EYE = 6;
   public final int X_DIST = 7;
   public final int Y_DIST = 8;
   public final int X_TRANS = 9;
   public final int Y_TRANS = 10;
   public final int SCALE = 11;

   public GLView(Context context) {
      super(context);

      // Uncomment this to turn on error-checking and logging
      //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);

      renderer = new GLRenderer(context);
      setRenderer(renderer);
   }
   
   public void setValue(int switchCode, float value){
	   
	   switch(switchCode){
		   case Z_DIST:
			   renderer.zDist += value;
			   break;
		   case X_DIST:
			   renderer.xDist += value;
			   break;
		   case Y_DIST:
			   renderer.yDist += value;
			   break;
		   case X_ANGLE:
			   renderer.xAngle += value;
			   break;
		   case Y_ANGLE:
			   renderer.yAngle += value;
			   break;
		   case Z_ANGLE:
			   renderer.zAngle += value;
			   break;
		   case X_EYE:
			   renderer.eyeX += value;
			   break;
		   case Y_EYE:
			   renderer.eyeY += value;
			   break;
		   case Z_EYE:
			   renderer.eyeZ += value;
			   break;
		   case X_TRANS:
			   renderer.xTrans += value;
			   break;
		   case Y_TRANS:
			   renderer.yTrans += value;
			   break;
		   case SCALE:
			   renderer.scale += value;
			   break;
	   }
	   
   }

}
