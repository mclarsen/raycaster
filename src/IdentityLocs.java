

import javax.media.opengl.GL3;

public class IdentityLocs {

	private static int mvLoc=-1;
	private static int projLoc=-1;
	private static int progID=-1;
	private static int vertLoc=-1;
	private static int colorLoc=-1;
	
	public static void setShaderID(int shaderID, GL3 gl){
		progID= shaderID;
		//attributes
		vertLoc=gl.glGetAttribLocation(shaderID, "vertPos");
		colorLoc=gl.glGetAttribLocation(shaderID, "vertColor");
		//uniforms
		mvLoc=gl.glGetUniformLocation(shaderID, "modelViewMatrix");
		projLoc=gl.glGetUniformLocation(shaderID, "projMatrix");
		
		if (mvLoc==-1|| projLoc==-1 || vertLoc==-1 || colorLoc==-1){
			System.out.println("Identity Locs error");
			System.out.println("vertLoc "+ vertLoc);
			System.out.println("projLoc "+ projLoc);
			System.out.println("mvLoc "+ mvLoc);
			System.out.println("colorLoc "+ colorLoc);
		}
	}
	
	public static int getProjLoc() {return projLoc;}
	public static int getVertLoc() {return vertLoc;}
	public static int getProgID() {return progID;}
	public static int getMVLoc() {return mvLoc;}
	public static int getColorLoc() {return colorLoc;}

}
