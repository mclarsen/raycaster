

import graphicslib3D.GLSLUtils;
import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;
import graphicslib3D.Vertex3D;
import graphicslib3D.light.PositionalLight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;








import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
/**
 * @author larceny
 *This class is a JFrame that displays an openGl Canvas. Contained are camera controls
 *and the rendering of the scence in the canvas.
 */
@SuppressWarnings("serial")
public class OpenGLFrame extends JFrame implements GLEventListener, ActionListener {
	
	//worls object containeer
	private ArrayList<Shape3D> objects;
	private GLCanvas myCanvas;
	private JFrame thisJFrame=this;
	private static GLU glu;
	private double moveSpeed=.001; // how fast camera moves per keystroke
	private double panSpeed=.3;
	private double aspect;
	
	private final static boolean OPENGL_DEBUG_MODE=true; // if true then debuggin output is on
	public static final int TICK_LENGTH=10; 			 // tick length in milliseconds
	private Timer timer;     							 // game timer
	private Camera myCamera;							 // camera object


	private boolean drawAxisOn=true; // will draw xyz axis
	// Key Binding Objects
	InputMap input_Map;
	ActionMap action_Map; 
	// flags
	private boolean camMoved=true;
	private boolean isPaused=false;
	private boolean wireFrameOn=false;
	
	// Matrix stacks
	private final int MAX_STACK_SIZE=64;
	private MatrixStack modelViewMat= new MatrixStack(MAX_STACK_SIZE);
	private MatrixStack projMat= new MatrixStack(MAX_STACK_SIZE);
	private Matrix3D proj;
	
	private int currentWidth;
	private int currentHeight;
	
	//shaderProgram IDs
	private int IdentityProgID;
	private int raycastProgID;
	
	// projection vars
	private float fov=40;
	private float near=.01f;
	private float far=2000.0f;
	
	//drawables
    Grid grid;
    ParticleSystem ps;
    LightSphere theLight;
    BoundingBox theBox;
    
    //lights
    private PositionalLight light1= new PositionalLight();
	float[] light1_specular= {.8f,.8f,.8f,1};
	float[] light1_diffuse= {1f,1f,1f,1};
	float[] light1_ambient={0,0,0,1};
	private boolean light1On=true;
    private LightAttributesLocs light1Locs;
  //value of global ambient light
    private Vector3D globalAmbientLight= new Vector3D(.9f,.9f,.9f,1); 
    
    //movement flags
    boolean onDown=false;
    boolean onRight=false;
    boolean onLeft=false;
    boolean onUp=false;
    boolean onW=false;
    boolean onS=false;
    boolean onA=false;
    boolean onD=false;
    boolean onQ=false;
    boolean onE=false;
    boolean onZ=false;
    boolean onC=false;
    
    // Shader Programs
    private ShaderProgram identityShader;
    private ShaderProgram raycastShader;
    
    
    //******************Volume Rendering Vars******************************
    public static int[] backFaceTextureID= new int[1];
    private int[] backFaceFrameBuff= new int [1];
    public static int[] volumeTextureID= new int[1];
    private boolean doOnce=true;  //FIND ME and remove
    
	/**
	 * Default constructor
	 * inits frame an all parts
	 */
	public OpenGLFrame(){
		
		glu= new GLU();    // GLU Object for Error reporting
		JPanel top= new JPanel();			 
		this.add(top);
		timer= new Timer(TICK_LENGTH, this);                   // Animation Timer
		objects= new ArrayList<Shape3D>();                     // world object holder
		myCamera= new Camera();				                   //create camera object

		this.setLayout(new BorderLayout());                    //JFrame Setup
		setTitle("RayCast Demo");
		setSize(1000,750);
		setLocation(200,200);
		
		int input_MapName = JComponent.WHEN_IN_FOCUSED_WINDOW; // Key Binding setup
		input_Map = top.getInputMap(input_MapName);            // Gets action map for top panel
		action_Map = top.getActionMap();
		buildKeyBindings();									   // Builds all key bindings
														
		myCanvas= new GLCanvas ();							   //canvas setup
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
				
		commandPanelBuilder();
		
		this.setVisible(true);								    //set frame options
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		timer.start();											// start animation
	}

