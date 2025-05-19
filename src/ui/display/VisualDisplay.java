package ui.display;

import ui.EventSender;
import visual.composite.popout.PopoutWindow;

public abstract class VisualDisplay extends PopoutWindow{

	private boolean wasDragging;
	
	public VisualDisplay(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void clickAction(int clickEvent, int x, int y) {
		//setFrameShapeDisc();
		if(!wasDragging)
			EventSender.sendEvent(clickEvent);
	}
	
	@Override
	public void clickPressAction(int clickEvent, int x, int y) {
		super.clickPressAction(clickEvent, x, y);
	}
	
	@Override
	public void clickReleaseAction(int clickEvent, int x, int y) {
		super.clickReleaseAction(clickEvent, x, y);
		wasDragging = false;
	}
	
	@Override
	public void dragAction(int event, int x, int y) {
		super.dragAction(event, x, y);
		wasDragging = true;
	}
	
}
