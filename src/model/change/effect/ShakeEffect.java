package model.change.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import model.change.ChangeFactory;

/**
 * 
 * To consider: separate horizontal and vertical stretch amounts from each other
 * 				make all images have a slight buffer zone around them so we can have it shake
 * 					within a consistent view space (not go off-screen) without it being jarring
 * 					to see the image suddenly offset a bit because only an image with shake effect
 * 					gets a border area
 * 				maybe another effect is having a slight border area, though then all image drawing
 * 					has to happen with a slight offset into the middle to center it; the ability
 * 					for other effects to draw off the screen will lose information by the time
 * 					we reach the border effect...
 * 				maybe the front end can inform us of the final image size so some effects can
 * 					'finalize' things into the desired image configuration and have the efficacy
 * 					we want. Changes then need an ordering to know which are done at the end.
 * 
 */

public class ShakeEffect implements Effect{

	private int shakeAmount;
	private int intensitySpeed;
	
	private int counter;
	private int lastOffX;
	private int lastOffY;
	
	public ShakeEffect(int shakeStrength, int shakeSpeed) {
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
		Random rand = new Random();
		if(counter % intensitySpeed == 0) {
			lastOffX = rand.nextInt(shakeRange) - shakeRange / 2;
			lastOffY = rand.nextInt(shakeRange) - shakeRange / 2;
		}
		counter++;
		gr2.drawImage(startImage, lastOffX, lastOffY, new Color(255, 255, 255, 0), null);
		gr2.dispose();
		
		return copy;
	}

	@Override
	public String export() {
		return ChangeFactory.KEYWORD_SHAKE_EFFECT + " " + ("" + shakeAmount) + " " + ("" + intensitySpeed);
	}

	
	
}
