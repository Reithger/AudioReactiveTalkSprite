package ui.display;

import java.awt.Image;

/**
 * 
 * TODO: Needs the 'exit on close' tag
 * 
 */

public class SpriteDisplay extends VisualDisplay{

	public SpriteDisplay(int wid, int hei) {
		super(wid, hei);
		this.setTitle("PNGTuber Ada Made");
		this.setExitOnClose(true);
	}
	
	public void updateDisplayedImage(Image spriteImage) {
		removeElementPrefixed("");
		handleImage("image", "basic", 5, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), false, spriteImage);
	}
	
	public Image retrieveImage(String filePath) {
		return getHandlePanel().retrieveImage(filePath);
	}
	
}
