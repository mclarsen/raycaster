

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * this class is the representation of a camera in 3d space. It has methods to change the camera location and rotate 
 * to change the viewing direction.
 * @author larceny
 *
 */
public class Camera {
	private Point3D loc; // location of camera in wolrd coordinates
	private Vector3D u,v,n; // camera orientaion v=up(y) n= looking at(z) u=  (x) 
	private double speed=0;
	private static final double MAX=10;
	private Vector3D momentum= new Vector3D(0,0,0);
	// constructor
	public Camera(){
		
		loc= new Point3D(0,0,10); // camera set initial position
		u=new Vector3D(-1,0,0); //x
		v= new Vector3D(0,1,0);//y
		n= new Vector3D(0,0,-1);//z
	}
	/**
	 * rotates camera along the U vector
	 * @param degrees
	 */
	public void pitch(double degrees){
		Matrix3D rotation= new Matrix3D(-degrees, this.getRightAxis());
		
		Vector3D newUp= this.getUpAxis().mult(rotation);
		this.setUpAxis(newUp);
		
		Vector3D newView= this.getViewDirection().mult(rotation);
		this.setViewDirection(newView);
	}
	
	
	/**
	 * rotates camera along the V vector
	 * @param degrees
	 */
	public void yaw(double degrees){
		Matrix3D rotation= new Matrix3D(degrees, this.getUpAxis());
		
		Vector3D newRight= this.getRightAxis().mult(rotation);
		this.setRightAxis(newRight);
		
		Vector3D newView= this.getViewDirection().mult(rotation);
		this.setViewDirection(newView);
	}
	
	public void roll(double degrees){
		Matrix3D rotation= new Matrix3D(degrees, this.getViewDirection());
		
		Vector3D newRight= this.getRightAxis().mult(rotation);
		this.setRightAxis(newRight);
		
		Vector3D newUp= this.getUpAxis().mult(rotation);
		this.setUpAxis(newUp);
	}
	/**
	 * moves the camera in the y direction 
	 * @param amount
	 */
	public void moveUp(double amount){
		amount=-amount;
		Vector3D curLocAsVec= new Vector3D(this.getLocation());
		Vector3D upDir= this.getUpAxis().normalize();
		Vector3D newLocAsVec= curLocAsVec.add(upDir.mult(amount));
		Point3D newLocPt= new Point3D(newLocAsVec.getX(),newLocAsVec.getY(),newLocAsVec.getZ());
		loc= newLocPt;
		
		if(speed>3) speed=2.5;
		else if (speed<-3) speed=-2.5;
		Vector3D temp= new Vector3D(v.getX(), v.getY(), v.getZ());
		temp.normalize();
		momentum.setX(momentum.getX()+amount*temp.getX());
		momentum.setY(momentum.getY()+amount*temp.getY());
		momentum.setZ(momentum.getZ()+amount*temp.getZ());
		
	}
	/**
	 * moves the camera in the forward direction 
	 * @param amount
	 */
	public void moveForward(double amount){
		//System.out.println("amount : "+amount);
		Vector3D curLocAsVec= new Vector3D(this.getLocation());
		Vector3D viewDir= this.getViewDirection().normalize();
		Vector3D newLocAsVec= curLocAsVec.add(viewDir.mult(amount));
		Point3D newLocPt= new Point3D(newLocAsVec.getX(),newLocAsVec.getY(),newLocAsVec.getZ());
		loc= newLocPt;
		
		
		if(speed>3) speed=2.5;
		else if (speed<-3) speed=-2.5;
		Vector3D temp= new Vector3D(n.getX(), n.getY(), n.getZ());
		temp.normalize();
		momentum.setX(momentum.getX()+amount*temp.getX());
		momentum.setY(momentum.getY()+amount*temp.getY());
		momentum.setZ(momentum.getZ()+amount*temp.getZ());
		
		 
		
	}
	
	/**
	 * moves the camera in the x direction 
	 * @param amount
	 */
	public void moveRight(double amount){
		Vector3D curLocAsVec= new Vector3D(this.getLocation());
		Vector3D rightDir= this.getRightAxis().normalize();
		Vector3D newLocAsVec= curLocAsVec.add(rightDir.mult(-amount));
		Point3D newLocPt= new Point3D(newLocAsVec.getX(),newLocAsVec.getY(),newLocAsVec.getZ());
		loc= newLocPt;
		
		if(speed>3) speed=2.5;
		else if (speed<-3) speed=-2.5;
		Vector3D temp= new Vector3D(u.getX(), u.getY(), u.getZ());
		temp.normalize();
		momentum.setX(momentum.getX()-amount*temp.getX());
		momentum.setY(momentum.getY()-amount*temp.getY());
		momentum.setZ(momentum.getZ()-amount*temp.getZ());
		
	}
	

	
	/**
	 * returns a copy of the camera location as a Point3D
	 * @return
	 */
	public Point3D getLocation(){
		Point3D temp= new Point3D();
		temp.setX(loc.getX());
		temp.setY(loc.getY());
		temp.setZ(loc.getZ());
		return temp;
		
	}
	/**returns a copy of the positive y direction as  a Vector3D
	 * @return
	 */
	public Vector3D getUpAxis(){
		// up axis is V/y axis
		Vector3D temp= new Vector3D();
		temp.setX(v.getX());
		temp.setY(v.getY());
		temp.setZ(v.getZ());
		return temp;
	}
	
	/**returns a copy of the positive view direction as  a Vector3D
	 * @return
	 */
	public Vector3D getViewDirection(){
		//view axis is N/z axis
		Vector3D temp= new Vector3D();
		temp.setX(n.getX());
		temp.setY(n.getY());
		temp.setZ(n.getZ());
		return temp;
	}
	
	/**returns a copy of the positive x direction as  a Vector3D
	 * @return
	 */
	public Vector3D getRightAxis(){
		// up axis is U/x axis
		Vector3D temp= new Vector3D();
		temp.setX(u.getX());
		temp.setY(u.getY());
		temp.setZ(u.getZ());
		return temp;
	}
	
	/**
	 * @param newDir
	 * Sets the camera view direction from a Vector3D
	 */
	public void setViewDirection(Vector3D newDir){
		// //N/z axis
		n.setX(newDir.getX());
		n.setY(newDir.getY());
		n.setZ(newDir.getZ());
	}
	
	/**
	 * sets the up axis of the camera from a Vector3D
	 * @param newDir
	 */
	public void setUpAxis(Vector3D newDir){
		// //N/z axis
		v.setX(newDir.getX());
		v.setY(newDir.getY());
		v.setZ(newDir.getZ());
	}
	
	/**
	 * Sets the x axis direction of the camera from a Vector3D
	 * @param newDir
	 */
	public void setRightAxis(Vector3D newDir){
		// //N/z axis
		u.setX(newDir.getX());
		u.setY(newDir.getY());
		u.setZ(newDir.getZ());
	}
	
	public void tick(){
		loc= new Point3D(loc.getX()+ momentum.getX(), loc.getY()+momentum.getY(),loc.getZ()+momentum.getZ());
		
	}
	/**
	 * Stops all camera movement
	 */
	public void allStop(){
		momentum.setX(0);
		momentum.setY(0);
		momentum.setZ(0);
	}
	
	/**
	 * Sets new camera Location
	 * @param newLoc
	 */
	public void setLocation(Point3D newLoc){
		this.loc= newLoc;
	}
}
