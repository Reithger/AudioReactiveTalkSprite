package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import model.profile.Profile;

/**
 * 
 * Class that handles the reading in of Profile schematics and writing of Profile schematics
 * to memory to save the user's configurations for their talk sprite(s).
 * 
 * Each profile is its own folder, containing .png or .jpg images and a config file containing the
 * data on AudioConfigs
 * 
 * Need a way, then, to choose which Profile should show up first (can just read folder names in
 * the pngassets folder with a valid config file to know the available profiles) and have a way to
 * easily swap Profiles for the user.
 * 
 * Maybe convert the file formats from generic .txt rough-hewn to a proper .json or something? Check how
 * Java can work with that, would be nice to have dictionary/map structure for this.
 * 
 */

public class ReadWriteConfig {
	
//---  Constants   ----------------------------------------------------------------------------
	
	public final static String CONFIG_FILE_NAME = "config.txt";
	public final static String CONFIG_PREFIX_DEFAULT_PROFILE = "DEF_PROFILE";
	public final static String CONFIG_PREFIX_DEFAULT_SET_AUTO = "DEFAULT_AUTO";
	
	public final static String CONFIG_KEYWORD_AUTO_SET = "auto";
	public final static String CONFIG_KEYWORD_MANUAL_SET = "manual";

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * If file paths for images lead to places not in the folder, make a local copy of it
	 * and reference that going forward.
	 * 
	 * We can just do that rewrite when the user points out an image to use from the UI.
	 * 
	 * Should these throw exceptions or try to handle them? What's the fail-proof response?
	 * 
	 * @param title
	 * @param encoded
	 */
	
	public static void writeProfileToFile(String title, String encoded) throws Exception {
		File f = new File(Controller.CONFIG_FILE_PATH + "/" + title);
		f.mkdirs();
		f = new File(Controller.CONFIG_FILE_PATH + "/" + title + "/" + CONFIG_FILE_NAME);
		writeFileContents(f, encoded);
	}
	
