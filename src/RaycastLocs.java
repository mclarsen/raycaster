import javax.media.opengl.GL3;


public class RaycastLocs {

	private  int mvLoc=-1;
	private  int stepSize=-1;
	private  int backFaceMap=-1;
	private  int projMat=-1;
	private  int volume=-1;
	private int progID=-1;
	private int threshLoc=-1;
	private int colorLoc=-1;
	private int transferLoc=-1;
	private int camPosLoc=-1;

	  
	public  RaycastLocs(GL3 gl, int programID){
		progID=programID;
		mvLoc= gl.glGetUniformLocation(programID, "modelViewMatrix");
		backFaceMap=gl.glGetUniformLocation(programID, "backFace");
		projMat=gl.glGetUniformLocation(programID, "projMatrix");
		volume=gl.glGetUniformLocation(programID, "volume");
		threshLoc=gl.glGetUniformLocation(programID, "thresholds");
		colorLoc=gl.glGetUniformLocation(programID, "color");
		transferLoc=gl.glGetUniformLocation(programID, "transferFunction");
		camPosLoc=gl.glGetUniformLocation(programID, "camPos");
		
		if ( mvLoc==-1 || volume==-1 || stepSize==-1 ||backFaceMap==-1
				|| projMat==-1 ||threshLoc==-1||colorLoc==-1 ||transferLoc==-1||camPosLoc==-1){
			System.out.println("Error getting RayCasgting locs");
			System.out.println("mvLoc "+ mvLoc);
			System.out.println("volume "+ volume);
			System.out.println("backFace "+ backFaceMap);
			System.out.println("ProjMat "+ projMat);
			System.out.println("StepSize"+ stepSize);
			System.out.println("threshLoc"+ threshLoc);
			System.out.println("colorLoc"+ colorLoc);
			System.out.println("transferLoc"+ transferLoc);
			System.out.println("camPosLoc"+ camPosLoc);


		}
	}
	
	public  int getMVPLoc(){ return mvLoc;}
	public  int getVolumeLoc(){ return volume;}
	public  int getProjLoc(){ return projMat;}
	public  int getProgID(){return progID;}
	public  int getStepSizeLoc(){return stepSize;}
	public  int getColorMapLoc(){return backFaceMap;}
	public  int getThreshLoc(){return threshLoc;}
	public  int getColorLoc(){return colorLoc;}
	public 	int getTransferLoc(){return transferLoc;}
	public 	int getCamPosLoc(){return camPosLoc;}



}
