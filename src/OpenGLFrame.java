

import graphicslib3D.GeometryTransformPipeline;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;
import graphicslib3D.light.PositionalLight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;



import java.util.Hashtable;

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
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	public static Matrix3D projM;
	
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
    VolumeRaycaster theVolume;
    
    //lights
    private PositionalLight light1= new PositionalLight();
	float[] light1_specular= {.8f,.8f,.8f,1};
	float[] light1_diffuse= {1f,1f,1f,1};
	float[] light1_ambient={0,0,0,1};
	private boolean light1On=true;
    private LightAttributesLocs light1Locs;
  //value of global ambient light
    private Vector3D globalAmbientLight= new Vector3D(.1f,.1f,.1f,1); 
    private int counter=0;
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
    
    //extra gui
    JSlider upperCutoff;
    JSlider lowerCutoff;
    JLabel  upperVal;
    JLabel  lowerVal;
    JColorChooser colorPicker;
    //******************Volume Rendering Vars******************************


    
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
		
		
		upperCutoff = new JSlider(JSlider.VERTICAL,0, 1000,1000);
		//upperCutoff.addChangeListener(this);
		upperCutoff.setMajorTickSpacing(100);
		upperCutoff.setPaintTicks(true);
		upperCutoff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				upperVal.setText("Upper Bound: "+upperCutoff.getValue()/1000f);
				theVolume.setUpperCutoff(upperCutoff.getValue()/1000f);
				//System.out.println("changed."+source.getValue());
			}
		});
		


		commandPanel.add(upperCutoff);
		
		upperVal= new JLabel("upper bound : 1");
		commandPanel.add(upperVal);
		
		lowerCutoff = new JSlider(JSlider.VERTICAL,0, 1000, 0);
		lowerCutoff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				lowerVal.setText("Lower Bound: "+lowerCutoff.getValue()/1000f);
				theVolume.setLowerCutoff(lowerCutoff.getValue()/1000f);
				//System.out.println("changed. "+source.getValue());
			}
		});
		//upperCutoff.addChangeListener(this);
		lowerCutoff.setMajorTickSpacing(100);
		lowerCutoff.setPaintTicks(true);
		commandPanel.add(lowerCutoff);
		lowerVal= new JLabel("Lower Bound: 0");
		commandPanel.add(lowerVal);

		final JLabel rLabel=new JLabel("Red: 1");
		final JSlider r=new JSlider(JSlider.VERTICAL,0, 100, 1);
		r.setMajorTickSpacing(100);
		r.setPaintTicks(true);
		r.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				rLabel.setText("Red: "+r.getValue()/100f);
				float[] color=theVolume.getColor();
				color[0]=source.getValue()/100f;
				theVolume.setColor(color);
			}
		});
		commandPanel.add(r);
		commandPanel.add(rLabel);
		
		final JLabel gLabel=new JLabel("Green: 1");
		final JSlider g=new JSlider(JSlider.VERTICAL,0, 100, 1);
		g.setMajorTickSpacing(100);
		g.setPaintTicks(true);
		g.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				gLabel.setText("Green: "+source.getValue()/100f);
				float[] color=theVolume.getColor();
				color[1]=source.getValue()/100f;
				theVolume.setColor(color);
				//System.out.println("changed. "+source.getValue());
			}
		});
		commandPanel.add(g);
		commandPanel.add(gLabel);
		
		final JLabel bLabel=new JLabel("Blue: 1");
		final JSlider b=new JSlider(JSlider.VERTICAL,0, 100, 1);
		b.setMajorTickSpacing(100);
		b.setPaintTicks(true);
		b.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				bLabel.setText("Blue: "+source.getValue()/100f);
				float[] color=theVolume.getColor();
				color[2]=source.getValue()/100f;
				System.out.println(color[0]+" "+color[1]+" "+color[2]);
				theVolume.setColor(color);
				System.out.println("changed. "+source.getValue()/100f);
			}
		});
		commandPanel.add(b);
		commandPanel.add(bLabel);
	}
	
	//--------------------------------------------GL Event Listener Methods---------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable arg0) {
		//System.out.println("Counter : "+counter);
		//counter++;
		//theBox.rotate(0, .1, 0);
		
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
		OpenGLFrame.projM=(Matrix3D) proj.clone();
		//installLighting(gl);
		gl.glUseProgram(identityShader.getProgramID());
		//---------set uniforms for the identity shader
		ps.setProjectionMatrix(proj);
		double[]  projVals= proj.getValues();									//get projection matrix
		float[] projValsf= new float[projVals.length];
		for(int i=0; i<projVals.length;i++)projValsf[i]=(float) projVals[i];	//convert to floats
		gl.glUniformMatrix4fv(IdentityLocs.getProjLoc(), 1,false, projValsf,0); //send projection matrix to shader
		
		//********************Render the backface to the framebuffer texture***********************************

		
		installLighting(gl);
		theVolume.Draw(arg0);
		
	    
		//*****************************************************************************************************
		gl.glUseProgram(identityShader.getProgramID());
		
		//theBox.renderFrontFace(true);
		//theBox.draw(arg0);
		
		if (wireFrameOn) gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);  //enable wirefram if set
		else gl.glPolygonMode( GL3.GL_FRONT_AND_BACK, GL3.GL_FILL );
		

		if (drawAxisOn) {
			grid.draw(arg0);										//Draw axis grid if enable
		}
		
		theLight.draw(arg0);
		//ps.draw(gl, myCamera.getLocation(), myCamera.getUpAxis());
		
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
		this.setPerspective(50f, 1f,.01f, 10f, proj);
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
		 * init drawables
		 */
		grid= new Grid(18, gl3);
		grid.scale(2, 2, 2);

		
		
		theLight= new LightSphere(gl3, new Point3D(0,0,0), 20, .1, Color.YELLOW);
		
		//RandomTexture test= new RandomTexture(64, gl3);
		ps=new ParticleSystem(arg0, createTexture(gl3, "two.jpg", true), 1000);
		ps.scale(100, 100, 100);
		
		errorCheck(gl3,"end init");							//check for errors
		
		
		
		theVolume= new VolumeRaycaster(arg0,myCanvas.getHeight(),myCanvas.getWidth(),"head.raw",256,256,113 , 16, true,true,this);
		theVolume.addTransferFuncton(TransferFunctionFactory.getHead2(), gl3);
		
		theVolume.nextTransferFunction();
		
		theVolume.rotate(0, 0, 90);
		theVolume.rotate(0, 90, 0);
		//theVolume.setScale(1f, 1f, .61f);
		
		//createTest Volume
		//createTestVolume2(gl3);
		//this.createEngineVolume(gl3);
		
		
		
		//engine
		//theVolume= new VolumeRaycaster(arg0,myCanvas.getHeight(),myCanvas.getWidth(),"Engine.raw",256,256,256 , 8, false,false, this);
		//theVolume.addTransferFuncton(TransferFunctionFactory.getEngine1(), gl3);
		//theVolume.nextTransferFunction();
		//Orange
		//theVolume= new VolumeRaycaster(arg0,myCanvas.getHeight(),myCanvas.getWidth(),"orange.raw",256,256,64 , 8, false,true, this);
		//theVolume.addTransferFuncton(TransferFunctionFactory.getOrange1(), gl3);
		//theVolume.nextTransferFunction();
		
		initLights(gl3);
		
		//TransferFunction tester= new TransferFunction(10);
//		tester.addRGBPegPoint(.1f, 1f, 1f, 1f);
//		tester.addAlphaPegPoint(.5f,1f);
//		tester.addAlphaPegPoint(.7f,0f);
//		float [] t=tester.getTransferArray();
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
		 light1.setPosition( new Point3D(0,2,0));
		 theLight.translate(light1.getPosition().getX(), light1.getPosition().getY(), light1.getPosition().getZ());
		 light1Locs= new LightAttributesLocs(gl, light1.getName(),theVolume.getProgramID());
	}
	
private void installLighting(GL3 gl){
		gl.glUseProgram(theVolume.getProgramID());
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
	
	
	public float[] getLightPosition(){
		float[] result = new float[3];
		result[0]=(float) myCamera.getLocation().getX();
		result[1]=(float) myCamera.getLocation().getY();
		result[2]=(float) myCamera.getLocation().getZ();
		return result;
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
