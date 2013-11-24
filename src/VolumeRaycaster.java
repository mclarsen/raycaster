import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;

import java.nio.FloatBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

/*
 * This class assumes that there is an identity shader loaded with static locations.
 * 
 */

public class VolumeRaycaster {
    public static int[] backFaceTextureID= new int[1];
    private int[] backFaceFrameBuff= new int [1];
    public static int[] volumeTextureID= new int[1];
    private ShaderProgram raycastShader;
    private int raycastProgID;
    private RaycastLocs rayLocs;
    private BoundingBox volumeBox;
    private int xDim;
    private int yDim;
    private int zDim;
    private int bitSize; // values are 8-bit ints 16-bit ...
    private boolean isBigEndian;
    private String fileName;

    private float scalarMax=0;
    private float scalarMin=1;
    
    
    private float upperCutoff=1;
    private float lowerCutoff=0;
    private float[] color= {1.0f,1.0f,1.0f};
    private float red=1;
    
	public VolumeRaycaster(GLAutoDrawable arg0, int screenHeight, int screenWidth,String fname,int xSize,int ySize,int zSize, int bitSize, boolean bigEndian )
	{
		xDim=xSize;
		yDim=ySize;
		zDim=zSize;
		this.bitSize=bitSize;
		isBigEndian= bigEndian;
		fileName=fname;
		
		GL3 gl = (GL3) arg0.getGL();
		
		//compile and link the shader
		raycastShader= new ShaderProgram();
		raycastShader.addShader("Shaders/raycast.vert", ShaderProgram.VERTEX_SHADER);
		raycastShader.addShader("Shaders/raycast.frag", ShaderProgram.FRAGMENT_SHADER);
		raycastShader.compileProgram(arg0);
		raycastShader.linkProgram(arg0);
		raycastProgID= raycastShader.getProgramID();
		gl.glUseProgram(raycastProgID);
		rayLocs= new RaycastLocs(gl,raycastProgID);
		
		volumeBox= new BoundingBox(gl, rayLocs);
		volumeBox.translate(0, 0, 0);
		volumeBox.scale(1, 1, 1);
		volumeBox.renderFrontFace(false);
		
		
		//Create Framebuffer to store the coordinates of the back face of the volume
				//inside of a texture
				//----------------------------------------------------------------------------
				//generate the framebuffer and the texture that will be rendered to.
				gl.glGenTextures (1, backFaceTextureID,0);
				gl.glGenFramebuffers (1, backFaceFrameBuff,0);
				
				int frameBuffID= backFaceFrameBuff[0];
				int backFaceTextID= backFaceTextureID[0];
				
				//bind them so we can set them up
				gl.glBindFramebuffer (GL3.GL_FRAMEBUFFER, frameBuffID);
				gl.glBindTexture (GL3.GL_TEXTURE_2D, backFaceTextID);
				
				//texture settings
				
				gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
				gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
				gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
				gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
				
				System.out.println("Width : "+screenHeight+" Height : "+screenWidth);
				gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA16F, screenWidth, screenHeight, 0, GL3.GL_RGBA, GL3.GL_FLOAT, null);
				//gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
				
