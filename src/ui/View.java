package ui;

import java.awt.Image;
import java.util.ArrayList;

import main.EventProcessor;
import main.ImageRetriever;

public class View implements ImageRetriever {

	private SpriteDisplay mainWindow;
	private ConfigDisplay cfgMenu;
	private EventProcessor eventHandler;
	
	public View(int wid, int hei, EventProcessor inEventHandler) {
		eventHandler = inEventHandler;
		mainWindow = new SpriteDisplay(wid, hei, eventHandler);
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
		if(cfgMenu == null) {
			cfgMenu = new ConfigDisplay(300, 500, activeProf, defaultProf, profiles, eventHandler);
		}
	}
	
	public void refreshConfigMenu(String activeProf, String defaultProf, ArrayList<String> profiles) {
		if(cfgMenu != null) {
			cfgMenu.refresh(activeProf, defaultProf, profiles);
		}
	}
	
	@Override
	public Image getImage(String filePath) {
		return mainWindow.retrieveImage(filePath);
	}
}
