

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Shape3D;

/**
 * @author mclarceny
 * Will draw a grid with as many divisions as specified. To be used with the Identity Shader
 */
public class Grid extends Shape3D{

	private float [] verts;
	private float [] colors;
	private int [] vaoID=new int[1];
	private float divs; 				// number of divisions in grid
	private int numLines;
	
	public Grid(int divisions, GL3 gl){
		divs= (float)divisions;
		numLines=(int)((divs+1)*2+1);
		init();
		/*
		 * create vao
		 */
		gl.glGenVertexArrays(1, vaoID, 0);
		gl.glBindVertexArray(vaoID[0]);
		int [] bufferIDs = new int[2];
		gl.glGenBuffers(2, bufferIDs,0);
		/*
		 *  set up vertex position data
		 */
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[0]);
		FloatBuffer vertBuf= FloatBuffer.wrap(verts);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(IdentityLocs.getVertLoc());
		gl.glVertexAttribPointer(IdentityLocs.getVertLoc(), 3, GL.GL_FLOAT, false, 0,0);
		/*
		 * color buffer setup
		 */
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[1]);
		FloatBuffer colorBuf=FloatBuffer.wrap(colors);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, colorBuf.limit()*4, colorBuf, GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(IdentityLocs.getColorLoc());
		gl.glVertexAttribPointer(IdentityLocs.getColorLoc(), 4, GL.GL_FLOAT, false, 0,0);
		
		gl.glBindVertexArray(0); //deactivate vao
	}// end constructor

	@Override
	public void draw(GLAutoDrawable arg0) {
		GL3 gl= (GL3) arg0.getGL();
		MatrixStack mv= GeometryTransformPipeline.getModelViewMatrixStack();
		mv.pushMatrix();
		  mv.multMatrix(this.getTransform());									//get grids transfom and append on to the model veiw
		  Matrix3D mvMat=mv.peek();												// get mv matrix
		  double[] mvVals = mvMat.getValues();
		  float[] mvValsf= new float[mvVals.length];
		  for(int i=0; i<mvVals.length;i++) mvValsf[i]=(float) mvVals[i];		//convert to float
		  //System.out.println("ModelView :"+mvMat);
		  gl.glUniformMatrix4fv(IdentityLocs.getMVLoc(), 1,false, mvValsf,0);	//set model view in shader
		  gl.glBindVertexArray(vaoID[0]);
		  gl.glDrawArrays(GL.GL_LINES, 0,numLines*2);
		  gl.glBindVertexArray(0);
	    mv.popMatrix();
		
	}
	
	/**
	 * Initializes model vertex geometry and colors
	 */
	private void init(){
		int numVerts=(int)(numLines*2); // +1 is for Y axis
		verts= new float[numVerts*3];
		colors= new float[numVerts*4];
		float increment=1/(divs/2);
		int current_index=0;
		/*
		 * calculate horizontal lines
		 */
		for (float i=1; i>-1; i=i-increment ){
										//first point
			verts[current_index]=-1f;   //x
			verts[current_index+1]=0;   //y
			verts[current_index+2]=i;   //z
			current_index+=3;
										//second point
			verts[current_index]=1f;    //x
			verts[current_index+1]=0;   //y
			verts[current_index+2]=i;   //z
			current_index+=3;
		}
		/*
		 * calculate verticle lines
		 */
		for (float i=1; i>-1; i=i-increment ){
										//first point
			verts[current_index]=i;     //x
			verts[current_index+1]=0;   //y
			verts[current_index+2]=-1f; //z
			current_index+=3;
										//second point
			verts[current_index]=i;     //x
			verts[current_index+1]=0;   //y
			verts[current_index+2]=1f;  //z
			current_index+=3;
			}
		
		/*
		 *  y-axis line 
		 */
		verts[current_index]=1f;   	  //x
		verts[current_index+1]=0; 	  //y
		verts[current_index+2]=1f;    //z
		current_index+=3;
		verts[current_index]=1f;      //x
		verts[current_index+1]=1;     //y
		verts[current_index+2]=1f;    //z
		
		/*
		 * colors:
		 * X axis red
		 * y axis green
		 * z axis blue
		 * all other lines grey
		 */
		for(int i=0; i<numVerts*4;i+=4){
			if(i==4){
			  colors[i]=1f;
			  colors[i+1]=0f;
			  colors[i+2]=0f;
			  colors[i+3]=1f;
			}
			else if (i==(numVerts/2)*4){
				colors[i]=0f;
				colors[i+1]=1f;
				colors[i+2]=0f;
				colors[i+3]=1f;	
			}
			else if(i==(numVerts*4-8)){
				
				
				colors[i]=0f;
				colors[i+1]=0f;
				colors[i+2]=1f;
				colors[i+3]=1f;
			}
			else{
				colors[i]=.5f;
				colors[i+1]=.5f;
				colors[i+2]=.5f;
				colors[i+3]=1f;
			}
				
		}
				
	}// end init
}
