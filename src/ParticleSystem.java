

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Random;

import graphicslib3D.GLSLUtils;
import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;

public class ParticleSystem extends Shape3D {
	private String [] varyings={"type1", "position1","velocity1", "age1"}; // names of variables to be used in transform feedback buffer
	private boolean isFirst=true;
	private int [] particleBuffer= new int[2];
	private int [] transformFeedback= new int[2];
	private boolean isPaused=false;
	
	private ShaderProgram update;
	private ShaderProgram render;
	private RandomTexture randomTexture;
	private float[] particles; 
	/*particle data stored in specific format represented by 8 floats
	// 1) type of particle 0=launcher 1= primary shell 2= secondary shell
	// 2-4) vec3 particle position
	// 5-7) vec3 particle velocity
	// 8) lifetime
	*/
	private int currentVertexBuff=0; // buffers are swapping so we need to keep track of which one we are writing to and which one we are reading from
	private int currentFeedbackBuff=1;
	
	private Texture texture;
	private UpdateLocs updateLocs;
	private RenderLocs renderLocs;
	//private float time=0;
	//private float timePassed=3;
	private int limit;
	private int[] queryID= new int[1];
	private IntBuffer buf = IntBuffer.allocate(1024);
	private Matrix3D proj= new Matrix3D();
	private long currentTime=0;
	private long theDate=new Date().getTime();
	
	
	public ParticleSystem(GLAutoDrawable draw, Texture tex, int size){
		GL3 gl= (GL3) draw.getGL();
		randomTexture= new RandomTexture(1000, gl);
		texture= tex;
		particles= new float[size*8];
		initLauncher();
		//initCloud();
		FloatBuffer particleBuff= FloatBuffer.wrap(particles);
		limit=particleBuff.limit()*4;
		gl.glGenTransformFeedbacks(2, transformFeedback,0);
		gl.glGenBuffers(2,particleBuffer, 0);
		
		for (int i = 0; i < 2 ; i++) {
	        gl.glBindTransformFeedback(GL3.GL_TRANSFORM_FEEDBACK, transformFeedback[i]);
	        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particleBuffer[i]);
	        gl.glBufferData(GL3.GL_ARRAY_BUFFER, particleBuff.limit()*4, particleBuff, GL3.GL_DYNAMIC_DRAW);
	        gl.glBindBufferBase(GL3.GL_TRANSFORM_FEEDBACK_BUFFER, 0, particleBuffer[i]);
	    }
		
		// create update program
		update= new ShaderProgram();
		update.addShader("Shaders/update.vert", ShaderProgram.VERTEX_SHADER);
		update.addShader("Shaders/update.geom", ShaderProgram.GEOMETRY_SHADER);
		update.compileProgram(draw);
		// must be done before linking
		gl.glTransformFeedbackVaryings(update.getProgramID(), 4, varyings, GL3.GL_INTERLEAVED_ATTRIBS);
		update.linkProgram(draw);
		updateLocs = new UpdateLocs(gl, update.getProgramID());
		
		this.setUpdateUniforms(gl);
		
		render= new ShaderProgram();
		render.addShader("Shaders/render.vert", ShaderProgram.VERTEX_SHADER);
		render.addShader("Shaders/render.geom", ShaderProgram.GEOMETRY_SHADER);
		render.addShader("Shaders/render.frag", ShaderProgram.FRAGMENT_SHADER);
		render.compileProgram(draw);
		render.linkProgram(draw);
		renderLocs=new RenderLocs(gl, render.getProgramID());
		
