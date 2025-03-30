package model.change.filter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import model.change.ChangeFactory;

public class DarkenFilter implements Filter{

	private double darkenStrength;
	
	public DarkenFilter(double darkenAmount) {
		darkenStrength = darkenAmount;
	}
	
	@Override
	public Image applyChange(Image startImage) {
		BufferedImage copy = new BufferedImage(startImage.getWidth(null), startImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(startImage, 0, 0, new Color(255, 255, 255, 0), null);
		gr2.dispose();
		
		int width = copy.getWidth();
		int height = copy.getHeight();
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Color c = new Color(copy.getRGB(i, j), true);
				int transp = c.getAlpha();
				Color use = new Color((int) (c.getRed() * darkenStrength), (int) (c.getGreen() * darkenStrength), (int) (c.getBlue() * darkenStrength), transp);
				copy.setRGB(i, j, use.getRGB());
			}
		}
		return copy;
	}
	
	@Override
	public String export() {
		return ChangeFactory.KEYWORD_DARKEN_FILTER + " " + ("" + darkenStrength);
	}

}
