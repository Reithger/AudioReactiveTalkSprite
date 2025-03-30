package ui;

import main.EventProcessor;
import visual.composite.popout.PopoutWindow;

public class AudioConfigsDisplay extends PopoutWindow{

	private EventProcessor eventHandler;
	
	public AudioConfigsDisplay(int width, int height, String profileRef, EventProcessor eventProcessor) {
		super(width, height);
		eventHandler = eventProcessor;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void clickAction(int clickEvent, int x, int y) {
		eventHandler.processEvent(clickEvent);
	}

}
