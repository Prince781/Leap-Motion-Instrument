import com.glyphein.j3d.loaders.milkshape.MS3DLoader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

import javax.media.j3d.*;

import java.net.URL;
import java.awt.*;

import javax.swing.*;
import javax.vecmath.Color3f;

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
		//world.addChild(new ColorCube(0.3f));
		MS3DLoader loader = new MS3DLoader();
		Scene scene;
		try {
			URL loadPath = new URL("file://localhost/"+System.getProperty("user.dir")+"/models/Left Key.ms3d");
			try { //try to load a model
				scene = loader.load(loadPath);
				BranchGroup bg = scene.getSceneGroup();
				Shape3D keyShape = (Shape3D) bg.getChild(0);
				bg.removeChild(0);
				Appearance app = new Appearance();
				Color3f objColor = new Color3f(0.7f, 0.2f, 0.8f);
				Color3f black = new Color3f(1.0f, 1.0f, 1.0f);
				app.setMaterial(new Material(objColor, black, objColor, black, 80.0f));
				keyShape.setAppearance(app);
				world.addChild(keyShape);
			} catch (Exception e) {
				System.out.println("Failed to load MS3D model.");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(world);
	}
	public void instrumentUpdate(Instrument instrument) {
		System.out.println("Instrument update being called.");
	}
}