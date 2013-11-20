

import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Material;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;
import graphicslib3D.Vertex3D;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

public class LightSphere extends Shape3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// geometry vars
	private int[] indices;
	private Vertex3D[] vertices;
	//VAO data
	private float[] verts; // raw data
	private float[] normals;
	private float[] colors;
	private int[] vaoID= new int[1];// vertex array object ID
	private int numVertices;
	private int numIndices;
	private int precision;
	private double radius;
	private boolean isBuilding=false; // sets the sphere to be drawn triangle by triangle
	private int incBuilding=3;
	private IdentityLocs attLocs; // vertex attribute locations
	private Material material;
	private float time=0;
	private float timeInc=.01f;
	
	// not textures or lights for light sphere
	private boolean islightingOn=false;
	private boolean textureEnabled=false;
	
	//constructor
	public LightSphere(GL3 gl, Point3D initPos, int precision, double radius, Color c){
		attLocs= new IdentityLocs();
		this.precision=precision;
		this.radius=radius;
		
		initSphere();
		//dump vert, colors, and normal values from init into float []s
		int numFloats=indices.length*3;
		verts = new float[numFloats];
		normals= new float[numFloats];
		colors = new float[numFloats+ indices.length]; // four values per color
		
		 Vector3D normal= new Vector3D();
		 Point3D point;
		//float r=1,g=1, b=1;
		for( int i=0; i<indices.length; i++){
		  verts[(i*3)]=(float) vertices[indices[i]].getX();
		  verts[(i*3)+1]=(float) vertices[indices[i]].getY();
		  verts[(i*3)+2]=(float) vertices[indices[i]].getZ();
		 
		  point= vertices[indices[i]].getLocation();

		  
		  //colors
		  
		  colors[(i*4)]= (float) c.getRed()/255f;
		  colors[(i*4)+1]= (float) c.getGreen()/255f;
		  colors[(i*4)+2]=(float) c.getBlue()/255f;
		  colors[(i*4)+3]=(float)  1;           //make opaque    //vertices[indices[i]].getColor().getAlpha();
		  


		}
		// create vao
		gl.glGenVertexArrays(1, vaoID, 0);
		gl.glBindVertexArray(vaoID[0]);
		int [] bufferIDs = new int[2];
		gl.glGenBuffers(2, bufferIDs,0);
		
		
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
		
		//normal buff setup
		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIDs[2]);
		//FloatBuffer normalBuf=FloatBuffer.wrap(normals);
		//gl.glBufferData(GL.GL_ARRAY_BUFFER, normalBuf.limit()*4, normalBuf, GL.GL_STATIC_DRAW);
		//gl.glEnableVertexAttribArray(attLocs.getNormalLoc());
		//gl.glVertexAttribPointer(attLocs.getNormalLoc(), 3, GL.GL_FLOAT, false, 0,0);
		

		
		gl.glBindVertexArray(0); //deactivate vao
	}
	/* (non-Javadoc)
	 * @see a2.Moon#draw(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void draw(GLAutoDrawable drawable) {
		GL3 gl = (GL3) drawable.getGL();
		gl.glUseProgram(IdentityLocs.getProgID());

		MatrixStack mv= GeometryTransformPipeline.getModelViewMatrixStack();
		mv.pushMatrix();
			mv.multMatrix(this.getTransform());
			Matrix3D mvMat=mv.peek();


			// get mv matrix
			double[] mvVals = mvMat.getValues();
			float[] mvValsf= new float[mvVals.length];
			for(int i=0; i<mvVals.length;i++) mvValsf[i]=(float) mvVals[i];
			gl.glUniformMatrix4fv(IdentityLocs.getMVLoc(), 1,false, mvValsf,0);
			gl.glBindVertexArray(vaoID[0]);
			gl.glDrawArrays(GL.GL_TRIANGLES, 0,indices.length);
			gl.glBindVertexArray(0);
		mv.popMatrix();
	}
	
	 /**
	 * Inits the geometry variables based on input params
	 * by taking a half ring of points and sweeping them
	 * in a full circle to create the vertices of a sphere
	 */
	private void initSphere(){
		 	numVertices=(precision+1)*(precision+1);
			numIndices=precision*2*precision*3;
			vertices= new Vertex3D[numVertices];
			indices= new int[numIndices];
			Vector3D zAxis= new Vector3D(0,0,1);
			
			
			int r=255 ,g=0 ,b=255;// color vars
			int colorInc= 255/precision;
			//calcs first semi cicle
			for (int i=0; i<=precision; i++){
				vertices[i]= new Vertex3D();
				float angle=i*180f/precision;
				Matrix3D rotateZ= new Matrix3D(angle, zAxis);
				Point3D p= new Point3D(0,radius,0);
				
				p=p.mult(rotateZ);
				vertices[i].setLocation(p);
				
				//set color
				vertices[i].setColor(r, g, b);
				b-=colorInc;
		}	
			
			//  sweep 360 degrees
			for (int slice=1; slice<precision+1; slice++){
				
				double angle=slice*360f/precision;
				Vector3D yAxis= new Vector3D(0,1,0);
				Matrix3D rotateX= new Matrix3D(angle, yAxis);
				for(int i=0; i<=precision; i++){
					vertices[slice*(precision+1)+i]= new Vertex3D();
					Point3D ring0Pos= vertices[i].getLocation();
					Point3D newPosition= ring0Pos.mult(rotateX);
					vertices[slice*(precision+1)+i].setLocation(newPosition);
					
					//color 
					vertices[slice*(precision-2)+i+1].setColor(vertices[i].getColor());
				}
			}
			Color color;
			for (int i=0; i<vertices.length; i++){
				 color=vertices[i].getColor();
				if(color.getBlue()==0 && color.getRed()==0 && color.getGreen()==0) vertices[i].setColor(255, 255, 255);
			}
			//assign triangle indices to vertex positions
			for(int slice=0; slice<precision; slice++){
				for( int i=0; i<precision; i++){
					if (i==0){
						
						//top
						indices[((slice*precision+i)*2)*3+0]=slice*(precision+1)+i;
						indices[((slice*precision+i)*2)*3+2]=(slice+1)*(precision+1)+i+1;
						indices[((slice*precision+i)*2)*3+1]=slice*(precision+1)+i+1;
						
						
					}
					else if (i==precision-1){
						//bottom
						indices[((slice*precision+i)*2)*3+0]=(slice)*(precision+1)+i;
						indices[((slice*precision+i)*2)*3+2]=(slice+1)*(precision+1)+i;
						indices[((slice*precision+i)*2)*3+1]=(slice)*(precision+1)+i+1;
						
	
					}
					else{ 
						
						//everything in between
						indices[((slice*precision+i)*2)*3+0]=slice*(precision+1)+i;
						indices[((slice*precision+i)*2)*3+2]=(slice+1)*(precision+1)+i;
						indices[((slice*precision+i)*2)*3+1]=slice*(precision+1)+i+1;
						
						indices[((slice*precision+i)*2+1)*3+1]=(slice+1)*(precision+1)+i;
						indices[((slice*precision+i)*2+1)*3+0]=(slice+1)*(precision+1)+i+1;
						indices[((slice*precision+i)*2+1)*3+2]=slice*(precision+1)+i+1;
						

					}
			
			}
	 }
	 }
	 
	 

}