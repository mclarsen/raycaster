import javax.media.opengl.GL3;


public class RaycastLocs {

	private static int mvpLoc=-1;
	private static int camPosLoc=-1;
	private static int particleSize=-1;
	private static int backFaceMap=-1;
	private static int projMat=-1;
	private static int camUp=-1;
	private static int normalMatLoc=-1;
	
	public RaycastLocs(GL3 gl, int programID){
		mvpLoc= gl.glGetUniformLocation(programID, "mviewMat");
		camPosLoc=gl.glGetUniformLocation(programID, "camPos");
		backFaceMap=gl.glGetUniformLocation(programID, "backFace");
		projMat=gl.glGetUniformLocation(programID, "projMat");
		normalMatLoc=gl.glGetUniformLocation(programID, "normalMat");
		
		if ( mvpLoc==-1 || camPosLoc==-1 || particleSize==-1 ||backFaceMap==-1
				|| projMat==-1 || camUp==-1 || normalMatLoc==-1){
			System.out.println("Error getting rendering locs");
			System.out.println("mvpLoc "+ mvpLoc);
			System.out.println("camPos "+ camPosLoc);
			System.out.println("paritcleSize "+ particleSize);
			System.out.println("backFace "+ backFaceMap);
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
	public int getColorMapLoc(){return backFaceMap;}
	public int getNormalMatLoc(){return normalMatLoc;}

}
