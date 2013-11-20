

import java.nio.FloatBuffer;
import java.util.Random;
import javax.media.opengl.GL3;

/**
 * @author mclarceny
 * Creates a opnGL texture that stores random numbers. Enables random numbers to
 * be looked up inside shader code. The texture wraps so that any value can be looked
 * up outside the texture coordinate range(on't be clamped). Since values are stored in
 * RGB format, they all will be in the range (0,1).
 */
public class RandomTexture {
	private int[] textureID= new int[1];
	
	public RandomTexture(int size, GL3 gl){
		Random rand= new Random();
		float[] data= new float[size*3];
		/*
		 * fill array with random values
		 */
		for(int i=0; i<size*3;i++){
			data[i]=rand.nextFloat();
		}
		FloatBuffer buffer=FloatBuffer.wrap(data);
		gl.glGenTextures(1, textureID,0);
		gl.glBindTexture(GL3.GL_TEXTURE_1D,textureID[0]);
		gl.glTexImage1D(GL3.GL_TEXTURE_1D, 0, GL3.GL_RGB, size, 0, GL3.GL_RGB, GL3.GL_FLOAT, buffer);
		gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
	    gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
	    gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT); 
		
	    OpenGLFrame.errorCheck(gl, "end of RandomTexture");
	}//constructor
	
	public void bind(GL3 gl, int textureUnit){
		gl.glActiveTexture(textureUnit);
		gl.glBindTexture(GL3.GL_TEXTURE_1D, textureID[0]);
	}
}