	/**
	 * commandPanelBuilder constructs all controls in the command panel
	 */
	private void commandPanelBuilder(){
		// Command Panel
		JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new GridLayout(12, 1));
		commandPanel.setBorder(new TitledBorder("Commands : "));
		this.add(commandPanel, BorderLayout.WEST);
		
		final JButton wireFrameButton = new JButton();
		wireFrameButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				    if (wireFrameOn){
				    	wireFrameButton.setText("Turn on wire frame mode");
				    	wireFrameOn=false;
				    }
				    else {
				    	wireFrameButton.setText("Turn off wire frame mode");
				    	wireFrameOn=true;
				    }
				  } 
				} );
		wireFrameButton.setText("Turn on wire frame mode");
		wireFrameButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		commandPanel.add(wireFrameButton);
		
		final JButton pauseButton = new JButton();
		pauseButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				    if (isPaused){
				    	pauseButton.setText("Pause Animation");
				    	ps.setPaused(false);
				    	//timer.start();
				    	isPaused=false;
				    }
				    else {
				    	pauseButton.setText("Resume Animantion");
				    	//timer.stop();
				    	ps.setPaused(true);
				    	isPaused=true;
				    }
				  } 
				} );
		pauseButton.setText("Pause Animation");
		pauseButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		commandPanel.add(pauseButton);
	}
	
	//--------------------------------------------GL Event Listener Methods---------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable arg0) {
		//System.out.println(light1Locs.getGlobalAmientLoc());
		/*
		 * Movement flags
		 */
		if(onDown) myCamera.pitch(-panSpeed);
		if(onRight) myCamera.yaw(-panSpeed);
		if(onUp) myCamera.pitch(panSpeed);
		if(onLeft) myCamera.yaw(panSpeed);
		if(onW) myCamera.moveForward(moveSpeed);
		if(onS) myCamera.moveForward(-moveSpeed);
		if(onD) myCamera.moveRight(moveSpeed);
		if(onA) myCamera.moveRight(-moveSpeed);
		if(onQ) myCamera.moveUp(moveSpeed);
		if(onE) myCamera.moveUp(-moveSpeed);
		if(onZ) myCamera.roll(-panSpeed);
		if(onC) myCamera.roll(panSpeed);
		myCamera.tick(); 													//calculate movement based on momentum
		
		GL3 gl=(GL3) arg0.getGL();
		//gl.glEnable(GL3.GL_BLEND);											//enable alpha blending
		//gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);		//set blend function
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT|GL3.GL_DEPTH_BUFFER_BIT); 		// clear color and depth buffer
	
		/*
		 * Set projection Matrix using camera's current position and load matrix into
		 * the matrix stack. Also, reset model view matrix. 
		 */	
		proj.setToIdentity();
		this.setPerspective(fov, (float)aspect,near, far, proj);
		GeometryTransformPipeline.getModelViewMatrix().setToIdentity();
		GeometryTransformPipeline.getModelViewMatrixStack().loadMatrix(this.lookAt(myCamera));
		GeometryTransformPipeline.getProjectionMatrixStack().loadMatrix(proj);
		
		installLighting(gl);
		gl.glUseProgram(identityShader.getProgramID());
		//---------set uniforms for the identity shader
		ps.setProjectionMatrix(proj);
		double[]  projVals= proj.getValues();									//get projection matrix
		float[] projValsf= new float[projVals.length];
		for(int i=0; i<projVals.length;i++)projValsf[i]=(float) projVals[i];	//convert to floats
		gl.glUniformMatrix4fv(IdentityLocs.getProjLoc(), 1,false, projValsf,0); //send projection matrix to shader
		//********************Render the backface to the framebuffer texture***********************************

		//System.out.println("Proj: " + proj);
		
		
		//render to the buffer
		gl.glBindFramebuffer (GL3.GL_FRAMEBUFFER, backFaceFrameBuff[0]);
		gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, backFaceTextureID[0], 0);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT|GL3.GL_DEPTH_BUFFER_BIT); 			// clear color and depth buffer
		theBox.renderFrontFace(false);
		theBox.draw(arg0);
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);							//undbind the renderbuffer
		
		// Now lets do some casting----------------------------------------------------------------------------
		gl.glUseProgram(raycastProgID);
		gl.glUniformMatrix4fv(RaycastLocs.getProjLoc(), 1,false, projValsf,0); //send projection matrix to raycast shader
		//bind the two textures
		gl.glEnable(GL3.GL_BLEND);
		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
		gl.glActiveTexture(GL3.GL_TEXTURE1);
	    gl.glBindTexture( GL3.GL_TEXTURE_2D,  backFaceTextureID[0]);
	    gl.glUniform1i(RaycastLocs.getColorMapLoc(), 1);
	    gl.glActiveTexture(GL3.GL_TEXTURE2);
	    gl.glBindTexture( GL3.GL_TEXTURE_3D,  volumeTextureID[0]);
	    gl.glUniform1i(RaycastLocs.getVolumeLoc(), 2);
	    theBox.renderFrontFace(true);
	    theBox.draw(arg0);
		
	    //Matrix3D mvp= GeometryTransformPipeline.getModelViewProjectionMatrix();
	    //Matrix3D coord=new Matrix3D();
	    //coord.translate(1, -1, -1);
	    //mvp.concatenate(coord);
	    //System.out.println("Screen X :  "+ (mvp.getCol(3).getX()/mvp.getCol(3).getW()+1)/2.0);
	    //System.out.println("Screen Y :  "+ (mvp.getCol(3).getY()/mvp.getCol(3).getW()+1)/2.0);
	    //System.out.println(mvp.getCol(3).toString());
	    //Vertex3D t= new Vertex3D(1,0,0);
	    //System.out.println(mvp.toString());
	    //t=t.mult(mvp);
	    //System.out.println(t.toString());
		//*****************************************************************************************************
		gl.glUseProgram(identityShader.getProgramID());
		
		//theBox.renderFrontFace(true);
		//theBox.draw(arg0);
		
		if (wireFrameOn) gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);  //enable wirefram if set
		else gl.glPolygonMode( GL3.GL_FRONT_AND_BACK, GL3.GL_FILL );
		

		if (drawAxisOn) {
			grid.draw(arg0);										//Draw axis grid if enable
		}
		
		//theLight.draw(arg0);
		ps.draw(gl, myCamera.getLocation(), myCamera.getUpAxis());
		
		errorCheck(gl,"display");												//check for errors in display
	}

	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		
		
	}

	
	@Override
	public void init(GLAutoDrawable arg0) {
		GL3 gl3 = (GL3) arg0.getGL();
		/*
		 *  geometry pipeline code setup
		 */
		//System.out.println("GL Version : " +gl3.glGetString(GL3.GL_VERSION));
		
		modelViewMat.loadIdentity();
		GeometryTransformPipeline.setModelViewMatrixStack(modelViewMat);
		GeometryTransformPipeline.setProjectionMatrixStack(projMat);
		proj= new Matrix3D();
		this.setPerspective(60f, 1f,.01f, 100f, proj);
		projMat.loadMatrix(proj);
		gl3.glEnable(GL3.GL_DEPTH_TEST);								
		gl3.glEnable(GL3.GL_CULL_FACE);
		/*
		 *  Identity shader creation code
		 */
		identityShader= new ShaderProgram();
		identityShader.addShader("Shaders/Identity.vert", ShaderProgram.VERTEX_SHADER);
		identityShader.addShader("Shaders/Identity.frag", ShaderProgram.FRAGMENT_SHADER);
		identityShader.compileProgram(arg0);
		identityShader.linkProgram(arg0);
		IdentityProgID= identityShader.getProgramID();
		gl3.glUseProgram(IdentityProgID);
		IdentityLocs.setShaderID(IdentityProgID, gl3);
		/*
		 *  RayCasting shader creation code
		 */
		raycastShader= new ShaderProgram();
		raycastShader.addShader("Shaders/raycast.vert", ShaderProgram.VERTEX_SHADER);
		raycastShader.addShader("Shaders/raycast.frag", ShaderProgram.FRAGMENT_SHADER);
		raycastShader.compileProgram(arg0);
		raycastShader.linkProgram(arg0);
		raycastProgID= raycastShader.getProgramID();
		gl3.glUseProgram(raycastProgID);
		RaycastLocs.setShaderID(gl3,raycastProgID);
		
		/*
		 * init drawables
		 */
		grid= new Grid(18, gl3);
		grid.scale(2, 2, 2);
		theBox= new BoundingBox(gl3);
		theBox.translate(0, 0, 0);
		//theBox.scale(1, 1, 10);
		theBox.renderFrontFace(false);
		
		theLight= new LightSphere(gl3, new Point3D(0,0,0), 20, .1, Color.YELLOW);
		
		//RandomTexture test= new RandomTexture(64, gl3);
		ps=new ParticleSystem(arg0, createTexture(gl3, "two.jpg", true), 1000);
		ps.scale(100, 100, 100);
		
		errorCheck(gl3,"end init");							//check for errors
		
		initLights(gl3);
		
		
		//Create Framebuffer to store the coordinates of the back face of the volume
		//inside of a texture
		//----------------------------------------------------------------------------
		//generate the framebuffer and the texture that will be rendered to.
		gl3.glGenTextures (1, backFaceTextureID,0);
		gl3.glGenFramebuffers (1, backFaceFrameBuff,0);
		
		int frameBuffID= backFaceFrameBuff[0];
		int backFaceTextID= backFaceTextureID[0];
		
		//bind them so we can set them up
		gl3.glBindFramebuffer (GL3.GL_FRAMEBUFFER, frameBuffID);
		gl3.glBindTexture (GL3.GL_TEXTURE_2D, backFaceTextID);
		
		//texture settings
		
		gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
		
		System.out.println("Width : "+myCanvas.getWidth()+" Height : "+myCanvas.getHeight());
		gl3.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA16F, myCanvas.getWidth(), myCanvas.getHeight(), 0, GL3.GL_RGBA, GL3.GL_FLOAT, null);
		//gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
		
		//tell the framebuffer to render to the backface texture and do colors (other options are rending the depth buffer for shadows)
		gl3.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, backFaceTextID, 0);
		
		//check the status of the frame buff
		int frameCode=gl3.glCheckFramebufferStatus (GL3.GL_FRAMEBUFFER);
		if (frameCode!=GL3.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("ERROR creating frame buffer");
			if(frameCode==GL3.GL_FRAMEBUFFER_UNDEFINED) System.out.println("Undefined");
			if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT ) System.out.println("Incomplete Attachment");
			if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT ) System.out.println("No Attatchmemt");
			if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER ) System.out.println("Incomplete Draw Buffer");
			if(frameCode==GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER ) System.out.println("Incomplete ReadBuffer");
			if(frameCode==GL3.GL_FRAMEBUFFER_UNSUPPORTED ) System.out.println("Not supported");
			//if(frameCode==GL3.GL_FRAMEBUFFER_UNDEFINED) System.out.println("Undefined");
			
			
		}
		//unbind the framebuffer
		gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
		
		//createTest Volume
		create_TestVolume(gl3);
	}
	
	
	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width,
			int height) {
		GL3 gl= (GL3) arg0.getGL();
		aspect= (double)width/(double)height;
		System.out.println("Aspect : "+ aspect);
		this.setPerspective(60f, (float) aspect,near, far, proj);
		projMat.loadIdentity();
		projMat.loadMatrix(proj);
		GeometryTransformPipeline.getProjectionMatrixStack().loadMatrix(proj);
		currentWidth=width;
		currentHeight=height;
	}
	//--------------------------------------- END GL Event Listener Methods-----------------------------------------------------

	/* 
	 * This method responds to the timer contained in this class
	 * and calls tick for every object that is IAnimated. Finally, 
	 * the canvas is redrawn. Also, and asynchronous tasks may be synchronized 
	 * within this method.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		myCanvas.display();
		
	}// action performed 
	
	/**
	 * Builds keybinding:
	 * Q moves down
	 * W moves forward
	 * E moves up
	 * A moves left
	 * S moves backwards
	 * D moves right
	 * Z rolls left
	 * X stops camera movement
	 * C rolls right
	 * SPACE toggles axis grid display
	 * UP Arrow looks down (inverted Y axis)
	 * DOWN Arrow looks up (inverted Y axis)
	 * LEFT Arrow looks left
	 * RIGHT Arrow looks right
	 * ESC closes program
	 */
	private void buildKeyBindings(){
		KeyStroke upArrow_Key = KeyStroke.getKeyStroke("UP");
		input_Map.put(upArrow_Key, "up");
		action_Map.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onUp=true;
            }});
		
		KeyStroke upArrow_KeyR = KeyStroke.getKeyStroke("released UP");
		input_Map.put(upArrow_KeyR, "upR");
		action_Map.put("upR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onUp=false;
            }});
		
		KeyStroke downArrow_Key = KeyStroke.getKeyStroke("DOWN");
		input_Map.put(downArrow_Key, "down");
		action_Map.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onDown=true;
            }});
		
		KeyStroke downArrow_KeyR = KeyStroke.getKeyStroke("released DOWN");
		input_Map.put(downArrow_KeyR ,"downR");
		action_Map.put("downR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onDown=false;
            }});
		
		KeyStroke rightArrow_Key = KeyStroke.getKeyStroke("RIGHT");
		input_Map.put(rightArrow_Key, "right");
		action_Map.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onRight=true;
            }});
		
		KeyStroke rightArrow_KeyR = KeyStroke.getKeyStroke("released RIGHT");
		input_Map.put(rightArrow_KeyR, "rightR");
		action_Map.put("rightR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onRight=false;
            }});
		
		KeyStroke leftArrow_Key = KeyStroke.getKeyStroke("LEFT");
		input_Map.put(leftArrow_Key, "left");
		action_Map.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onLeft=true;
            }});
		
		KeyStroke leftArrow_KeyR = KeyStroke.getKeyStroke("released LEFT");
		input_Map.put(leftArrow_KeyR, "leftR");
		action_Map.put("leftR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onLeft=false;
            }});
		
		
		KeyStroke forward_Key= KeyStroke.getKeyStroke("W");
		input_Map.put(forward_Key, "W");
		action_Map.put("W", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onW=true;
            }});
		
		KeyStroke forward_KeyR= KeyStroke.getKeyStroke("released W");
		input_Map.put(forward_KeyR, "WR");
		action_Map.put("WR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onW=false;
            }});
		
		KeyStroke backward_Key= KeyStroke.getKeyStroke("S");
		input_Map.put(backward_Key, "S");
		action_Map.put("S", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onS=true;
            }});
		
		KeyStroke backward_KeyR= KeyStroke.getKeyStroke("released S");
		input_Map.put(backward_KeyR, "SR");
		action_Map.put("SR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onS=false;
            }});
		
		KeyStroke moveRight_Key= KeyStroke.getKeyStroke("D");
		input_Map.put(moveRight_Key, "D");
		action_Map.put("D", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onD=true;
            }});
		
		KeyStroke moveRight_KeyR= KeyStroke.getKeyStroke("released D");
		input_Map.put(moveRight_KeyR, "DR");
		action_Map.put("DR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onD=false;
            }});
		
		KeyStroke moveLeft_Key= KeyStroke.getKeyStroke("A");
		input_Map.put(moveLeft_Key, "A");
		action_Map.put("A", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onA=true;
            }});
		
		KeyStroke moveLeft_KeyR= KeyStroke.getKeyStroke("released A");
		input_Map.put(moveLeft_KeyR, "AR");
		action_Map.put("AR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onA=false;
            }});
	
		KeyStroke moveUp_Key= KeyStroke.getKeyStroke("Q");
		input_Map.put(moveUp_Key, "Q");
		action_Map.put("Q", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onQ=true;
            }});
		
		KeyStroke moveUp_KeyR= KeyStroke.getKeyStroke("released Q");
		input_Map.put(moveUp_KeyR, "QR");
		action_Map.put("QR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onQ=false;
            }});
		
		KeyStroke moveDown_Key= KeyStroke.getKeyStroke("E");
		input_Map.put(moveDown_Key, "E");
		action_Map.put("E", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onE=true;
            }});
		
		KeyStroke moveDown_KeyR= KeyStroke.getKeyStroke("released E");
		input_Map.put(moveDown_KeyR, "ER");
		action_Map.put("ER", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onE=false;
            }});
		
		KeyStroke axis_Key= KeyStroke.getKeyStroke("SPACE");
		input_Map.put(axis_Key, "space");
		action_Map.put("space", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	if(drawAxisOn) drawAxisOn=false;
            	else drawAxisOn=true;
            }});
		
		KeyStroke Z_Key= KeyStroke.getKeyStroke("Z");
		input_Map.put(Z_Key, "Z");
		action_Map.put("Z", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onZ=true;
            }});

		KeyStroke Z_KeyR= KeyStroke.getKeyStroke("released Z");
		input_Map.put(Z_KeyR, "ZR");
		action_Map.put("ZR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onZ=false;
            }});
		
		KeyStroke C_Key= KeyStroke.getKeyStroke("C");
		input_Map.put(C_Key, "C");
		action_Map.put("C", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onC=true;
            }});
		
		KeyStroke C_KeyR= KeyStroke.getKeyStroke("released C");
		input_Map.put(C_KeyR, "CR");
		action_Map.put("CR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	onC=false;
            }});
				
		KeyStroke x_Key= KeyStroke.getKeyStroke("X");
		input_Map.put(x_Key, "x");
		action_Map.put("x", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	myCamera.allStop();
 
            }});
		
		//close program
		KeyStroke esc_Key=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		input_Map.put(esc_Key, "escape");
		action_Map.put("escape", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	thisJFrame.dispose();
            	System.exit(1);
            }});
		
	}// end keybinding buider
	
	/**
	 * Sets a perspective matrix for passed params into mat.
	 * @param fovY
	 * @param aspect
	 * @param near
	 * @param far
	 * @param mat
	 */
	private void setPerspective(float fovY, float aspect, float near, float far, Matrix3D mat){
		double xmin, xmax, ymin, ymax; 					//clipping plane
		/*
		 *  compute coords of clipping plane corners
		 */
		ymax= near*(Math.tan(fovY/2.0 * (Math.PI/180)));
		ymin= -ymax;
		xmin= ymin*aspect;
		xmax= -xmin;
		/*
		 * construct projection matrix
		 */
		mat.setToIdentity();
		mat.setElementAt(0, 0, (2.0f*near)/(xmax-xmin));
		mat.setElementAt(1,1,(2.0f*near)/(ymax-ymin));
		mat.setElementAt(0, 2, 0);
		mat.setElementAt(1, 2, 0);
		mat.setElementAt(2,2, -((far+near)/(far-near)));
		mat.setElementAt(3, 2, -1.0f);
		mat.setElementAt(2, 3, -((2.0f*far*near)/(far-near) ) );
		mat.setElementAt(3,3, 0.0f);
		
	}
	
	/**
	 * Returns a matrix containing the rotations and translations
	 * of the camera.
	 * @param cam
	 * @return
	 */
	private Matrix3D lookAt(Camera cam){
		Matrix3D vtm= new Matrix3D();   // contains rotaion values
		Matrix3D trans= new Matrix3D(); // contains translation values.
		trans.setToIdentity();
		trans.setElementAt(0, 3, cam.getLocation().getX());
		trans.setElementAt(1, 3, cam.getLocation().getY());
		trans.setElementAt(2, 3, cam.getLocation().getZ());
		vtm.setRow(0, cam.getRightAxis());     // U dir (X)
		vtm.setRow(1, cam.getUpAxis());        // V dir (Y)
		vtm.setRow(2, cam.getViewDirection()); // N (Z)
		vtm.concatenate(trans);
		return vtm;
	}
	 
	
	/**
	 * Creates a JOGL texture with mip mapping enable or disabled
	 * @param gl
	 * @param fileName
	 * @param mipMap mipMap Flag
	 * @return
	 */
	private Texture createTexture(GL3 gl,String fileName, boolean mipMap)
	{
		String path = "Textures" + File.separator + fileName;
		
		Texture tex = null;
		try{
			tex = TextureIO.newTexture(new File(path), mipMap);
					
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(0);
		}
		if (mipMap){
			gl.glTexParameterf(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			gl.glTexParameterf(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_NEAREST);
			
			gl.glGenerateMipmap(GL3.GL_TEXTURE_2D);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_BASE_LEVEL, 0);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAX_LEVEL, 10);
		}
		else{
			tex.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
			tex.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		}
		
		
		return tex;
	}
	
	private void initLights(GL3 gl){
		 light1.setName("light");
		 light1.setSpecular(light1_specular);
		 light1.setDiffuse(light1_diffuse);
		 light1.setAmbient(light1_ambient);
		 light1.setPosition( new Point3D(0,4,0));
		 theLight.translate(light1.getPosition().getX(), light1.getPosition().getY(), light1.getPosition().getZ());
		 light1Locs= new LightAttributesLocs(gl, light1.getName(), ps.getRenderProgramId());
	}
	
