package model.profile;

import java.awt.Image;
import java.util.ArrayList;

import control.ImageRetriever;
import model.change.Change;
import model.change.ChangeFactory;

public class Profile {
	
//---  Instance Variables   -------------------------------------------------------------------

	private String title;
	
	private ArrayList<AudioConfig> configs;
	
	/** currDuration is a measure of how long the current AudioConfig has been active for*/
	private int currDuration;
	/** currConfig is a reference value to know which AudioConfig is currently active (persistence will let them sustain for longer)*/
	private String currConfig;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Profile(String label) {
		title = label;
		configs = new ArrayList<AudioConfig>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * Function that uses the specified arguments to construct a new AudioConfig object and add it
	 * to this Profile's list of AudioConfig objects.
	 * 
	 * Throws an exception if the new AudioConfig object has the same audio threshold value as an
	 * existing AudioConfig object already associated to this Profile.
	 * 
	 * @param threshold
	 * @param title
	 * @param baseImage
	 * @param persistence
	 * @throws Exception
	 */
	
	public void addAudioConfig(String title, int threshold, String filePath, int persistence) throws Exception{
		if(checkDuplicateThreshold(threshold)) {
			throw new Exception("Attempt to add duplicate audio threshold value to Profile: " + threshold);
		}
		AudioConfig ac = new AudioConfig(threshold, title, filePath, persistence);
		configs.add(ac);
		sortConfigs();
	}
	
	/**
	 * 
	 * Function that adds a defined Change (Effect or Filter) to a specified AudioConfig associated
	 * to this Profile object.
	 * 
	 * They keyword argument should be referenced from the public constant values in the ChangeFactory
	 * class, and the specifiers are the relevant arguments for configuring that Change object.
	 * 
	 * @param title
	 * @param keyword
	 * @param specifiers
	 */
	
	public void addAudioConfigChange(String title, String keyword, String ... specifiers) {
		Change c = ChangeFactory.formulateChange(keyword, specifiers);
		if(c != null && getConfig(title) != null) {
			if(ChangeFactory.interpretFilter(keyword)) {
				getConfig(title).addFilter(c);
			}
			else {
				getConfig(title).addEffect(c);
			}
		}
	}
	
	/**
	 * 
	 * Convert this Profile and the AudioConfigs it contains to a String format for
	 * writing to a config file; we assume the corresponding import from String format
	 * is done at a higher level and the regular constructor and add functions are used
	 * to recreate the Profile and AudioConfig objects.
	 * 
	 * @return
	 */
	
	public String exportProfile() {
		StringBuilder out = new StringBuilder();
		out.append(title + "\n");
		for(AudioConfig ac : configs) {
			out.append(ac.exportAudioConfig() + "\n");
		}
		return out.toString();
	}
	
	/**
	 * 
	 * Function that uses the Visitor pattern to borrow the image retrieval capability
	 * from the SVI library via the View class (that has a HandlePanel), but puts the
	 * View class through an interface for clean coding.
	 * 
	 * AudioConfigs have the filePath, this prompts the system to read that filePath and
	 * give each AudioConfig the corresponding Image object.
	 * 
	 * @param imgGet
	 */
	
	public void populateAudioConfigImages(ImageRetriever imgGet) {
		for(AudioConfig ac : configs) {
			ac.setBaseImage(imgGet.getImage(ac.getImageFilePath()));
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 * For the provided loudness value, return the appropriate image associated to the
	 * AudioConfig object that matches the provided loudness value.
	 * 
	 * Basically, with a list of AudioConfigs sorted ascending by their Audio Thresholds, find
	 * the AudioConfig with the largest Audio Threshold that is smaller than the provided
	 * loudness value.
	 * 
	 * @param loudness
	 * @return
	 */
	
	public Image getAppropriateAudioImage(int loudness) {
		if(currConfig != null) {
			if(currDuration < getConfig(currConfig).getPersistence()) {
				currDuration++;
				return getConfig(currConfig).getDisplayImage();
			}
		}
		for(int i = 0; i < configs.size(); i++) {
			if(loudness < configs.get(i).getAudioThreshold()) {
				AudioConfig out = configs.get(i-1 < 0 ? 0 : i - 1);
				currDuration = 0;
				currConfig = out.getTitle();
				return out.getDisplayImage();
			}
		}
		AudioConfig out = configs.get(configs.size() - 1);
		currDuration = 0;
		currConfig = out.getTitle();
		return out.getDisplayImage();
	}
	
	/**
	 * 
	 * Debug function that performs the same behavior as getAppropriateAudioImage but
	 * just returns the title of the AudioConfig that corresponds to the provided
	 * loudness value instead of the Image.
	 * 
	 * @param loudness
	 * @return
	 */
	
	public String getTriggeredAudioConfig(int loudness) {
		for(int i = 0; i < configs.size(); i++) {
			if(loudness < configs.get(i).getAudioThreshold()) {
				return configs.get(i-1 < 0 ? 0 : i - 1).getTitle();
			}
		}
		return configs.get(configs.size() - 1).getTitle();
	}
	
	public ArrayList<String> getAllBaseImagePaths(){
		ArrayList<String> out = new ArrayList<String>();
		for(AudioConfig ac : configs) {
			out.add(ac.getImageFilePath());
		}
		return out;
	}

	public String getTitle() {
		return title;
	}
	
//---  Support Methods   ----------------------------------------------------------------------

	private AudioConfig getConfig(String title) {
		for(AudioConfig ac : configs) {
			if(ac.getTitle().equals(title)) {
				return ac;
			}
		}
		return null;
	}

	private boolean checkDuplicateThreshold(int newThresh) {
		for(int i = 0;i < configs.size(); i++) {
			if(configs.get(i).getAudioThreshold() == newThresh) {
				return true;
			}
		}
		return false;
	}
	
	private void sortConfigs() {
		ArrayList<AudioConfig> out = new ArrayList<AudioConfig>();
		for(int i = 0; i < configs.size(); ) {
			int quietest = 0;
			for(int j = 0; j < configs.size() - i; j++) {
				if(configs.get(j).getAudioThreshold() < configs.get(quietest).getAudioThreshold()) {
					quietest = j;
				}
			}
			out.add(configs.get(quietest));
			configs.remove(quietest);
		}
		configs = out;
	}
	
}
