package model.change.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import model.change.ChangeFactory;

/**
 * Effect type that pads out a set number of pixels to each side of the given image
 * to avoid any cut-offs from other Effects that may move the image
 * 
 * TODO: Need structural way to ensure this one happens before any Effects that may cut
 * off a part of the image; if it happens after, the image is already changed. Currently
 * the order of effects in the config file decides this, bad way to handle it.
 * 
 */

public class PadEffect implements Effect{
	
	private int widPad;
	private int heiPad;
	
	public PadEffect(int extraWid, int extraHei) {
		widPad = extraWid;
		heiPad = extraHei;
	}

	@Override
	public Image applyChange(Image startImage) {
		int newWid = startImage.getWidth(null) + 2 * widPad;// + shakeRange;
		int newHei = startImage.getHeight(null) + 2 * heiPad;// + shakeRange;
		BufferedImage copy = new BufferedImage(newWid, newHei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(startImage, 0, 0, new Color(255, 255, 255, 0), null);
		gr2.setColor(new Color(copy.getRGB(1, 1)));
		gr2.fillRect(0, 0, newWid, newHei);
		gr2.drawImage(startImage, widPad, heiPad, new Color(255, 255, 255, 0), null);

		gr2.dispose();
		
		return copy;
	}

	@Override
	public String export() {
		return ChangeFactory.KEYWORD_PAD_EFFECT + " " + widPad + " " + heiPad;
	}

}
