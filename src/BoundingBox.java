import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Shape3D;
import graphicslib3D.Vertex3D;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;



public class BoundingBox extends Shape3D{
	
	private float [] verts={.5f,.5f,.5f,    -.5f,.5f,.5f,    -.5f,-.5f,.5f,       .5f,.5f,.5f,    -.5f,-.5f,.5f,     .5f, -.5f,.5f,  // front face
						    .5f,.5f,-.5f,   -.5f,.5f,-.5f,    -.5f,.5f,.5f,        .5f,.5f,-.5f,    -.5f,.5f,.5f,      .5f,.5f,.5f,  //top face
						    .5f,-.5f,-.5f,  -.5f,-.5f,-.5f,   -.5f,.5f,-.5f,      .5f,-.5f,-.5f,    -.5f,.5f,-.5f,    .5f,.5f,-.5f, // back face
						    .5f,-.5f,.5f,   -.5f,-.5f,.5f,   -.5f,-.5f,-.5f,      .5f,-.5f,.5f,   -.5f,-.5f,-.5f,     .5f,-.5f,-.5f,// bottom face
						    .5f,.5f,-.5f,   .5f,.5f,.5f,     .5f,-.5f,.5f,        .5f,.5f,-.5f,    .5f,-.5f,.5f,      .5f,-.5f,-.5f,  //right face
						    -.5f,.5f,.5f,   -.5f,.5f,-.5f,   -.5f,-.5f,-.5f,      -.5f,.5f,.5f,   -.5f,-.5f,-.5f,     -.5f,-.5f,.5f};
	
	
	private float [] normals={0,0, 1,  0,0, 1,  0,0, 1,   0,0, 1,  0,0, 1,  0,0, 1, //front
			                 0,1,0,    0,1,0,   1,1,0,   0,1,0,    0,1,0,    0,1,0,  // top
			                 0,0,-1,   0,0,-1,  0,0,-1,  0,0,-1, 0,0,-1, 0,0,-1,     //back
			                 0,-1,0,  0,-1,0,  0,-1,0,   0,-1,0,   0,-1,0,   0,-1,0,  //bottom
			                 1,0,0,   1,0,0,   1,0,0,   1,0,0,    1,0,0,    1,0,0, //right
			                 -1,0,0,  -1,0,0,   -1,0,0,  -1,0,0,  -1,0,0,   -1,0,0};
	
	
	
	private float [] colors={ 1,1,1,1,  1,1,1,1,   1,1,1,1,    1,1,1,1,    1,1,1,1,    1,1,1,1,//front
							  1,0,1,1,  1,0,1,1,   1,0,1,1,    1,0,1,1,    1,0,1,1,    1,0,1,1,//top
							  0,1,1,1,  0,1,1,1,   0,1,1,1,    0,1,1,1,    0,1,1,1,    0,1,1,1,//back
							  1,1,0,1,  1,1,0,1,   1,1,0,1,    1,1,0,1,    1,1,0,1,    1,1,0,1,//bottom
							  1,1,1,1,  1,1,1,1,   1,1,1,1,    1,1,1,1,    1,1,1,1,    1,1,1,1,//right
							  0,0,1,1,  0,0,1,1,   0,0,1,1,    0,0,1,1,    0,0,1,1,    0,0,1,1};//left
	private int[] vaoID= new int[1];// vertex array object ID

	private float[] textCoords={1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f,
			   				    1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f,
			   				    1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f,
			   				    1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f,
			   				    1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f,
			   			    	1f,1f,        0f,1f,     0f,0f,    1f,1f,   0f,0f,  1f,0f};
	
	private boolean renderFront=true;
	
	
	public BoundingBox  (GL3 gl){
		
		

		//this.rotate(180f, new Vector3D(0,0,1)); //flip
		
		// create vao
			gl.glGenVertexArrays(1, vaoID, 0);
			gl.glBindVertexArray(vaoID[0]);
			int [] bufferIDs = new int[2]; //change this to add normals and the like
			gl.glGenBuffers(2, bufferIDs,0);// and this
			
			
			// set up vertex position data
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[0]);
			FloatBuffer vertBuf= FloatBuffer.wrap(verts);
			
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL.GL_STATIC_DRAW);
			gl.glEnableVertexAttribArray(IdentityLocs.getVertLoc());
			gl.glVertexAttribPointer(IdentityLocs.getVertLoc(), 3, GL.GL_FLOAT, false, 0,0);
			
			//color buffer setup
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[1]);
			FloatBuffer colorBuf=FloatBuffer.wrap(colors);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, colorBuf.limit()*4, colorBuf, GL.GL_STATIC_DRAW);
			gl.glEnableVertexAttribArray(IdentityLocs.getColorLoc());
			gl.glVertexAttribPointer(IdentityLocs.getColorLoc(), 4, GL.GL_FLOAT, false, 0,0);
			
			
			//NOTE: if you want to enable the colors and normals then gen more vaos
			//normal buff setup
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[2]);
//			FloatBuffer normalBuf=FloatBuffer.wrap(normals);
//			gl.glBufferData(GL.GL_ARRAY_BUFFER, normalBuf.limit()*4, normalBuf, GL.GL_STATIC_DRAW);
//			gl.glEnableVertexAttribArray(attLocs.getNormalLoc());
//			gl.glVertexAttribPointer(attLocs.getNormalLoc(), 3, GL.GL_FLOAT, false, 0,0);
//			
//			//textbuff setup
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[3]);
//			FloatBuffer textBuf=FloatBuffer.wrap(textCoords);
//			gl.glBufferData(GL.GL_ARRAY_BUFFER, textBuf.limit()*4, textBuf, GL.GL_STATIC_DRAW);
//			gl.glEnableVertexAttribArray(attLocs.getTexCoord0_loc());// not including this line cost me 10+ hours!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//			gl.glVertexAttribPointer(attLocs.getTexCoord0_loc(), 2, GL.GL_FLOAT, false, 0,0);
//			gl.glBindVertexArray(0); //deactivate vao
			
	}
	
	@Override
	public void draw(GLAutoDrawable drawable) {
		GL3 gl = (GL3) drawable.getGL();
		if(!renderFront) gl.glCullFace(GL.GL_FRONT);  //cull front faces 

		
		//get shader vars
		int mvMatLoc=IdentityLocs.getMVLoc();

		MatrixStack mv= GeometryTransformPipeline.getModelViewMatrixStack();
		mv.pushMatrix();
			
			mv.multMatrix(this.getTransform());
			Matrix3D mvMat=mv.peek();
			//System.out.println(mvMat.toString());
			//System.out.println(this.getTranslation().toString());
			//mvMat.concatenate(this.getTranslation());
			//get projection matrix
			// get mv matrix
			double[] mvVals = mvMat.getValues();
			float[] mvValsf= new float[mvVals.length];
			for(int i=0; i<mvVals.length;i++) mvValsf[i]=(float) mvVals[i];
			//send to shader
			gl.glUniformMatrix4fv(mvMatLoc, 1,false, mvValsf,0);
			gl.glBindVertexArray(vaoID[0]);
			gl.glDrawArrays(GL.GL_TRIANGLES, 0,72);
			gl.glBindVertexArray(0);
		mv.popMatrix();

		if (!renderFront) gl.glCullFace(GL.GL_BACK); // retore cull face back to CCW
	}

	public void renderFrontFace(boolean front){
		renderFront=front;
	}
	
}
