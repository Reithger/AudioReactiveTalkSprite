package model.effects;

/**
 * 
 * An Effect is an influence on the base image itself, changing it significantly (not the source image file
 * but the local copy loaded into the program); these are likely to be dynamic, persistent, and
 * complex.
 * 
 * An Effect will change the reference image for all AudioConfigs.
 * 
 */

public interface Effect extends Change{

}
