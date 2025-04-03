package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.audio.AudioLevelPasser;
import main.audio.AudioReading;
import model.profile.Profile;
import ui.EventSender;
import ui.View;

/**
 * 
 * TODO: Function to make the audio loudness display on screen so you can get a sense of
 * how loud you are for customizing your audio thresholds
 * 
 * TODO: Allow user to apply multiplier filter on audio volume so that the incoming value from
 * the read_audio.py file gets adjusted (don't have to change all audio thresholds for new audio device)
 * 
 * TODO: Some way of deciding which audio input to use (how to tell which is currently in use? Python context
 * for this, too, but need to pass that config option from Java to Python (just config.txt, reload Python))
 * 
 * TODO: Send View width/height values into Profile so that we can have the resize occur between Filters
 * and Effects (changing notion to that Filters are pre-resize, Effects are post-resize)
 * 
 */

public class Controller implements EventProcessor, AudioLevelPasser {
	
//---  Constants   ----------------------------------------------------------------------------
	
	public final static String CONFIG_FILE_PATH = "./ARTS/";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static int DEFAULT_WIDTH = 250;
	private static int DEFAULT_HEIGHT = 250;
	
	private static String INTERNAL_SKULL_PATH = "./main/assets/skull.png";
	private static String DEFAULT_PROFILE_PATH = CONFIG_FILE_PATH + "Default/";
	private static String SKULL_PATH = CONFIG_FILE_PATH + "Default/skull.png";

	private static AudioReading audio;
	private static View view;
	
	/** 
	 *  Either this should be a list of Profiles (and thus a manager object) or we let the program
	 *  read config info from files to inform the user of various Profiles and only have one loaded
	 *  in at a time.
	 *  
	 *  Potential plans to have things like Tone/Tambre change significant portions of the talk sprite
	 *  may require we have multiple Profiles loaded in at once for convenience.
	 *  
	 */
	private volatile Profile profile;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Controller() {
		File f = new File(CONFIG_FILE_PATH);
		f.mkdirs();
		EventSender.assignEventProcessor(this);
		view = new View(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		checkNeedDefaultProfile();
		ReadWriteConfig.populateConfigDefaultValues();
		audio = new AudioReading(this);
		
		//profile = makeStarterProfile();
		profile = ReadWriteConfig.readInProfile(ReadWriteConfig.getDefaultProfile());
		profile.populateAudioConfigImages(view);
		receiveAudio(0);
	}
	
	private void checkNeedDefaultProfile() {
		if(ReadWriteConfig.readValidProfiles().size() == 0) {
			Image img = view.getImage(INTERNAL_SKULL_PATH);
			BufferedImage copy = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gr2 = copy.createGraphics();
			gr2.drawImage(img, 0, 0, new Color(255, 255, 255, 0), null);
			gr2.dispose();
			File f = new File(SKULL_PATH);
			File g = new File(DEFAULT_PROFILE_PATH);
			g.mkdirs();
			try {
				f.createNewFile();
				ImageIO.write(copy, "png", f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			makeStarterProfile();
			try {
				ReadWriteConfig.writeProfileToFile("Default", profile.exportProfile());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Profile makeStarterProfile() {
		profile = new Profile("Default");
		try {
			profile.addAudioConfig("Quiet", 0, SKULL_PATH, 0);
			profile.addAudioConfigChange("Quiet", "darken", ".6");
			profile.addAudioConfigChange("Quiet", "mirror");
			profile.addAudioConfig("Speaking", 20, SKULL_PATH, 7);
			profile.addAudioConfigChange("Speaking", "shake", "1", "1");
			profile.addAudioConfigChange("Speaking", "mirror");
			profile.addAudioConfig("Loud", 300, SKULL_PATH, 12);
			profile.addAudioConfigChange("Loud", "red");
			profile.populateAudioConfigImages(view);
			return profile;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void updateActiveProfile(String profileName) {
		profile = ReadWriteConfig.readInProfile(profileName);
		if(ReadWriteConfig.getStatusDefaultProfileAutoset()) {
			try {
				ReadWriteConfig.establishDefaultProfile(profileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		profile.populateAudioConfigImages(view);
		refreshConfigMenu();
	}
	
	public void setAudioLevelAdjustor(double in) {
		if(audio != null && in >= 0.0)
			audio.setAudioLevelAdjustment(in);
	}
	
	private ArrayList<String> getNonSpecialProfiles(){
		ArrayList<String> out = ReadWriteConfig.readValidProfiles();
		String active = profile.getTitle();
		String defaultProf = ReadWriteConfig.getDefaultProfile();
		out.remove(active);
		out.remove(defaultProf);
		return out;
	}
	
	private void refreshConfigMenu() {
		view.refreshConfigMenu(profile.getTitle(), ReadWriteConfig.getDefaultProfile(), getNonSpecialProfiles());
	}
	
	@Override
	public void processEvent(int code) {
		switch(code) {
			case CodeReference.CODE_DISPLAY_PROFILES:
				view.promptConfigMenu(profile.getTitle(), ReadWriteConfig.getDefaultProfile(), getNonSpecialProfiles());
				break;
			case CodeReference.CODE_ACTIVE_PROFILE:
				refreshConfigMenu();
				break;
			case CodeReference.CODE_DEFAULT_PROFILE:
				refreshConfigMenu();
				break;
			default:
				if(code >= CodeReference.CODE_BASE_PROFILES && code < CodeReference.CODE_BASE_CONFIGS) {
					int index = code - CodeReference.CODE_BASE_PROFILES;
					updateActiveProfile(getNonSpecialProfiles().get(index));
				}
				else {
					refreshConfigMenu();
				}
				break;
		}
	}

	@Override
	public void receiveAudio(int newAudio) {
		if(profile != null) {
		Image img = profile.getAppropriateAudioImage((int)(newAudio * audio.getAudioAdjustment()));
			if(img != null)
				view.updateSpriteDisplay(img);
		}
	}
	
}
