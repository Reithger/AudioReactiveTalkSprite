package ui;

import java.awt.Image;

import main.EventProcessor;
import visual.composite.HandlePanel;
import visual.frame.WindowFrame;

public class SpriteDisplay {

	private WindowFrame frame;
	private HandlePanel hp;
	private EventProcessor eventHandler;
	
	public SpriteDisplay(int wid, int hei, EventProcessor eventProcessor) {
		frame = new WindowFrame(wid, hei);
		frame.setName("PNGTuber Ada Made");
		eventHandler = eventProcessor;
		hp = new HandlePanel(0, 0, wid, hei) {
			
			@Override
			public void clickEvent(int event, int x, int y, int clickType) { 
				eventHandler.processEvent(0);
			}
			
		};
		frame.addPanel("basic", hp);
		frame.showWindow();
	}
	
	public void updateDisplayedImage(Image spriteImage) {
		hp.removeAllElements();
		hp.addImage("image", 5, "default", 0, 0, frame.getWidth(), frame.getHeight(), false, spriteImage, true);
	}
	
	public Image retrieveImage(String filePath) {
		return hp.retrieveImage(filePath);
	}
	
}
