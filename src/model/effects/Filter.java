package model.effects;

/**
 * 
 * A Filter is an influence on a sprite being displayed, changing it in a simple way (not the source
 * image but the local copy loaded into the program); these are simpler and any change disappears once
 * the AudioConfig with this filter is swapped away from.
 * 
 * A Filter will not change the reference image for all AudioConfigs.
 * 
 */

public interface Filter extends Change {

}
