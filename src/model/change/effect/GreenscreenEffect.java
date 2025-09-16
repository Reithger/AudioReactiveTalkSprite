package model.change.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import model.change.ChangeFactory;

/**
 * 
 * Effect that draws an underyling Green color for consistent chroma-keying
 * 
 */

public class GreenscreenEffect implements Effect {

	private static final Color[] COLORS = new Color[] {new Color(0, 255, 0), new Color(0, 0, 255)};
	
	private Color useColor;
	
	public GreenscreenEffect(int choice) {
		useColor = COLORS[choice % COLORS.length];
	}
	
	@Override
	public Image applyChange(Image startImage) {
		int newWid = startImage.getWidth(null);// + shakeRange;
		int newHei = startImage.getHeight(null);// + shakeRange;
		BufferedImage copy = new BufferedImage(newWid, newHei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(startImage, 0, 0, new Color(255, 255, 255, 0), null);
		gr2.setColor(useColor);
		gr2.fillRect(0, 0, newWid, newHei);
		gr2.drawImage(startImage, 0, 0, new Color(255, 255, 255, 0), null);

		gr2.dispose();
		
		return copy;
	}

	@Override
	public String export() {
		return ChangeFactory.KEYWORD_GREENSCREEN_EFFECT;
	}

}
