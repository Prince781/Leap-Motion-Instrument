/***************************
 * Leap Instrument (TBN)
****************************/

import com.leapmotion.leap.*;
import java.io.*;

public class LeapInstrument extends Listener {
	GUI gui; //the interface for the program
	Instrument instrument;
	public void onInit(Controller controller) {
		//leap motion daemon initialization
		System.out.println("Listener initialized.");
	}
	public void onConnect(Controller controller) {
		//leap motion connect
		System.out.println("Leap Motion is connected.");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}
	public void onFrame(Controller controller) {
		//leap motion frame - this is where main interaction is
		Frame frame = controller.frame(); //get frame instance of controller
		for (Finger finger : frame.fingers()) {
			if (!finger.isFinger()) continue;
			Vector tipPos = finger.tipPosition();
			float x_off = tipPos.getX(); //horizontal distance across the instrument
			float y_off = tipPos.getY(); //y offset
			float offset = y_off - instrument.getInstrumentHeight();
			float veloc = -finger.tipVelocity().getY();
			if (offset <= instrument.getDepressionAmount() && offset >= 0f && veloc > 0)
				instrument.play(x_off, veloc);
		}
	}
	public void onDisconnect(Controller controller) {
		//leap motion disconnect
		System.out.println("Leap Motion has been disconnected.");
	}
	public void onExit(Controller controller) {
		System.out.println("Good bye.");
	}
	public LeapInstrument() throws Exception {
		gui = new GUI();
		instrument = new Instrument("Piano");
		gui.instrumentUpdate(instrument);
	}
	public static void main(String[] args) throws Exception {
		LeapInstrument listener = new LeapInstrument();
		Controller con = new Controller(); //leap motion controller
		
		con.addListener(listener);
		
		System.out.println("Press any key to quit...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Quitting");
		con.removeListener(listener);
	}
}
