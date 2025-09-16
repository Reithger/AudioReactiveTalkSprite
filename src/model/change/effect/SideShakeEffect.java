package model.change.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import model.change.ChangeFactory;

public class SideShakeEffect implements Effect {

	private int shakeAmount;
	private int intensitySpeed;
	
	private int counter;
	private int lastOffX;
	
	public SideShakeEffect(int shakeStrength, int shakeSpeed) {
		shakeAmount = shakeStrength;
		intensitySpeed = shakeSpeed;
		counter = 0;
	}
	
	@Override
	public Image applyChange(Image startImage) {
		int shakeRange = shakeAmount * 2;
		int newWid = startImage.getWidth(null);// + shakeRange;
		int newHei = startImage.getHeight(null);// + shakeRange;
		BufferedImage copy = new BufferedImage(newWid, newHei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(startImage, 0, 0, new Color(255, 255, 255, 0), null);
		Random rand = new Random();
		if(counter % intensitySpeed == 0) {
			lastOffX = rand.nextInt(shakeRange) - shakeRange / 2;
		}
		counter++;
		gr2.setColor(new Color(copy.getRGB(newWid - 2, 1)));
		gr2.fillRect(0, 0, newWid, newHei);
		gr2.drawImage(startImage, lastOffX, 0, new Color(255, 255, 255, 0), null);

		gr2.dispose();
		
		return copy;
	}

	@Override
	public String export() {
		return ChangeFactory.KEYWORD_SIDE_SHAKE_EFFECT + " " + ("" + shakeAmount) + " " + ("" + intensitySpeed);
	}

	
}
