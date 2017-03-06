
// External imports
import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;


// Local imports
// None

/**
 * Example application that demonstrates how to put together a single-threaded
 * rendering system.
 */
public class ThreeDStarter extends Frame
implements GLEventListener, KeyListener

{
	int xmovement ,ymovement = 0;
	int Z=0;
	int Ztranslate = 0;
	int zoff = 0;
	int angle = 0;
	int inc=1;
	boolean fired=false;
	boolean left = false; 
	boolean right= false ;
	int numberFired = 0;
	int fireDistance=1;
	
	public ThreeDStarter()
	{

		super("GameProject");

		setLayout(new BorderLayout());

		setSize(600, 770);
		setLocation(200, 400);

		
		
		
		//Close the window when told to by the OS
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Need to set visible first before starting the rendering thread due
		// to a bug in JOGL. 

		setVisible(true);

		setupJOGL();


	}

	//---------------------------------------------------------------
	// Methods defined by GLEventListener
	//---------------------------------------------------------------

	/**
	 * Called by the drawable immediately after the OpenGL context is
	 * initialized; the GLContext has already been made current when
	 * this method is called.
	 *
	 * @param drawable The display context to render to
	 */
	@Override
	public void init(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();     
		gl.glClearColor(0, 0, 0, 0);     
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		//gl.glFrustum( -1, 1, -1, 1, -50, 50);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		//gl.glClearDepth(1.0f);                   // Set background depth to farthest
		gl.glEnable(GL.GL_DEPTH_TEST);   // Enable depth testing for z-culling
		gl.glDepthFunc(GL.GL_LEQUAL);    // Set the type of depth-test
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);   // Enable smooth shading
		gl.glLoadIdentity();
		
	}

	/**
	 * Called by the drawable when the surface resizes itself. Used to
	 * reset the viewport dimensions.
	 *
	 * @param drawable The display context to render to
	 */
	@Override
	public void reshape(GLAutoDrawable drawable,
			int x,
			int y,
			int width,
			int height)
	{
		//GLU glu = new GLU();
		GL2 gl = drawable.getGL().getGL2();
		GLU glu=GLU.createGLU(gl);
		// Compute aspect ratio of the new window
		if (height == 0) height = 1;                // To prevent divide by 0
		float aspect = (float)width / (float)height;

		// Set the viewport to cover the new window


		// Set the aspect ratio of the clipping volume to match the viewport
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);  // To operate on the Projection matrix
		gl.glLoadIdentity(); 
		gl.glViewport(0, 0, width, height);
		glu.gluPerspective(45, aspect, 20f, 160);
		gl.glPushMatrix();
		glu.gluLookAt( 20,-5,60,  10, 0,0,  0,1,0 ); 
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();   
		
		
	}


	/**
	 * Called by the drawable when the display mode or the display device
	 * associated with the GLDrawable has changed
	 */
	public void displayChanged(GLAutoDrawable drawable,
			boolean modeChanged,
			boolean deviceChanged)
	{
	}

	/**
	 * Called by the drawable to perform rendering by the client.
	 *
	 * @param drawable The display context to render to
	 */

	@Override
	public void display(GLAutoDrawable drawable) {   	
		//update(drawable);
		ship(drawable);
		
	}

	// argument added to allow updating the perspective
	public void update (GLAutoDrawable drawable){
		//angle=(angle+inc) % 360;
		GL2 gl = drawable.getGL().getGL2();
		GLU glu=GLU.createGLU(gl);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glPushMatrix();
		
	}

	public void ship(GLAutoDrawable drawable)
	{

		
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear color and depth buffer
		float X_CENTRE=6, LENGTH=3,Y_CENTRE=2;
		
		////////////////////////
		////////////////////////
		
		gl.glPushMatrix(); 
		gl.glScalef(.05f, .05f, .05f);
		gl.glTranslated(xmovement, -350, Ztranslate);
		//zoff=0;              
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		
		//specify the vertices to draw Ship in 2d space
		gl.glColor3f(1.0f, 0.0f, 1.0f);
		gl.glVertex2f( X_CENTRE + LENGTH * 0, Y_CENTRE + LENGTH * 12); 
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glVertex2f( X_CENTRE - LENGTH * 8, Y_CENTRE - LENGTH * 8);
		gl.glColor3f(0.0f, 1.0f, 1.0f);
		gl.glVertex2f( X_CENTRE - LENGTH  * 0, Y_CENTRE - LENGTH * 0);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glVertex2f( X_CENTRE + LENGTH * 8, Y_CENTRE - LENGTH * 8);
		gl.glPopMatrix();
		gl.glEnd();
		
		
		
		if(fireDistance>800) 
			fired=false;
		
		
		if(fired){
			GLU glu=GLU.createGLU(gl);
			GLUquadric quadratic = glu.gluNewQuadric();
			gl.glTranslatef( X_CENTRE , LENGTH * 12+(fireDistance+=10), 0 ); 
			//flip the cylinder to point up along the Y instead of the Z
			gl.glRotatef(90.0f, 1f, 0.0f, 0f);
			glu.gluCylinder(quadratic,1f,5f,10f,32,32);
	
			//glu.gluDeleteQuadric( quadratic );
			
			
		 }
	
		
		gl.glPopMatrix();
		////////////////////////
		////////////////////////
		//gl.glPushMatrix();
	
		

		// Render a pyramid consists of 4 triangles 
		gl.glPushMatrix();
		gl.glRotatef(180,0,0,0);
		
		gl.glBegin(GL.GL_TRIANGLES);           // Begin drawing the pyramid with 4 triangles
		// Front
		gl.glColor3f(1.0f, 0.0f, 0.0f);     // Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f);     // Green
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f);     // Blue
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Right
		gl.glColor3f(1.0f, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f);     // Blue
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f);     // Green
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Back
		gl.glColor3f(1.0f, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f);     // Green
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f);     // Blue
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);

		// Left
		gl.glColor3f(1.0f,0.0f,0.0f);       // Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f,0.0f,1.0f);       // Blue
		gl.glVertex3f(-1.0f,-1.0f,-1.0f);
		gl.glColor3f(0.0f,1.0f,0.0f);       // Green
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);
		gl.glEnd();   // Done drawing the pyramid
		gl.glPopMatrix();




	}

	//---------------------------------------------------------------
	// Local methods
	//---------------------------------------------------------------

	/**
	 * Create the basics of the JOGL screen details.
	 */
	private void setupJOGL()
	{
		GLProfile glp = GLProfile.getDefault();  	
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);

		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);

		add(canvas, BorderLayout.CENTER);
		FPSAnimator animator = new FPSAnimator(canvas, 70);
		animator.start();

	}

	public static void main(String[] args)
	{
		ThreeDStarter demo = new ThreeDStarter();
		demo.setVisible(true);
		
	}
	
	

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		 
		int key = e.getKeyCode();  // Tells which key was pressed.
		if ( key == KeyEvent.VK_HOME )
			System.exit(0);
		else if (key == KeyEvent.VK_SPACE && !fired){
			playSound("blaster.wav");
			fired=true;
			fireDistance = 3;
			}
		else if (key == KeyEvent.VK_LEFT){
			xmovement-=30;
		}else if (key == KeyEvent.VK_RIGHT){
			xmovement+=30;
		}else if (key == KeyEvent.VK_UP){
			Ztranslate+=30;
		}else if (key == KeyEvent.VK_DOWN){
			Ztranslate-=30;
		}
		
		repaint();
	}

	/**
	 * Called when the user types a character.
	 */
	@Override
	public void keyTyped(KeyEvent e) { 
		char ch = e.getKeyChar();  // Which character was typed.
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		char ch = e.getKeyChar();  // Which character was typed.
	}

	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
							getClass().getResourceAsStream("blaster.wav" ));
					clip.open(inputStream);
					clip.start(); 
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}