package model.change;

import model.change.effect.ShakeEffect;
import model.change.filter.DarkenFilter;
import model.change.filter.MirrorFilter;
import model.change.filter.RedFilter;

public class ChangeFactory {
	
	public static final String KEYWORD_RED_FILTER = "red";
	
	public static final String KEYWORD_DARKEN_FILTER = "darken";
	
	public static final String KEYWORD_SHAKE_EFFECT = "shake";
	
	public static final String KEYWORD_MIRROR_FILTER = "mirror";

	/**
	 * 
	 * This is a clunky way to do this, figure out something better
	 * 
	 * Maybe Effect and Filter interfaces become parent classes that implement Change
	 * and bestow an identity that each Change object can inherently know their
	 * status via?
	 * 
	 * Changes should also have a getter to inform how much information they expect to get
	 * for easy error checking.
	 * 
	 * @param keyword
	 * @return
	 */
	
	public static boolean interpretFilter(String keyword) {
		switch(keyword) {
			case KEYWORD_RED_FILTER:
				return true;
			case KEYWORD_DARKEN_FILTER:
				return true;
			case KEYWORD_SHAKE_EFFECT:
				return false;
			case KEYWORD_MIRROR_FILTER:
				return true;
			default:
				return false;
		}
	}
	
	public static Change formulateChange(String keyword, String ... details) {
		switch(keyword) {
			case KEYWORD_RED_FILTER:
				return new RedFilter();
			case KEYWORD_DARKEN_FILTER:
				return new DarkenFilter(Double.parseDouble(details[0]));
			case KEYWORD_SHAKE_EFFECT:
				return new ShakeEffect(Integer.parseInt(details[0]), Integer.parseInt(details[1]));
			case KEYWORD_MIRROR_FILTER:
				return new MirrorFilter();
			default:
				return null;
		}
	}
	
}