		gl.glGenQueries(1, queryID,0);
		
		
	}// constructor
	
	private void setRenderUniforms(GL3 gl){
		gl.glUseProgram(render.getProgramID());
		gl.glUniform1f(renderLocs.getParticleSizeLoc(), 0.006f); // set particle size
		gl.glUniform1i(renderLocs.getColorMapLoc(), 1); // set texture unit for firework
	}
	
	private void setUpdateUniforms(GL3 gl){
		gl.glUseProgram(update.getProgramID());
		gl.glUniform1f(updateLocs.getTimeLoc(), 0.0f);
		gl.glUniform1f(updateLocs.getTimePassedLoc(), 0.0f);
		gl.glUniform1f(updateLocs.getSecondaryShellLifetime(),500.0f);
		gl.glUniform1f(updateLocs.getLauncherLifetimeLoc(), 01.0f);   			//speed at which particles are launched
		gl.glUniform1f(updateLocs.getShellLifetimeLoc(), 2000.0f);
		
		randomTexture.bind(gl, GL3.GL_TEXTURE2);
		gl.glUniform1i(updateLocs.getRandomTexLoc(), 2);// texture unit one
	}
	
	private void update(GL3 gl){
		gl.glUseProgram(update.getProgramID());
		//set time locs
	    float t=(float)this.getDeltaMilli();
	    //System.out.println("Delta= "+t);
	    //System.out.println("Now= "+(float)currentTime);
		gl.glUniform1f(updateLocs.getTimePassedLoc(), t);
		gl.glUniform1f(updateLocs.getTimeLoc(), (float)currentTime);
		OpenGLFrame.errorCheck(gl,"before random bind");
		randomTexture.bind(gl, GL3.GL_TEXTURE2);
		OpenGLFrame.errorCheck(gl,"after random bind");
		gl.glEnable(GL3.GL_RASTERIZER_DISCARD); // disable rest of the pipeline
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particleBuffer[currentVertexBuff]);
		gl.glBindTransformFeedback(GL3.GL_TRANSFORM_FEEDBACK,transformFeedback[currentFeedbackBuff]);
		
	    gl.glEnableVertexAttribArray(0); // ints correspond to layout locations in shaders
	    gl.glEnableVertexAttribArray(1);
	    gl.glEnableVertexAttribArray(2);
	    gl.glEnableVertexAttribArray(3);
	    
	    gl.glVertexAttribPointer(0, 1, GL3.GL_FLOAT, false, 4*8 , 0);         // type
	    gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 4*8,  4);         // position
	    gl.glVertexAttribPointer(2, 3, GL3.GL_FLOAT, false, 4*8, 16);         // velocity
	    gl.glVertexAttribPointer(3, 1, GL3.GL_FLOAT, false, 4*8, 28);         // lifetime
	    
	    gl.glBeginTransformFeedback(GL3.GL_POINTS);
	    
	    if( isFirst){
	    	
	    	gl.glDrawArrays(GL3.GL_POINTS, 0, particles.length); // draw initialized launcher on first run
	    	
	    	isFirst=false;
	    	OpenGLFrame.errorCheck(gl,"isFirst");
	    }
	    else{
	    	OpenGLFrame.errorCheck(gl,"xxx");
	    	
	    	//gl.glBeginQuery(GL3.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN, queryID[0]);
	    	gl.glDrawTransformFeedback(GL3.GL_POINTS, transformFeedback[currentVertexBuff]);
	    	//gl.glEndQuery(GL3.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN);
	    	//gl.glGetQueryObjectiv(queryID[0], GL3.GL_QUERY_RESULT, buf);
	    	//System.out.println("Q: "+buf.get());
	    	//FloatBuffer data=FloatBuffer.allocate(1024);
	    	//gl.glGetBufferSubData(GL3.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 1024, data);

		    gl.glDisableVertexAttribArray(0);
	    	OpenGLFrame.errorCheck(gl,"mid");
	    }
	    
	    gl.glEndTransformFeedback();

	    gl.glDisableVertexAttribArray(0);
	    gl.glDisableVertexAttribArray(1);
	    gl.glDisableVertexAttribArray(2);
	    gl.glDisableVertexAttribArray(3);
	    //gl.glBindBuffer(GL3.GL_ARRAY_BUFFER,0);
	    gl.glDisable(GL3.GL_RASTERIZER_DISCARD);
		
	}
	
	public void draw(GL3 gl, Point3D camPos, Vector3D camUp){
		//time+=timePassed;
	
		//System.out.println("C "+currentTime);
		OpenGLFrame.errorCheck(gl,"before update");
		this.update(gl);
		OpenGLFrame.errorCheck(gl,"after update");
		gl.glUseProgram(render.getProgramID());
		
	    float [] camVals= new float[3];
	    camVals[0]=(float)camPos.getX();
	    camVals[1]=(float)camPos.getY();
	    camVals[2]=(float)camPos.getZ();
		gl.glUniform3fv(renderLocs.getCamPosLoc(),1, camVals,0 );
		
		float [] camUpVals= new float[3];
		camUpVals[0]=(float) camUp.getX();
		camUpVals[1]=(float) camUp.getY();
		camUpVals[2]=(float) camUp.getZ();
		gl.glUniform3fv(renderLocs.getCamUpLoc(),1, camUpVals,0 );
		
		//Matrix3D mv= GeometryTransformPipeline.getModelViewMatrix();
		MatrixStack mv= GeometryTransformPipeline.getModelViewMatrixStack();
		mv.pushMatrix();
			
			mv.multMatrix(this.getTransform());
			Matrix3D mvMat=mv.peek();
	   
			OpenGLFrame.errorCheck(gl,"HERE");
			
			double[]  mvVals= mvMat.getValues();										//get mvp matrix
			float[] mvValsf= new float[mvVals.length];
			for(int i=0; i<mvVals.length;i++)mvValsf[i]=(float) mvVals[i];			//convert to floats
			gl.glUniformMatrix4fv(renderLocs.getMVPLoc(), 1,false, mvValsf,0);		//send projection matrix to shader
			
			double[]  projVals= proj.getValues();									//get mvp matrix
			float[] projValsf= new float[projVals.length];
			for(int i=0; i<projVals.length;i++)projValsf[i]=(float) projVals[i];	//convert to floats
			gl.glUniformMatrix4fv(renderLocs.getProjLoc(), 1,false, projValsf,0); 	//send projection matrix to shader
			
			setRenderUniforms(gl);
			
			Matrix3D normMat=GeometryTransformPipeline.getNormalMatrix(true);		//get the normal matrix
			double[] normVals= normMat.getValues();
			float[] normValsFloat= new float[normVals.length];
			for(int i=0; i< normVals.length;i++) normValsFloat[i]= (float) normVals[i];
			gl.glUniformMatrix4fv(renderLocs.getNormalMatLoc(), 1,false, normValsFloat,0);
	
			//System.out.println("HHH"+ mv);
			gl.glActiveTexture(GL3.GL_TEXTURE1);
		    texture.bind(gl);
		    gl.glUniform1i(renderLocs.getColorMapLoc(), 1);
		    
		    gl.glDisable(GL3.GL_RASTERIZER_DISCARD);
		    
		    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particleBuffer[currentFeedbackBuff]);    
	
		    //enable and tell open gl what attribute arrays that you want access to 
		    //in the vertex shader inlcuding layout positions
		    gl.glEnableVertexAttribArray(0);
		    gl.glEnableVertexAttribArray(1);
	
		    
		    gl.glVertexAttribPointer(0, 1, GL3.GL_FLOAT, false, 4*8 , 0);         // type
		    gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 4*8,  4);         // position
		    
		    //gl.glBeginQuery(GL3.GL_PRIMITIVES_GENERATED, queryID[0]);
		    gl.glDrawTransformFeedback(GL3.GL_POINTS, this.transformFeedback[this.currentFeedbackBuff]);
		    //FloatBuffer data=FloatBuffer.allocate(1024);
	    	//gl.glGetBufferSubData(GL3.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 1024, data);
		    //gl.glEndQuery(GL3.GL_PRIMITIVES_GENERATED);
	    	//gl.glGetQueryObjectiv(queryID[0], GL3.GL_QUERY_RESULT, buf);
	    	//System.out.println("R: "+buf.get());
	    	//System.out.println(data.get());
	    	
		mv.popMatrix();
	    
	    if(currentFeedbackBuff==0){
	    	currentFeedbackBuff=1;
	    	currentVertexBuff=0;
	    }
	    else{
	    	currentFeedbackBuff=0;
	    	currentVertexBuff=1;
	    }
		
		
	}
	
	public void setProjectionMatrix(Matrix3D m){
		proj=(Matrix3D) m.clone();
		//System.out.println("Clone : "+ proj);
	}
	
	private void initLauncher(){
		particles[0]= 0.0f; // type
		particles[1]= 0.0f; // x pos
		particles[2]= 0.0f; // y pos 
		particles[3]= 0.0f; // z pos
		particles[4]= 0.0f; // x vel
		particles[5]= 0.0001f; // y vel
		particles[6]= 0.0f; // z vel
		particles[7]= 0.0f; // lifetime
		
		/*particles[8]= 0.0f; // type
		particles[9]= 0.0f; // x pos
		particles[10]= 0.01f; // y pos 
		particles[11]= 0.01f; // z pos
		particles[12]= 0.0f; // x vel
		particles[13]= 0.0001f; // y vel
		particles[14]= 0.0f; // z vel
		particles[15]= 0.0f; // lifetime    */
	}
	
	private void initCloud(){
		Random rand=new Random();
		for(int i=0; i<particles.length;i+=8){
			particles[i]= 1.0f; // type
			particles[i+1]= rand.nextFloat()/10; // x pos
			particles[i+2]= rand.nextFloat()/10; // y pos 
			particles[i+3]= rand.nextFloat()/10; // z pos
			particles[i+4]= rand.nextFloat()/100000; // x vel
			particles[i+5]= rand.nextFloat()/100000; // y vel
			particles[i+6]= rand.nextFloat()/100000; // z vel
			particles[i+7]= 100000.0f; // lifetime
		}
	}
	
	private long getDeltaMilli(){
		if(!isPaused){
			long now=new Date().getTime();
			long delta=now-theDate;
			currentTime+=delta;
			theDate=now;
			return delta;
		}else return 0;
	}

	@Override
	public void draw(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void setPaused(boolean pause){
		isPaused=pause;
	}
	
	public int getRenderProgramId(){
		return render.getProgramID();
	}
	

}
