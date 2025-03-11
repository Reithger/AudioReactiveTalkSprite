package main;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import model.effects.ChangeFactory;
import model.profile.Profile;
import ui.View;

/**
 * 
 * TODO: Function to make the audio loudness display on screen so you can get a sense of
 * how loud you are for customizing your audio thresholds
 * 
 */

public class Controller implements EventProcessor{
	
//---  Constants   ----------------------------------------------------------------------------
	
	public final static String CONFIG_FILE_PATH = "./pngassets/";

//---  Instance Variables   -------------------------------------------------------------------
	
	private static int DEFAULT_WIDTH = 250;
	private static int DEFAULT_HEIGHT = 250;
	
	private static String SKULL_PATH = "./pngassets/Skull Test/skull.png";

	private static AudioReading audio;
	private static View view;
	
	/** Either this should be a list of Profiles (and thus a manager object) or we let the program
	 *  read config info from files to inform the user of various Profiles and only have one loaded
	 *  in at a time.
	 *  
	 *  Potential plans to have things like Tone/Tambre change significant portions of the talk sprite
	 *  may require we have multiple Profiles loaded in at once for convenience.
	 *  
	 */
	private static Profile profile;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Controller() {
		File f = new File(CONFIG_FILE_PATH);
		f.mkdirs();
		audio = new AudioReading();
		view = new View(DEFAULT_WIDTH, DEFAULT_HEIGHT, this);
		ReadWriteConfig.populateConfigDefaultValues();
		
		profile = ReadWriteConfig.readInProfile(ReadWriteConfig.getDefaultProfile());
		profile.populateAudioConfigImages(view);
	}
	
	private Profile makeStarterProfile() {
		profile = new Profile("Skull");
		try {
			profile.addAudioConfig("Quiet", 0, SKULL_PATH, 0);
			profile.addAudioConfigChange("Quiet", ChangeFactory.KEYWORD_DARKEN_FILTER, ".6");
			profile.addAudioConfig("Speaking", 20, SKULL_PATH, 7);
			profile.addAudioConfigChange("Speaking", ChangeFactory.KEYWORD_SHAKE_EFFECT, "1", "1");
			profile.addAudioConfig("Loud", 300, SKULL_PATH, 12);
			profile.addAudioConfigChange("Loud", ChangeFactory.KEYWORD_RED_FILTER);
			profile.populateAudioConfigImages(view);
			
			return profile;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void initiateRegularUpdate() {
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			double val = audio.getCurrentAudio();
			Image img = profile.getAppropriateAudioImage((int)val);
			if(img != null)
				view.updateSpriteDisplay(img);
		}
	}
	
	public void updateActiveProfile(String profileName) {
		profile = ReadWriteConfig.readInProfile(profileName);
		if(ReadWriteConfig.getStatusDefaultProfileAutoset()) {
			try {
				ReadWriteConfig.establishDefaultProfile(profileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void processEvent(int code) {
		switch(code) {
			case 0:
				view.promptConfigMenu();
				break;
		}
	}

	
}
