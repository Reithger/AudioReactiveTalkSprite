package model.effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class MirrorFilter implements Filter {
	
	@Override
	public Image applyChange(Image startImage) {
		int wid = startImage.getWidth(null);
		int hei = startImage.getHeight(null);
		BufferedImage copy = new BufferedImage(wid, hei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(startImage, wid, 0, -wid, hei, new Color(255, 255, 255, 0), null);
		gr2.dispose();

		return copy;
	}
	
	@Override
	public String export() {
		return ChangeFactory.KEYWORD_MIRROR_FILTER;
	}

}