	public static Profile readInProfile(String profileName) {
		File f = new File(Controller.CONFIG_FILE_PATH + "/" + profileName + "/" + CONFIG_FILE_NAME);
		ArrayList<String> lines = null;
		try {
			lines = retrieveFileContents(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Profile out = new Profile(lines.get(0));
		
		// Interpret contents of file to construct the AudioConfig objects
		for(int i = 1; i < lines.size(); i++) {
			String header = lines.get(i);
			String[] components = header.split(" ");
			if(components.length < 5) {
				continue;
			}
			int numEffects = Integer.parseInt(components[3]);
			String path = components[4];
			for(int j = 5; j < components.length; j++) {
				path = path + " " + components[j];
			}
			try {
				out.addAudioConfig(components[0], Integer.parseInt(components[1]), path, Integer.parseInt(components[2]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(int j = 0; j < numEffects; j++) {
				String effect = lines.get(i + j + 1);
				String[] effect_pieces = effect.split(" ");
				String keyword = effect_pieces[0];
				String[] args = new String[effect_pieces.length - 1];
				for(int k = 1; k < effect_pieces.length; k++) {
					args[k-1] = effect_pieces[k];
				}
				out.addAudioConfigChange(components[0], keyword, args);
			}
			i += numEffects;
		}
		
		return out;
	}
		
	/**
	 * 
	 * Function that checks inside of the pngassets folder for validly
	 * configured folders containing Profiles for display and reading into
	 * the program.
	 * 
	 * @return
	 */
	
	public static ArrayList<String> readValidProfiles(){
		File f = new File(Controller.CONFIG_FILE_PATH);
		String[] contents = f.list();
		ArrayList<String> out = new ArrayList<String>();
		for(String s : contents) {
			if(validateProfileFolder(s)) {
				out.add(s);
			}
		}
		return out;
	}
	
	/**
	 * 
	 * Function to establish default starting values for the config file; ensures the config
	 * file exists and has each key prefix extant in it.
	 * 
	 * Sets default profile to a system default that tells the user to click on the program to edit it
	 * 
	 * Sets the default profile autoset mode to automatically rewrite the default profile
	 * 
	 */
	
	public static void populateConfigDefaultValues() {
		try {
			if(getDefaultProfile() == null)
				establishDefaultProfile("Default");
			if(getStatusDefaultProfileAutoset() == null)
				setStatusDefaultProfileAutoset(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Function that updates the contents of the config.txt file to change the
	 * default Profile (the one loaded in on start-up) to the one designated.
	 * 
	 * 
	 * @param profileName
	 */
	
	public static void establishDefaultProfile(String profileName) throws Exception{
		if(!validateProfileFolder(profileName)) {
			throw new Exception("Invalid Profile referenced: " + profileName);
		}
		assignConfigData(CONFIG_PREFIX_DEFAULT_PROFILE, profileName);
	}
	
	/**
	 * 
	 * Function that changes the config value for whether or not to update the default Profile
	 * to the last used Profile whenever the Profile changes; set to TRUE if you want it to
	 * automatically rewrite the default Profile, FALSE if you want to manually set your default
	 * and NOT have it be overwritten automatically.
	 * 
	 * @param in
	 * @throws IOException
	 */
	
	public static void setStatusDefaultProfileAutoset(boolean in) throws IOException {
		assignConfigData(CONFIG_PREFIX_DEFAULT_SET_AUTO, in ? CONFIG_KEYWORD_AUTO_SET : CONFIG_KEYWORD_MANUAL_SET);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public static String getDefaultProfile() {
		try {
			return getConfigData(CONFIG_PREFIX_DEFAULT_PROFILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * If it returns TRUE, the system is set to automatically overwrite the default profile in
	 * the config menu whenever you change active Profiles. If it returns FALSE, the system is set
	 * to not overwrite the default value; the user can manually select the default Profile and it
	 * will stay as the default.
	 * 
	 * @return
	 */
	
	public static Boolean getStatusDefaultProfileAutoset() {
		try {
			String val = getConfigData(CONFIG_PREFIX_DEFAULT_SET_AUTO);
			return val == null ? null : val.equals(CONFIG_KEYWORD_AUTO_SET);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static String getConfigData(String configLabel) throws IOException {
		File f = new File(Controller.CONFIG_FILE_PATH + "/" + CONFIG_FILE_NAME);
		f.createNewFile();
		ArrayList<String> lines = retrieveFileContents(f);
		for(String s : lines) {
			if(s.startsWith(configLabel)) {
				String[] parts = s.split(":");
				return parts[1];
			}
		}
		return null;
	}
	
	private static void assignConfigData(String configLabel, String newData) throws IOException {
		File f = new File(Controller.CONFIG_FILE_PATH + "/" + CONFIG_FILE_NAME);
		f.createNewFile();
		ArrayList<String> contents = retrieveFileContents(f);
		boolean found = false;
		for(int i = 0; i < contents.size(); i++) {
			if(contents.get(i).startsWith(configLabel)) {
				contents.set(i, configLabel + ":" + newData);
				found = true;
			}
		}
		if(!found) {
			contents.add(configLabel + ":" + newData);
		}
		writeFileContents(f, contents);
	}
	
	private static ArrayList<String> retrieveFileContents(File f) throws FileNotFoundException{
		Scanner sc = new Scanner(f);
		ArrayList<String> out = new ArrayList<String>();
		while(sc.hasNextLine()) {
			out.add(sc.nextLine());
		}
		sc.close();
		return out;
	}
	
	private static void writeFileContents(File f, String contents) throws IOException {
		f.delete();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		raf.writeBytes(contents);
		raf.close();
	}
	
	private static void writeFileContents(File f, ArrayList<String> contents) throws IOException {
		f.delete();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		for(String s : contents) {
			raf.writeBytes(s + "\n");
		}
		raf.close();
	}
	
	/**
	 * 
	 * Function that ensures the proffered folder contains an appropriate
	 * config file and has the designated images present.
	 * 
	 * TODO: Should have all the validations here, even for Effects and Filters.
	 * 
	 * @param profileName
	 * @return
	 */
	
	private static boolean validateProfileFolder(String profileName) {
		File g = new File(Controller.CONFIG_FILE_PATH + "/" + profileName + "/" + CONFIG_FILE_NAME);
		if(g.exists()) {
			Profile p = readInProfile(profileName);
			for(String s : p.getAllBaseImagePaths()) {
				File h = new File(s);
				if(!h.exists()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
