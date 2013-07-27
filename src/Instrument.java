import java.util.*;
import javax.sound.midi.*;

//instrument info

public class Instrument {
	/* Certain values, such as instrument type,
	 * are to be kept static from their initial
	 * declarations.
	 */
	private String name = "";
	public String getName() {
		return name;
	}
	private boolean pitched;
	public boolean isPitched() {
		return pitched;
	}
	private boolean fingered;
		/* if the instrument is fingered, then have certain special
		 * constraints, such as key depression rate, that dictate
		 * whether or not to play a sound from input
		 */
	public boolean isFingered() {
		return fingered;
	}
	private float depressionAmount;
		/* the amount that the user has to to press down (either on
		 * a keyboard or with the hand on a drum) in order to 
		 * initiate a response from the instrument
		 */
	public float getDepressionAmount() {
		return depressionAmount;
	}
	private float velocityThreshold;
		/* the MINIMUM amount of velocity, striking the keyboard, 
		 * in order for a sound to be made
		 */
	public float getVelocityThreshold() {
		return velocityThreshold;
	}
	private int type;
		/* type, a subset within channel, that is primarily used for
		 * percussion instruments such as drums
		 */
	public int getType() {
		return type;
	}
	private float fingerHandDistance;
		/* this is the default finger/hand distance necessary to change
		 * notes, such as the space between piano keys or harp
		 * strings
		 * ---
		 * values are in mm
		 */
	public float getFingerHandDistance() {
		return fingerHandDistance;
	}
	private float instrumentHeight;
		/* this is the default distance that the instrument is away
		 * from the Leap Motion's perspective (in y-coordinates)
		 * ---
		 * this shall be useful in detecting instrument events, based
		 * on y-values for fingers and hands
		 */
	public float getInstrumentHeight() {
		return instrumentHeight;
	}
	private Synthesizer synth;
	public Synthesizer getSynth() {
		return synth;
	}
	private MidiChannel channel;
	public MidiChannel getChannel() {
		return channel;
	}
	private int channelNum;
		/* this is the default channel number of the instrument
		 * that is listed in the synthesizer instrument bank
		 */
	public int getChannelNumber() {
		return channelNum;
	}
	public void play(float distance, float pressure) {
		/* simulate the playing of a note, including the effect of 
		 * finger pressure on the instrument
		 * ---
		 * distance should be the x-coordinates of the finger
		 * in mm
		 */
		pressure /= velocityThreshold;
		if (pitched) {
			if (fingered) {
				int note = (int)(60f + (distance/fingerHandDistance));
				if (channel.getPolyPressure(note) != 0)
					channel.setPolyPressure(note, (int)pressure);
				else channel.noteOn(note, (int)pressure);
				System.out.println("Playing a note on key "+note+" with pressure "+pressure+".");
			}
		} else {
			int note = (int)(0f + (distance/fingerHandDistance));
			
		}
	}
	public Instrument(String name) throws Exception {
		/* we can determine if the instrument is pitched or not,
		   based on its name */
		int[] instrumentData = (new InstrumentList()).getInstrumentData(name);
		if (instrumentData == null)
			throw new InvalidInstrumentNameException();
		pitched = instrumentData[2] == 1;
		synth = MidiSystem.getSynthesizer();
		synth.open(); //crucial; opens the synthesizer
		channelNum = instrumentData[0];
		channel = synth.getChannels()[channelNum];
		type = instrumentData[1];
		fingered = instrumentData[3] == 1;
		fingerHandDistance = instrumentData[4];
		instrumentHeight = instrumentData[5];
		depressionAmount = instrumentData[6];
		velocityThreshold = instrumentData[7];
	}
}

class InstrumentList {
	private HashMap<String, int[]> instrumentData = new HashMap<String, int[]>();
	InstrumentList() {
		//put in instrument data
		/* instrument info array
		 * 0: channel
		 * 1: instrument type (for percussion, usually)
		 * 2: pitched (0 == false, 1 == true)
		 * 3: isFingered (0 == not fingered, 1 == fingered)
		 * 4: distance (distance between hands/fingers) in mm
		 * 5: instrumentHeight (distance instrument is, or height) in mm
		 * 6: depressionAmount (amount user must "press down" with appendages) in mm
		 * 7: velocityThreshold (minimum velocity required to initiate) in mm/s
		 */
		instrumentData.put("Kick", new int[] {9, 35, 0, 0, 90, 40, 2});
		instrumentData.put("HiHat", new int[] {9, 42, 0, 0, 90, 40, 2});
		instrumentData.put("Snare", new int[] {9, 40, 0, 0, 90, 40, 2});
		instrumentData.put("Crash", new int[] {9, 55, 0, 0, 90, 40, 2});
		instrumentData.put("Floor Tom", new int[] {9, 41, 0, 0, 90, 40, 2});
		instrumentData.put("Low Tom", new int[] {9, 45, 0, 0, 90, 40, 2});
		instrumentData.put("High Tom", new int[] {9, 50, 0, 0, 90, 40, 2});
		instrumentData.put("Piano", new int[] {0, 0, 1, 1, 14, 90, 40, 2});
		/* note that Acoustic Grand Piano does not have an additional note specifier after
		 * its channel information, which is only for drums (all located on Channel 9)
		 * the value at position 1 in the piano's array (which is 0) is simply there for
		 * consistency when checking instrument data upon initializing the Instrument class
		 * -----
		 * in addition, the Acoustic Grand Piano's note width is 14 mm, for an octave width of 168 mm
		 */
	}
	public int[] getInstrumentData(String name) {
		return instrumentData.get(name);
	}
}

class InvalidInstrumentNameException extends Exception {
	private static final long serialVersionUID = 3048234708234800283L;
	private String message = "Invalid instrument name given.";
	public String getMessage() {
		return message;
	}
}

class NotADrumException extends Exception {
	private static final long serialVersionUID = 4554485839751074094L;
	private String message = "Not a drum!";
	public String getMessage() {
		return message;
	}
}

class NotAPitchedInstrumentException extends Exception {
	private static final long serialVersionUID = -9036848803641944909L;
	private String message = "Not a pitched instrument!";
	public String getMessage() {
		return message;
	}
}
