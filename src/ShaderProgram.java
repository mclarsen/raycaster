

import graphicslib3D.GLSLUtils;

import java.util.Stack;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

public class ShaderProgram {
	/*
	 * shader types
	 */
	public static final int VERTEX_SHADER=0;
	public static final int GEOMETRY_SHADER=1;
	public static final int FRAGMENT_SHADER=2;
	
	private int programID;
	/*
	 * Shader source locations
	 */
	private String vertexShader=null;
	private String geomShader=null;
	private String fragShader=null;
	/*
	 * flags to ensure proper usage
	 */
	private boolean alreadyCompiled=false;
	private boolean alreadyLinked=false;
	
	
	public ShaderProgram(){
		programID=-1;
	}
	
	public int getProgramID(){
		return programID;
	}
	
	public void addShader(String shaderSource, int shaderType){
		if(!alreadyCompiled&&!alreadyLinked){
			if(shaderType==VERTEX_SHADER){
				if(vertexShader==null){
					vertexShader=shaderSource;
				}
				else{
					System.out.println("ERROR> already has vertex shader");
				}
			}// add vertex shader
			if(shaderType==FRAGMENT_SHADER){
				if(fragShader==null){
					fragShader=shaderSource;
				}
				else{
					System.out.println("ERROR> already has fragment shader");
				}
			}// add frag shader
			if(shaderType==GEOMETRY_SHADER){
				if(geomShader==null){
					geomShader=shaderSource;
				}
				else{
					System.out.println("ERROR> already has geomtry shader");
				}
			}// add geom shader
			
		}// linked and compiled
		else{
			System.out.println("ERROR> Program already compiled or linked");
		}
	}// end add shader
	
	public void compileProgram(GLAutoDrawable draw){
		if(!alreadyCompiled){
			GL3 gl=(GL3) draw.getGL();
			int vertID=-1;
			int fragID=-1;
			int geomID=-1;
			/*
			 * compile existing shaders
			 */
			if(vertexShader!=null) vertID=compileVert(draw);
			if(geomShader!=null) geomID=compileGeom(draw);
			if(fragShader!=null) fragID=compileFrag(draw);
			/*
			 *  attach  shaders prgrams
			 */
			programID=gl.glCreateProgram();
			if(vertexShader!=null) gl.glAttachShader(programID, vertID);
			if(fragShader!=null)   gl.glAttachShader(programID, fragID);
			if(geomShader!=null) gl.glAttachShader(programID, geomID);
			alreadyCompiled=true;
		}
		else
		{
			System.out.println("Already Compiled");
		}
	}// end compile prorgam
	
	public void linkProgram(GLAutoDrawable draw){
		if(!alreadyLinked){
			GL3 gl=(GL3) draw.getGL();
			gl.glLinkProgram(programID);
			int[] linked = new int[1];
			gl.glGetProgramiv(programID, GL3.GL_LINK_STATUS, linked, 0);
			if (linked[0]!=1){
				System.out.println(" Linking failed so hard");
				GLSLUtils.printShaderInfoLog(draw, programID);
			}
			alreadyLinked=true;
		}
		else{
			System.out.println("Error > already linked");
		}
	}// link program
	
	/**
	 * Compiles a vertex shader and returns compiled shaderID
	 * @param draw
	 * @return
	 */
	private int compileVert(GLAutoDrawable draw){
		GL3 gl=(GL3) draw.getGL();
		int vertexShaderID= gl.glCreateShader(GL3.GL_VERTEX_SHADER);	
		String[] vertexShaderSource=GLSLUtils.readShaderSource(vertexShader);
		int [] lengths = new int [vertexShaderSource.length];
		for (int i=0; i<lengths.length;i++){
			lengths[i]=vertexShaderSource[i].length();
		}
		gl.glShaderSource(vertexShaderID, vertexShaderSource.length, vertexShaderSource, lengths, 0);
		gl.glCompileShader(vertexShaderID);
		int [] vertCompiled = new int[1];
		gl.glGetShaderiv(vertexShaderID, GL3.GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0]!=1){
			System.out.println("...Vertex compilation failed so hard");
			GLSLUtils.printShaderInfoLog(draw, vertexShaderID);
		}
		return vertexShaderID;
	}//compile vert
	
	/**
	 * Compiles a geometry shader and returns compiled shaderID
	 * @param draw
	 * @return
	 */
	private int compileGeom(GLAutoDrawable draw){
		GL3 gl=(GL3) draw.getGL();
		int geomShaderID= gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);	
		String[] fragShaderSource=GLSLUtils.readShaderSource(geomShader);
		int [] frag_lengths = new int [fragShaderSource.length];
		for (int i=0; i<frag_lengths.length;i++){
			frag_lengths[i]=fragShaderSource[i].length();
		}
		gl.glShaderSource(geomShaderID, fragShaderSource.length, fragShaderSource, frag_lengths, 0);
		gl.glCompileShader(geomShaderID);
		int [] geomCompiled = new int[1];
		gl.glGetShaderiv(geomShaderID, GL3.GL_COMPILE_STATUS, geomCompiled, 0);
		if (geomCompiled[0]!=1){
			System.out.println("...Geometry compilation failed so hard");
			GLSLUtils.printShaderInfoLog(draw, geomShaderID);
		}
		return geomShaderID;
	}// compile geom
	
	
	/**
	 * Comiles a fragment shader and returns compiled shaderID
	 * @param draw
	 * @return
	 */
	private int compileFrag(GLAutoDrawable draw){
		GL3 gl=(GL3) draw.getGL();
		int fragShaderID= gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);	
		String[] fragShaderSource=GLSLUtils.readShaderSource(fragShader);
		int [] frag_lengths = new int [fragShaderSource.length];
		for (int i=0; i<frag_lengths.length;i++){
			frag_lengths[i]=fragShaderSource[i].length();
		}
		gl.glShaderSource(fragShaderID, fragShaderSource.length, fragShaderSource, frag_lengths, 0);
		gl.glCompileShader(fragShaderID);
		int [] fragCompiled = new int[1];
		gl.glGetShaderiv(fragShaderID, GL3.GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0]!=1){
			System.out.println("...Frag compilation failed so hard");
			GLSLUtils.printShaderInfoLog(draw, fragShaderID);
		}
		return fragShaderID;
	}//compile Frag 


}// end class