private void installLighting(GL3 gl){
		gl.glUseProgram(ps.getRenderProgramId());
		Matrix3D viewMat= this.lookAt(myCamera);
		
		// global ambient light
		float [] ambVals= new float[4];
		ambVals[0]=(float) globalAmbientLight.getX();
		ambVals[1]=(float) globalAmbientLight.getY();
		ambVals[2]=(float) globalAmbientLight.getZ();
		ambVals[3]=(float) globalAmbientLight.getW();
		gl.glUniform4fv(light1Locs.getGlobalAmientLoc(),1, ambVals,0);

		
		
		// light 1
		Vector3D position= new Vector3D(light1.getPosition().getX(),light1.getPosition().getY(),light1.getPosition().getZ(),1);
		Vector3D eyePosition=position.mult(viewMat);
		float[] points= new float[3];
		points[0]=(float) eyePosition.getX();
		points[1]=(float) eyePosition.getY();
		points[2]=(float) eyePosition.getZ();
		
		
		
		gl.glUniform4fv(light1Locs.getAmbLoc(), 1, light1.getAmbient(),0);
		gl.glUniform4fv(light1Locs.getDiffLoc(), 1, light1.getDiffuse(),0);
		gl.glUniform4fv(light1Locs.getSpecLoc(), 1, light1.getSpecular(),0);
		gl.glUniform3fv(light1Locs.getPosLoc(), 1, points,0);
	
	}
	
	private void renderBack(GL3 gl){
		gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, backFaceFrameBuff[0], 0);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		//draw
	}

	private void create_TestVolume(GL3 gl){
		int size=100*100*100;
		
		float[] data= new float[size*4];
		
		// not sure about the order here
		for(int x=0; x<1000000;x++)
		{
			
					if (x<500000)
					{
						data[x*4]=1.0f;
						data[x*4+1]=0.0f;
						data[x*4+2]=0.0f;
						data[x*4+3]=0.4f;
					}
					else
					{
						data[x*4]=0.0f;
						data[x*4+1]=0.0f;
						data[x*4+2]=1.0f;
						data[x*4+3]=1.0f;
					}
			
		}
		
		gl.glGenTextures(1, volumeTextureID,0);
		gl.glBindTexture(GL3.GL_TEXTURE_3D, volumeTextureID[0]);
		
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
		
		FloatBuffer buffer=FloatBuffer.wrap(data);
		gl.glTexImage3D(GL3.GL_TEXTURE_3D, 0,GL3.GL_RGBA, 100, 100,100,0, GL3.GL_RGBA,GL3.GL_FLOAT,buffer);
	}
	
	private void createTestVolume2(GL3 gl){
		int xSize=128;
		int ySize=128;
		int zSize=128;
		
		float[] data= new float[xSize*ySize*zSize*4];
		
		for(int x=0; x<xSize;x++)
		{
			for(int y=0; y<ySize;y++)
			{
				for(int z=0; z<zSize;z++)
				{
					
					data[(x*4)   + (y * ySize * 4) + (z * zSize * ySize * 4)] = (z%250)/255.0f;
					data[(x*4)+1 + (y * ySize * 4) + (z * zSize * ySize * 4)] = (y%250)/255.0f;
					data[(x*4)+2 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 230/255.0f;
					
					float length =	(float) Math.sqrt( (x-(xSize-20))*(x-(xSize-20))+ (y-(ySize-30))*(y-(ySize-30))+(z-(zSize-20))*(z-(zSize-20)) );
					
					//System.out.println(length);
					boolean test = (length < 42);
					if(test)
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 0;

					length =	(float) Math.sqrt( (x-(xSize/2))*(x-(xSize/2))+ (y-(ySize/2))*(y-(ySize/2))+(z-(zSize/2))*(z-(zSize/2)) );
					
					test = (length < 24);
					if(test)
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 0;
					
					if(x > 20 && x < 40 && y > 0 && y < ySize && z > 10 &&  z < 50)
					{
						
						data[(x*4)   + (y * ySize * 4) + (z * zSize * ySize * 4)] = 100/255.0f;
					    data[(x*4)+1 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					    data[(x*4)+2 + (y * ySize * 4) + (z * zSize * ySize * 4)] = (y%100)/255.0f;
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					}

					if(x > 50 && x < 70 && y > 0 && y < ySize && z > 10 &&  z < 50)
					{
						
						data[(x*4)   + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					    data[(x*4)+1 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					    data[(x*4)+2 + (y * ySize * 4) + (z * zSize * ySize * 4)] = (y%100)/255.0f;
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					}

					if(x > 80 && x < 100 && y > 0 && y < ySize && z > 10 &&  z < 50)
					{
						
						data[(x*4)   + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					    data[(x*4)+1 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 70/255.0f;
					    data[(x*4)+2 + (y * ySize * 4) + (z * zSize * ySize * 4)] = (y%100)/255.0f;
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 250/255.0f;
					}
					
					
					length =	(float) Math.sqrt( (x-(24))*(x-(24))+ (y-(24))*(y-(24))+(z-(24))*(z-(24)) );
					test = (length < 40);
					if(test)
						data[(x*4)+3 + (y * ySize * 4) + (z * zSize * ySize * 4)] = 0;
					
				}
			}
		}
		gl.glGenTextures(1, volumeTextureID,0);
		gl.glBindTexture(GL3.GL_TEXTURE_3D, volumeTextureID[0]);
		
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
		for(int i=xSize*ySize*zSize*4-1; i>xSize*ySize*zSize*4-1000; i--) System.out.println(data[i]);
		FloatBuffer buffer=FloatBuffer.wrap(data);
		gl.glTexImage3D(GL3.GL_TEXTURE_3D, 0,GL3.GL_RGBA, xSize, ySize,zSize,0, GL3.GL_RGBA,GL3.GL_FLOAT,buffer);
		
	}
	/**
	 * Check openGL error stack for errors at a location specified by the string passed.
	 * Static so it is available to all.
	 * @param gl
	 * @param location
	 */
	static void errorCheck(GL3 gl,String location){
		if (OPENGL_DEBUG_MODE){
			int eCode = gl.glGetError();
			if(eCode != GL.GL_NO_ERROR){
				System.out.println("OpenGL reports an error: " + eCode+ " " +glu.gluErrorString(eCode) +" at "+ location);
				
			}
		}
	}// end error check
}
