package ui.display;

import ui.EventSender;
import visual.composite.popout.PopoutWindow;

public abstract class VisualDisplay extends PopoutWindow{

	public VisualDisplay(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void clickAction(int clickEvent, int x, int y) {
		EventSender.sendEvent(clickEvent);
	}
	
}
