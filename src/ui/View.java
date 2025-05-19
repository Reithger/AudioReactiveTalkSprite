package ui;

import java.awt.Image;
import java.util.ArrayList;

import main.ImageRetriever;
import ui.display.AudioConfigsDisplay;
import ui.display.ProfilesDisplay;
import ui.display.SpriteDisplay;

public class View implements ImageRetriever {

	private SpriteDisplay mainWindow;
	private ProfilesDisplay cfgMenu;
	
	public View(int wid, int hei) {
		mainWindow = new SpriteDisplay(wid, hei);
	}
	
	public void updateSpriteDisplay(Image img) {
		mainWindow.updateDisplayedImage(img);
	}
	
	/**
	 * 
	 * This should generate a new ReadWriteConfig window that allows editing
	 * of the current Profile in the model backend; need an encoding pattern
	 * system for translating the current Profile to something that can be displayed,
	 * and also have robust buttons for sending edit commands to the backend model
	 * to change the current Profile
	 * 
	 */
	
	public void promptConfigMenu(String activeProf, String defaultProf, ArrayList<String> profiles) {
		if(cfgMenu != null) {
			cfgMenu.dispose();
		}
		cfgMenu = new ProfilesDisplay(300, 500, activeProf, defaultProf, profiles);
	}
	
	public void refreshConfigMenu(String activeProf, String defaultProf, ArrayList<String> profiles) {
		if(cfgMenu != null) {
			cfgMenu.refresh(activeProf, defaultProf, profiles);
		}
	}
	
	public void promptAudioConfigsMenu() {
		AudioConfigsDisplay acd = new AudioConfigsDisplay(250, 250, "reference");
	}
	
	@Override
	public Image getImage(String filePath) {
		return mainWindow.retrieveImage(filePath);
	}
}
