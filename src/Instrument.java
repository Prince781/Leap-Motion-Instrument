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
		InstrumentList.InstrumentValues instrumentData = (new InstrumentList()).getInstrumentData(name);
		if (instrumentData == null)
			throw new InvalidInstrumentNameException();
		pitched = instrumentData.pitched;
		synth = MidiSystem.getSynthesizer();
		synth.open(); //crucial; opens the synthesizer
		channelNum = instrumentData.channel;
		channel = synth.getChannels()[channelNum];
		type = instrumentData.instrumentType;
		fingered = instrumentData.fingered;
		fingerHandDistance = instrumentData.spacing;
		instrumentHeight = instrumentData.instrumentHeight;
		depressionAmount = instrumentData.heightWindow;
		velocityThreshold = instrumentData.velocityThreshold;
	}
}

class InstrumentList {
	public static class InstrumentValues {
		public final int channel;
			//the current MIDI channel of the instrument
		public final int instrumentType;
			//the current instrument type (for percussion, usually)
		public final boolean pitched;
			//whether or not the instrument is pitched
		public final boolean fingered;
			//whether or not the instrument is fingered
		public final float spacing;
			//the optimal distance (in mm) between appendages (fingers for fingered instruments, hands for drums)
		public final float instrumentHeight;
			//the minimum height (in mm) of the instrument from the Leap Motion sensor 
		public final float heightWindow;
			//the amount of tolerance given to the instrument's height +-(offset in mm)
		public final float velocityThreshold;
			//the required velocity of the user's appendages to initiate a sound from the instrument
		public InstrumentValues(int channelNum, int instrumentTypeNum, boolean isPitched, 
				boolean isFingered, float spacingAmount, float height, float heightTolerance, 
				float minimumVelocity) {
			channel = channelNum;
			instrumentType = instrumentTypeNum;
			pitched = isPitched;
			fingered = isFingered;
			spacing = spacingAmount;
			instrumentHeight = height;
			heightWindow = heightTolerance;
			velocityThreshold = minimumVelocity;
		}
	}
	private HashMap<String, InstrumentValues> instrumentData = new HashMap<String, InstrumentValues>();
	InstrumentList() {
		//put in instrument data
		instrumentData.put("Kick", new InstrumentValues(9, 35, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("HiHat", new InstrumentValues(9, 42, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("Snare", new InstrumentValues(9, 40, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("Crash", new InstrumentValues(9, 55, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("Floor Tom", new InstrumentValues(9, 41, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("Low Tom", new InstrumentValues(9, 45, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("High Tom", new InstrumentValues(9, 50, false, false, 100.0f, 90.0f, 40.0f, 2.0f));
		instrumentData.put("Piano", new InstrumentValues(0, 0, true, true, 14.0f, 90.0f, 20.0f, 5.0f));
		/* note that Acoustic Grand Piano does not have an additional note specifier after
		 * its channel information, which is only for drums (all located on Channel 9)
		 * the value at position 1 in the piano's array (which is 0) is simply there for
		 * consistency when checking instrument data upon initializing the Instrument class
		 * -----
		 * in addition, the Acoustic Grand Piano's note width is 14 mm, for an octave width of 168 mm
		 */
	}
	public InstrumentValues getInstrumentData(String name) {
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
