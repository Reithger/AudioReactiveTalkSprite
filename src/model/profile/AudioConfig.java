package model.profile;

import java.awt.Image;
import java.util.ArrayList;

import model.change.Change;

/**
 * 
 * Representation of the set of 'Audio Level Threshold -> Displayed Sprite w/ Chosen Filters/Effects'
 * 
 */

public class AudioConfig {

//---  Instance Variables   -------------------------------------------------------------------
	
	private String title;
	
	private int audioThreshold;
	/** 'persistence' is a measure of how long this AudioConfig should persist after it becomes active*/
	private int persistence;
	/** Just a reference value for when this exports to a file format representation*/
	private String imagePath;
	
	private Image baseImage;
	/** This is the image we derive from the baseImage via the effects and filters on this object*/
	private Image affectedImage;
	
	private ArrayList<Change> effects;
	
	private ArrayList<Change> filters;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public AudioConfig(int threshold, String inTitle, String filePath, int inPersistence) {
		audioThreshold = threshold;
		imagePath = filePath;
		persistence = inPersistence;
		title = inTitle;
		effects = new ArrayList<Change>();
		filters = new ArrayList<Change>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * Need a way to identify and remove these filters, maybe rebuild AudioConfig from scratch
	 * on any change?
	 * 
	 * @param f
	 */
	
	public void addFilter(Change f) {
		filters.add(f);
	}
	
	public void addEffect(Change e) {
		effects.add(e);
	}
	
	/**
	 * 
	 * This function should instruct this AudioConfig to apply the Effects to
	 * the baseImage; some Effects are persistent and will 'stack' the changes
	 * they cause for long-term semi-permanent changes. 
	 * 
	 * Thus, this function causes any such changes to apply again and update the
	 * image accordingly (this may just change some parameters in the specific Effect
	 * object as each call to configureImage applies onto the baseImage)
	 * 
	 */
	
	public Image triggerEffectsChange() {
		return configureImage();
	}

	/**
	 * 
	 * Export format for AudioConfig (AC) is:
	 * [AC title], [audio threshold], [persistence], [imagePath], [# of Changes (Effects/Filters)]
	 * 
	 * Followed by a separate line for each Effect/Filter, generally formatted as:
	 * [Keyword for the Change], ... [Whatever arguments specify its behavior]
	 * 
	 * 
	 * @return
	 */
	
	public String exportAudioConfig() {
		StringBuilder out = new StringBuilder();
		
		out.append(title + " " + audioThreshold + " " + persistence + " " +  getNumberChanges() + " " + imagePath + "\n");
		for(Change c : effects) {
			out.append(c.export() + "\n");
		}
		for(Change c : filters) {
			out.append(c.export() + "\n");
		}
		
		return out.toString();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAudioThreshold(int newThresh) {
		audioThreshold = newThresh;
	}
	
	public void setBaseImage(Image newImage) {
		baseImage = newImage;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTitle() {
		return title;
	}
	
	public String getImageFilePath() {
		return imagePath;
	}
	
	public int getAudioThreshold() {
		return audioThreshold;
	}
	
	/**
	 * 
	 * Persistence is a value that defines how long this AudioConfig object should display its
	 * image for once activated; this is measured by, each time an audio level check is performed,
	 * decrementing a counter against this persistence value; once the counter reaches the persistence value,
	 * the current AudioConfig object can change.
	 * 
	 * This is just so that certain effects at specific audio levels don't last for a split second and can
	 * end while you're drawing a breath between words; it looks weird to have it go into quiet mode
	 * mid-sentence.
	 * 
	 * @return
	 */
	
	public int getPersistence() {
		return persistence;
	}

	/**
	 * 
	 * Getter function that returns the configured image (the base image as affected by the
	 * associated Effects and Filters for this AudioConfig object).
	 * 
	 * If the image already exists (has been configured before), it returns that image.
	 * 
	 * If you need to cause a re-build of the image or re-apply the persistent changes from an
	 * Effect, call the 'triggerEffectsChange()' function and then call 'getDisplayImage()'.
	 * 
	 * @return
	 */
	
	public Image getDisplayImage() {
		if(affectedImage == null) {
			return configureImage();
		}
		return configureImage(); //affectedImage;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private int getNumberChanges() {
		return effects.size() + filters.size();
	}
	
	/**
	 * 
	 * Formats the image for this AudioConfig to send to the front UI; takes the
	 * basic image defined in the filePath (should be given the image due to complications
	 * in pulling the image that the SVI handles for us) and applies the lists of
	 * Effects and Filters to the image as a composite of alterations.
	 * 
	 * @return
	 */
	
	private Image configureImage() {
		if(baseImage == null) {
			return null;
		}
		affectedImage = baseImage.getScaledInstance(-1, -1, Image.SCALE_DEFAULT);
		
		for(Change e : effects) {
			affectedImage = e.applyChange(affectedImage);
		}
		
		for(Change f : filters) {
			affectedImage = f.applyChange(affectedImage);
		}
		return affectedImage;
	}
	
}
