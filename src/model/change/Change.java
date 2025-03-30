package model.change;

import java.awt.Image;

public interface Change {

	public abstract Image applyChange(Image startImage);
	
	public abstract String export();
	
}
