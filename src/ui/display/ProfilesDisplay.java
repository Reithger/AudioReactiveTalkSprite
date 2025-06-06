package ui.display;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import control.CodeReference;

/**
 * 
 * ConfigDisplay is a class that allows the user to view their A.R.T.S profiles and the audio configs
 * within the profile, and make edits to them.
 * 
 * Initial pop-up view should be of the available Profiles for quick-swapping between them or opening up an
 * editing menu; the currently read-in Profile should be at the top and obviously the current one.
 * 
 * TODO: Can set a Profile as the default; if no default set, just uses the last one you used
 * 
 * TODO: Need a way to add/delete profiles, too
 * 
 * 
 */

public class ProfilesDisplay extends VisualDisplay{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final String MOVE_GROUP = "slide";
	
	private static final String CONFIG_IMAGE_PATH = "/main/assets/skull.png";
	
	private static final int NUM_PROFILES_ONCE = 6;
	private static final int SCROLLBAR_WIDTH_RATIO = 18;
	
	private static final Font DEFAULT_FONT = null;
	
	private static final int MOD_NONE = 0;
	private static final int MOD_ACTIVE = 1;
	private static final int MOD_DEFAULT = 2;
	private static final int MOD_ACTIVE_DEFAULT = 3;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private int wid;
	private int hei;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ProfilesDisplay(int width, int height, String activeProfile, String defaultProfile, ArrayList<String> profiles) {
		super(width, height);
		wid = width;
		hei = height;
		drawPage(activeProfile, defaultProfile, profiles);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void refresh(String activeProfile, String defaultProfile, ArrayList<String> profiles) {
		this.removeElementPrefixed("");
		drawPage(activeProfile, defaultProfile, profiles);
	}
	
	private void drawPage(String activeProfile, String defaultProfile, ArrayList<String> profiles) {
		this.handleScrollbar("scrollbar", "bar", MOVE_GROUP, 35, wid - getScrollWid(), 0, getScrollWid(), hei, 0, hei, true);
		int startX = wid / 2 - (getScrollWid() / 2);
		int startY = hei / (NUM_PROFILES_ONCE * 3 / 2);
		int useProfileCode = CodeReference.CODE_BASE_PROFILES;
		int useConfigCode = CodeReference.CODE_BASE_CONFIGS;
		int changeY = hei / NUM_PROFILES_ONCE;
		
		if(activeProfile != null) {
			drawProfileButton(activeProfile, startX, startY, CodeReference.CODE_ACTIVE_PROFILE, CodeReference.CODE_ACTIVE_PROFILE_CONFIG, activeProfile.equals(defaultProfile) ? MOD_ACTIVE_DEFAULT : MOD_ACTIVE);
			startY += changeY;
		}
		if(defaultProfile != null && !activeProfile.equals(defaultProfile)) {
			drawProfileButton(defaultProfile, startX, startY, CodeReference.CODE_DEFAULT_PROFILE, CodeReference.CODE_DEFAULT_PROFILE_CONFIG, MOD_DEFAULT);
			startY += changeY;
		}
		for(String s : profiles) {
			drawProfileButton(s, startX, startY, useProfileCode++, useConfigCode++, MOD_NONE);
			startY += changeY;
		}
		// This line is just to give some buffer space at the bottom for the scrollbar to look nice
		this.handleLine("height_adjust", MOVE_GROUP, 0, startX, startY - changeY / 2, startX, startY - changeY / 2, 0, Color.white);
	}
	
	/**
	 * 
	 * Modifier is a simple code value to indicate if the profile is typed as Default and/or Active to adjust
	 * how we draw it for visual clarity.
	 * 
	 * 0 - nothing special
	 * 1 - active
	 * 2 - default
	 * 3 - both
	 * 
	 * @param profile
	 * @param x
	 * @param y
	 * @param code
	 * @param modifier
	 */
	
	private void drawProfileButton(String profile, int x, int y, int code, int configCode, int modifier) {
		boolean defaultProf = modifier / 2 == 1;
		boolean active = modifier % 2 == 1;
		int boxWid = (wid - getScrollWid()) * 7 / 8;
		int boxHei = hei / (NUM_PROFILES_ONCE + 1);
		this.handleRectangle("rect_" + profile, MOVE_GROUP, 10, x, y, boxWid, boxHei, active ? Color.DARK_GRAY : Color.white, defaultProf ? Color.orange : Color.black);
		if(defaultProf) {
			this.handleThickRectangle("outline_" + profile, MOVE_GROUP, 15, x - boxWid / 2, y - boxHei / 2, x + boxWid / 2, y + boxHei / 2, Color.orange, 3);
		}
		this.handleButton("butt_" + profile, MOVE_GROUP, 10, x,  y,  (wid - getScrollWid()) * 7 / 8, hei / (NUM_PROFILES_ONCE + 1), code);
	
		int titleX = x - boxWid / 3;
		this.handleText("text_" + profile, MOVE_GROUP, 15, titleX, y, boxWid * 2 / 3, boxHei, DEFAULT_FONT, profile);
		
		int configX = x + boxWid / 3;
		this.handleImageButton("config_" + profile, MOVE_GROUP, 15, configX, y, boxHei * 4 / 5, boxHei * 4 / 5, CONFIG_IMAGE_PATH, configCode);
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private int getScrollWid() {
		return wid / SCROLLBAR_WIDTH_RATIO;
	}

}
