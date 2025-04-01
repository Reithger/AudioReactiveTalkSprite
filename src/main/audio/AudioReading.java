package main.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import main.Controller;

/**
 * 
 * Need to adjust this so that the Port we use is dynamic and we can communicate a different port to the python program.
 * 
 * Need additional timer so that if the Python just stops responding without closing, resets the listener
 * 
 * Also just figure out how to pass arguments to the Python program so we can tell it the correct port number
 * 
 */

public class AudioReading implements InitiateListening{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int START_PORT = 5439;
	
	private final static int TIMEOUT_PERIOD = 5000;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private AudioLevelPasser passTo;
	private double audioAdjustment;
	private int currentPort;
	private volatile ListenerPacket packet;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public AudioReading(AudioLevelPasser reference) {
		audioAdjustment = 1;
		verifyPythonFileNear();
		passTo = reference;
		setUpListening();
		currentPort = START_PORT;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void setUpListening() {
		iteratePortNumber();
		if(packet != null) {
			packet.closeOutSession();
		}
		startLocalListener(passTo);
		callAudioCheck();
	}

	private void iteratePortNumber() {
		currentPort++;
		if(currentPort > 7500) {
			currentPort = 3500;
		}
	}
	
	private void startLocalListener(AudioLevelPasser reference){
		packet = new ListenerPacket();
		
		ListeningThread listen = new ListeningThread(packet, reference, currentPort);
		
		TimeOutThread timeOut = new TimeOutThread(packet, this, 1000, TIMEOUT_PERIOD);
		
		packet.assignThreads(listen, timeOut);
		
		listen.start();
		timeOut.start();
	}
	
	private void callAudioCheck() {
		try {
			Runtime.getRuntime().exec("python " + Controller.CONFIG_FILE_PATH + "read_audio.py " + currentPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//---  Setter Methods   -----------------------------------------------------------------------

	public void setAudioLevelAdjustment(double in) {
		audioAdjustment = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public double getAudioAdjustment() {
		return audioAdjustment;
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private void verifyPythonFileNear() {
		File f = new File(Controller.CONFIG_FILE_PATH + "/read_audio.py");
		if(!f.exists() || !validateFileCorrect(f)) {
			try {
				ArrayList<String> contents = getTemplatePythonContents();
				f.delete();
				f.createNewFile();
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				for(String s : contents) {
					raf.writeBytes(s + "\n");
				}
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private boolean validateFileCorrect(File f) {
		System.out.println("Validating read_audio.py file correctness");
		Scanner sc = null;
		try {
			sc = new Scanner(f);
			ArrayList<String> compare = new ArrayList<String>();
			while(sc.hasNextLine()) {
				compare.add(sc.nextLine());
			}
			sc.close();
			ArrayList<String> correct = getTemplatePythonContents();
			if(compare.size() != correct.size()) {
				System.out.println("read_audio.py file not validated, rewrite");
				return false;
			}
			for(int i = 0; i < compare.size(); i++) {
				if(!compare.get(i).equals(correct.get(i))) {
					System.out.println("read_audio.py file not validated, rewrite");
					return false;
				}
			}
			System.out.println("read_audio.py file validated, safe to use");
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("read_audio.py file not validated, no rewrite performed; error occured");
			return false;
		}
		
	}
	
	private ArrayList<String> getTemplatePythonContents() {
		InputStream is = null;
		Scanner sc;
		try {
			is = AudioReading.class.getResourceAsStream("../assets/read_audio.txt");
			sc = new Scanner(is);
		}
		catch(Exception e) {
			is = AudioReading.class.getResourceAsStream("/main/assets/read_audio.txt");
			sc = new Scanner(is);
		}
		ArrayList<String> out = new ArrayList<String>();
		while(sc.hasNextLine()) {
			out.add(sc.nextLine());
		}
		sc.close();
		return out;
	}

}
