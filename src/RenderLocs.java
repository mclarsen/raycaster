

import javax.media.opengl.GL3;

public class RenderLocs {
	private static int mvpLoc=-1;
	private static int camPosLoc=-1;
	private static int particleSize=-1;
	private static int colorMap=-1;
	private static int projMat=-1;
	private static int camUp=-1;
	private static int normalMatLoc=-1;
	
	public RenderLocs(GL3 gl, int programID){
		mvpLoc= gl.glGetUniformLocation(programID, "mviewMat");
		camPosLoc=gl.glGetUniformLocation(programID, "camPos");
		particleSize=gl.glGetUniformLocation(programID, "particleSize");
		colorMap=gl.glGetUniformLocation(programID, "colorMap");
		projMat=gl.glGetUniformLocation(programID, "projMat");
		camUp=gl.glGetUniformLocation(programID, "camUp");
		normalMatLoc=gl.glGetUniformLocation(programID, "normalMat");
		
		if ( mvpLoc==-1 || camPosLoc==-1 || particleSize==-1 ||colorMap==-1
				|| projMat==-1 || camUp==-1 || normalMatLoc==-1){
			System.out.println("Error getting rendering locs");
			System.out.println("mvpLoc "+ mvpLoc);
			System.out.println("camPos "+ camPosLoc);
			System.out.println("paritcleSize "+ particleSize);
			System.out.println("colorMap "+ colorMap);
			System.out.println("ProjMat "+ projMat);
			System.out.println("CamUp "+ camUp);
			System.out.println("NormalMat "+ normalMatLoc);
		}
	}
	
	public int getMVPLoc(){ return mvpLoc;}
	public int getCamUpLoc(){return camUp;}
	public int getProjLoc(){ return projMat;}
	public int getCamPosLoc(){return camPosLoc;}
	public int getParticleSizeLoc(){return particleSize;}
	public int getColorMapLoc(){return colorMap;}
	public int getNormalMatLoc(){return normalMatLoc;}
}
