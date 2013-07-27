import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class GUI extends Thread {
	private boolean running = true;
	public JFrame window;
	public String windowTitle = "Leap Instrument Demo";
	private int[] windowDimensions = {1000, 700};
	private int[] contentDimensions = new int[2]; //inside canvas content
	private int guiControlsOffset = 250;
	private Graphics2D g2d;
	private Graphics2D b_g2d;
	private Canvas canvas;
	private BufferStrategy strategy;
	private BufferedImage background; //background buffered image
	private GraphicsConfiguration cfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	//private int[][] fingers;
	public GUI() {
		window = new JFrame(windowTitle);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//set window dimensions
		window.setBounds((screen.width-windowDimensions[0])/2,(screen.height-windowDimensions[1])/2,windowDimensions[0],windowDimensions[1]);
		
		canvas = new Canvas(cfg);
		canvas.setBounds(0, guiControlsOffset, 30, window.getSize().height-window.getInsets().top-window.getInsets().bottom-guiControlsOffset);
		window.add(canvas, 0);
		window.setVisible(true);
		window.setResizable(true);
		
		contentDimensions[0] = canvas.getSize().width;
		contentDimensions[1] = canvas.getSize().height;
		background = createBufferedImage(contentDimensions[0],contentDimensions[1],false);
		canvas.createBufferStrategy(2);
		
		do {
			strategy = canvas.getBufferStrategy();
		} while (strategy == null);
		
		start(); //start thread
	}
	private BufferedImage createBufferedImage(final int width, final int height, final boolean alpha) {
		return cfg.createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}
	private Graphics2D getBuffer() {
		if (g2d == null) try {
			g2d = (Graphics2D) strategy.getDrawGraphics();
		} catch (IllegalStateException e) {
			return null;
		}
		return g2d;
	}
	private boolean screenUpdate() {
		g2d.dispose(); //free resources
		g2d = null;
		try {
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
			return (!strategy.contentsLost());
		} catch (NullPointerException e) {
			return true;
		} catch (IllegalStateException e) {
			return true;
		}
	}
	public void run() { //thread run
		b_g2d = (Graphics2D) background.getGraphics();
		main: while (running) {
			update(); //non-rendering update
			do {
				if (!running) break main;
				Graphics2D bg = getBuffer(); //get buffered graphics
				render(b_g2d); //render stuff to background graphics
				bg.drawImage(background,0,0,null);
			} while (!screenUpdate()); //update screen 
		}
		window.dispose(); //free resources
	}
	public void instrumentUpdate(Instrument instrument) {
		/* update information based on instrument, for use later
		 * in update()
		 */
		
	}
	public void update() {
	}
	public void render(Graphics2D g) { //render using g
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, contentDimensions[0],  contentDimensions[1]);
		//TODO: add more code below:
	}
}