				//tell the framebuffer to render to the backface texture and do colors (other options are rending the depth buffer for shadows)
				gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, backFaceTextID, 0);
				
				//check the status of the frame buff
				int frameCode=gl.glCheckFramebufferStatus (GL3.GL_FRAMEBUFFER);
				if (frameCode!=GL3.GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("ERROR creating frame buffer");
					if(frameCode==GL3.GL_FRAMEBUFFER_UNDEFINED) System.out.println("Undefined");
					if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT ) System.out.println("Incomplete Attachment");
					if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT ) System.out.println("No Attatchmemt");
					if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER ) System.out.println("Incomplete Draw Buffer");
					if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER ) System.out.println("Incomplete ReadBuffer");
					if(frameCode==GL3.GL_FRAMEBUFFER_UNSUPPORTED ) System.out.println("Not supported");
					//if(frameCode==GL3.GL_FRAMEBUFFER_UNDEFINED) System.out.println("Undefined");
					
					
				}
				//unbind the framebuffer
				gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
				loadVolume(gl);
				
		
	}
	
	
	
	public void Draw(GLAutoDrawable arg0)
	{
		GL3 gl = (GL3) arg0.getGL();

		gl.glUseProgram(IdentityLocs.getProgID());
		
		//volumeBox.rotate(0, 0,.1);
		
		//render to the buffer
		gl.glBindFramebuffer (GL3.GL_FRAMEBUFFER, backFaceFrameBuff[0]);
		gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, backFaceTextureID[0], 0);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT|GL3.GL_DEPTH_BUFFER_BIT); 			// clear color and depth buffer
		volumeBox.renderFrontFace(false);
		volumeBox.draw(arg0);
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);							//undbind the renderbuffer
		
		
		// Now lets do some casting----------------------------------------------------------------------------
		gl.glUseProgram(raycastProgID);
		Matrix3D proj =GeometryTransformPipeline.getProjectionMatrixStack().peek();
		double[]  projVals= proj.getValues();									//get projection matrix
		float[] projValsf= new float[projVals.length];
		for(int i=0; i<projVals.length;i++)projValsf[i]=(float) projVals[i];	//convert to floats
		gl.glUniformMatrix4fv(rayLocs.getProjLoc(), 1,false, projValsf,0); //send projection matrix to shader
		
		gl.glUniform2f(rayLocs.getThreshLoc(), lowerCutoff, upperCutoff);
		gl.glUniform3f(rayLocs.getColorLoc(), color[0], color[1],color[2]); //glUniform3f  (rayLocs.getColorLoc(),color);
		//bind the two textures
		gl.glEnable(GL3.GL_BLEND);
		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
		gl.glActiveTexture(GL3.GL_TEXTURE1);
	    gl.glBindTexture( GL3.GL_TEXTURE_2D,  backFaceTextureID[0]);
	    
	    gl.glUniform1i(rayLocs.getColorMapLoc(), 1);
	    gl.glActiveTexture(GL3.GL_TEXTURE2);
	    gl.glBindTexture( GL3.GL_TEXTURE_3D,  volumeTextureID[0]);
	    gl.glUniform1i(rayLocs.getVolumeLoc(), 2);
	    
	    volumeBox.renderFrontFace(true);
	    volumeBox.draw(arg0);

	}
	
	public void setScale(float x, float y, float z){
		volumeBox.scale(x, y, z);
	}
	
	public void setUpperCutoff(float c){
		if (c>=0&&c<=1) this.upperCutoff=c;
	}
	
	public void setLowerCutoff(float c){
		if (c>=0&&c<=1) this.lowerCutoff=c;
	}
	public void setColor(float[] c){
		this.color[0]=c[0];
		this.color[1]=c[1];
		this.color[2]=c[2];
		//System.out.println(color[0]+" "+color[1]+" "+color[2]);
	}
	public void setRed(float r){
		this.red=r;
		System.out.println("Red "+r);
	}
	public float[] getColor(){return color;}
	private void loadVolume(GL3 gl){

		float data[] = RawReader.ReadRaw(bitSize, xDim, yDim, zDim, fileName,isBigEndian);
		for(int i=0;i<data.length;i++){
			if (data[i]>scalarMax) scalarMax=data[i];
			if(data[i]<scalarMin) scalarMin=data[i];
		}
		System.out.println("Scalars  : "+scalarMin+" - "+scalarMax);
		gl.glGenTextures(1, volumeTextureID,0);
		gl.glBindTexture(GL3.GL_TEXTURE_3D, volumeTextureID[0]);
		
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
		FloatBuffer buffer=FloatBuffer.wrap(data);
		gl.glTexImage3D(GL3.GL_TEXTURE_3D, 0,GL3.GL_RED, xDim, yDim,zDim,0, GL3.GL_RED,GL3.GL_FLOAT,buffer);
	}
}
