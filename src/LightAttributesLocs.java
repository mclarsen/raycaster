

import javax.media.opengl.GL3;

/**
 * @author larceny
 *keeps track of all the attribute locations of a positional light
 */
public class LightAttributesLocs {
	private int diffLoc;
	private int ambLoc;
	private int specLoc;
	private int posLoc;
	private String name;
	private int globalAmbientLoc=-1;
	
	public  LightAttributesLocs(GL3 gl, String lightName, int shaderProgID){
		name=lightName;
		diffLoc=gl.glGetUniformLocation(shaderProgID, name+".diffuse");
		ambLoc=gl.glGetUniformLocation(shaderProgID, name+".ambient");
		specLoc=gl.glGetUniformLocation(shaderProgID, name+".specular");
		posLoc=gl.glGetUniformLocation(shaderProgID, name+".position");
		globalAmbientLoc=gl.glGetUniformLocation(shaderProgID, "globalAmbient");
		
		
		if (diffLoc==-1 || ambLoc==-1 || specLoc==-1 || posLoc==-1||globalAmbientLoc==-1){
			System.out.println("Error getting light attributes for "+ name+": ");
			System.out.println("diffLoc " +diffLoc);
			System.out.println("ambLoc " +ambLoc);
			System.out.println("specLoc " +specLoc);
			System.out.println("posLoc " +posLoc);
			System.out.println("globalAmbient " +globalAmbientLoc);
		}
	}

	public int getDiffLoc() {
		return diffLoc;
	}

	public void setDiffLoc(int diffLoc) {
		this.diffLoc = diffLoc;
	}

	public int getAmbLoc() {
		return ambLoc;
	}

	public void setAmbLoc(int ambLoc) {
		this.ambLoc = ambLoc;
	}

	public int getSpecLoc() {
		return specLoc;
	}

	public void setSpecLoc(int specLoc) {
		this.specLoc = specLoc;
	}

	public int getPosLoc() {
		return posLoc;
	}

	public void setPosLoc(int posLoc) {
		this.posLoc = posLoc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getGlobalAmientLoc(){return this.globalAmbientLoc;}
	
}
