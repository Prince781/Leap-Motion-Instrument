import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

import javax.media.j3d.*;
import java.awt.*;
import javax.swing.*;

public class GUI {
	private String windowName = "Musil";
	public JFrame window;
	private int[] windowDimensions = {1000, 600};
	private GraphicsConfiguration cfg;
	private Canvas3D canvas;
	private SimpleUniverse universe;
	private Dimension screen;
	private BranchGroup world;
	public GUI() {
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		window = new JFrame(windowName);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds((screen.width-windowDimensions[0])/2,(screen.height-windowDimensions[1])/2,windowDimensions[0],windowDimensions[1]);
		cfg = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(cfg);
		window.add(canvas);
		window.setVisible(true);
		world = new BranchGroup();
		world.addChild(new ColorCube(0.3f));
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(world);
	}
	public void instrumentUpdate(Instrument instrument) {
		System.out.println("Instrument update being called.");
	}
}