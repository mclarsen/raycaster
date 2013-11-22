import javax.media.opengl.GL3;


public class RaycastLocs {

	private static int mvLoc=-1;
	private static int stepSize=-1;
	private static int backFaceMap=-1;
	private static int projMat=-1;
	private static int volume=-1;
	
	
	public static void setShaderID(GL3 gl, int programID){
		mvLoc= gl.glGetUniformLocation(programID, "modelViewMatrix");
		backFaceMap=gl.glGetUniformLocation(programID, "backFace");
		projMat=gl.glGetUniformLocation(programID, "projMatrix");
		volume=gl.glGetUniformLocation(programID, "volume");
		
		
		if ( mvLoc==-1 || volume==-1 || stepSize==-1 ||backFaceMap==-1
				|| projMat==-1 ){
			System.out.println("Error getting RayCasgting locs");
			System.out.println("mvLoc "+ mvLoc);
			System.out.println("volume "+ volume);
			System.out.println("backFace "+ backFaceMap);
			System.out.println("ProjMat "+ projMat);
			System.out.println("StepSize"+ stepSize);


		}
	}
	
	public static int getMVPLoc(){ return mvLoc;}
	public static int getVolumeLoc(){ return volume;}
	public static int getProjLoc(){ return projMat;}

	public static int getStepSizeLoc(){return stepSize;}
	public static int getColorMapLoc(){return backFaceMap;}


}
