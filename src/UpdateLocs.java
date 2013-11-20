

import javax.media.opengl.GL3;

public class UpdateLocs {

	private static int timeLoc=-1;
	private static int timePassedLoc=-1;
	private static int randomTextureLoc=-1;
	private static int launcherLifetimeLoc=-1;
	private static int shellLifetimeLoc=-1;
	private static int secondaryShellLifetimeLoc=-1;
	
	
	public UpdateLocs(GL3 gl, int programID){
		timeLoc=gl.glGetUniformLocation(programID, "time");
		timePassedLoc=gl.glGetUniformLocation(programID, "timePassed");
		randomTextureLoc=gl.glGetUniformLocation(programID, "randomTexture");
		launcherLifetimeLoc=gl.glGetUniformLocation(programID, "launcherLifetime");
		shellLifetimeLoc=gl.glGetUniformLocation(programID, "shellLifetime");
		secondaryShellLifetimeLoc=gl.glGetUniformLocation(programID, "secondaryShellLifetime");
		
		if(timeLoc==-1 || timePassedLoc==-1 || randomTextureLoc==-1 || launcherLifetimeLoc ==-1 ||
			shellLifetimeLoc==-1 || secondaryShellLifetimeLoc==-1 ){
			System.out.println("Error getting Update locations");
			System.out.println("time "+ timeLoc);
			System.out.println("timePassed "+ timePassedLoc);
			System.out.println("randomTexture "+ randomTextureLoc);
			System.out.println("laucherLifetime "+ launcherLifetimeLoc);
			System.out.println("shelllifetime "+ shellLifetimeLoc);
			System.out.println("secondaryShellLifetime "+ secondaryShellLifetimeLoc);
		}//if 
	}//constructor
	
	public int getTimeLoc() { return timeLoc;}
	public int getTimePassedLoc() {return timePassedLoc;}
	public int getRandomTexLoc(){ return randomTextureLoc;}
	public int getLauncherLifetimeLoc(){ return launcherLifetimeLoc;}
	public int getShellLifetimeLoc(){ return shellLifetimeLoc;}
	public int getSecondaryShellLifetime(){return secondaryShellLifetimeLoc;}
}
