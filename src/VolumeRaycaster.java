import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

/*
 * This class assumes that there is an identity shader loaded with static locations.
 * 
 */

public class VolumeRaycaster {
	private OpenGLFrame theFrame;
    private ArrayList<TransferFunction> transferFunctions;
    private final static int maxTransferFunctions=5;
    public static int[] backFaceTextureID= new int[1];
    private int[] transferTextureUnits= {GL3.GL_TEXTURE3,GL3.GL_TEXTURE4,GL3.GL_TEXTURE5,GL3.GL_TEXTURE6,GL3.GL_TEXTURE7,GL3.GL_TEXTURE8};
    private int currentTransferFunction=0;
    private int numTransferFunctions=1;
    
    private int[] backFaceFrameBuff= new int [1];
    public static int[] volumeTextureID= new int[1];
    private int[] transferTextureID= new int[maxTransferFunctions+1];
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

    private boolean autoScale=true;
    private TransferFunction defaultTransferFunction;
    
    
	public VolumeRaycaster(GLAutoDrawable arg0, int screenHeight, int screenWidth,String fname,int xSize,int ySize,int zSize, int bitSize, boolean bigEndian,boolean autoScale , OpenGLFrame frame)
	{
		theFrame=frame;
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
				
			defaultTransferFunction= new TransferFunction(1000);
			float step=1/6.0f;
			float current=0;
			
			defaultTransferFunction.addRGBPegPoint(0.001f, 1, 0, 0);
			defaultTransferFunction.addRGBPegPoint(1f, 1f, 1f, 1f);
			defaultTransferFunction.addAlphaPegPoint(0.001f, 0f);
			defaultTransferFunction.addAlphaPegPoint(1f, 1f);
			
			//create the texutre
			float [] tData= defaultTransferFunction.getTransferArray();
			//System.out.println(tData.length);
			
			FloatBuffer buffer=FloatBuffer.wrap(tData);
			gl.glGenTextures(5, transferTextureID,0);				//generate all 5 textures
			gl.glBindTexture(GL3.GL_TEXTURE_1D,transferTextureID[0]);
			gl.glTexImage1D(GL3.GL_TEXTURE_1D, 0, GL3.GL_RGBA, tData.length/4, 0, GL3.GL_RGBA, GL3.GL_FLOAT, buffer);
			gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		    gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE); 
	}
	
	public void addTransferFuncton(TransferFunction tfunc,GL3 gl){
		if(numTransferFunctions<maxTransferFunctions)
		{
			float [] tData=tfunc.getTransferArray();
			FloatBuffer buffer=FloatBuffer.wrap(tData);

			gl.glBindTexture(GL3.GL_TEXTURE_1D,transferTextureID[numTransferFunctions]);
			gl.glTexImage1D(GL3.GL_TEXTURE_1D, 0, GL3.GL_RGBA, tData.length/4, 0, GL3.GL_RGBA, GL3.GL_FLOAT, buffer);
			gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		    gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			gl.glTexParameterf(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE); 
			numTransferFunctions++;
		}
		else System.out.println("Cannot add transfer function- too many");
	}
	
	public void nextTransferFunction(){
		currentTransferFunction++;
		if(currentTransferFunction==numTransferFunctions) currentTransferFunction=0;
	}
	
	public void Draw(GLAutoDrawable arg0)
	{
		GL3 gl = (GL3) arg0.getGL();

		gl.glUseProgram(IdentityLocs.getProgID());
		
		volumeBox.rotate(0, .1,0);
		
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
		//float[] camPos=theFrame.getCameraPosition();
		//gl.glUniform3f(rayLocs.getCamPosLoc(), camPos[0], camPos[1],camPos[2]);
		
		//bind the two textures
		gl.glEnable(GL3.GL_BLEND);
		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
		gl.glActiveTexture(GL3.GL_TEXTURE1);
	    gl.glBindTexture( GL3.GL_TEXTURE_2D,  backFaceTextureID[0]);
	    gl.glUniform1i(rayLocs.getColorMapLoc(), 1);
	    
	    gl.glActiveTexture(GL3.GL_TEXTURE2);
	    gl.glBindTexture( GL3.GL_TEXTURE_3D,  volumeTextureID[0]);
	    gl.glUniform1i(rayLocs.getVolumeLoc(), 2);
	    
	    gl.glActiveTexture(transferTextureUnits[currentTransferFunction]);
	    gl.glBindTexture( GL3.GL_TEXTURE_1D,  transferTextureID[currentTransferFunction]);
	    gl.glUniform1i(rayLocs.getTransferLoc(), currentTransferFunction+3);
	    
	    volumeBox.renderFrontFace(true);
	    volumeBox.draw(arg0);

	}
	
	public void setScale(float x, float y, float z){
		volumeBox.scale(x, y, z);
	}
	
	public void rotate(double x, double y, double z){
		volumeBox.rotate(x, y, z);
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
		System.out.println(color[0]+" "+color[1]+" "+color[2]);
	}
	
	public float[] getColor(){return color;}
	private void loadVolume(GL3 gl){

		float data[] = RawReader.ReadRaw(bitSize, xDim, yDim, zDim, fileName,isBigEndian);
		for(int i=0;i<data.length;i++){
			if (data[i]>scalarMax) scalarMax=data[i];
			if(data[i]<scalarMin) scalarMin=data[i];
		}
		//scale the data to eliminate un-used values
		if(autoScale){
			
			for(int i=0;i<data.length;i++){
				data[i]=data[i]/scalarMax;
			}
				//if (data[i]>scalarMax) scalarMax=data[i];
				//if(data[i]<scalarMin) scalarMin=data[i];
			
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
	
	public int getProgramID(){return this.raycastProgID;}
}
