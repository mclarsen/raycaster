import javax.media.opengl.GL3;


public class RaycastLocs {

	private  int mvLoc=-1;
	private  int stepSize=-1;
	private  int backFaceMap=-1;
	private  int projMat=-1;
	private  int volume=-1;
	private int progID=-1;
	
	public  RaycastLocs(GL3 gl, int programID){
		progID=programID;
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
	
	public  int getMVPLoc(){ return mvLoc;}
	public  int getVolumeLoc(){ return volume;}
	public  int getProjLoc(){ return projMat;}
	public  int getProgID(){return progID;}
	public  int getStepSizeLoc(){return stepSize;}
	public  int getColorMapLoc(){return backFaceMap;}


}
