package main.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;
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

public class AudioReading {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int START_PORT = 5439;
	
	private final static int TIMEOUT_PERIOD = 5000;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private AudioLevelPasser passTo;
	private double audioAdjustment;
	
	private int currentPort;
	
	public AudioReading(AudioLevelPasser reference) {
		audioAdjustment = 1;
		verifyPythonFileNear();
		passTo = reference;
		setUpListening();
		currentPort = START_PORT;
	}
	
	public void setAudioLevelAdjustment(double in) {
		audioAdjustment = in;
	}
	
	private void setUpListening() {
		iteratePortNumber();
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
		ListenerPacket packet = new ListenerPacket();
		
		Thread thread = new Thread() {
			private volatile ListenerPacket infoRef = packet;
			
			@Override
			public void run() {
				System.out.println("Starting Local Listener Service");
				try {
					infoRef.restartServer(currentPort);
					Socket client = infoRef.getClient();
					System.out.println(client);
					BufferedReader receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String received = receiver.readLine();
					while(received != null && !received.equals("exit")) {
						if(!received.equals(""))
							reference.receiveAudio((int)(audioAdjustment * Integer.parseInt(received)));
						received = receiver.readLine();
						infoRef.updateLastReceived();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					System.out.println("Connection Died, Restarting Listener Processes");
					infoRef.closeServers();
					infoRef.killTimerThread();
					setUpListening();
				}
			}
		};
		
		Thread timeOut = new Thread() {
			private volatile ListenerPacket infoRef = packet;
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(TIMEOUT_PERIOD);
						if(System.currentTimeMillis() - infoRef.getLastReceived() > TIMEOUT_PERIOD && !infoRef.getLastReceived().equals(0L)) {
							System.out.println("Listener Thread timed out on communication with Python process, restarting");
							infoRef.closeServers();
							infoRef.killListenerThread();
							setUpListening();
							break;
						}
						else {
							System.out.println("Listener Thread still in communication with Python process, last check in: " + infoRef.getLastReceived());
						}
					} catch (Exception e) {
						e.printStackTrace();
						infoRef.closeServers();
						infoRef.killListenerThread();
						setUpListening();
					}
				}
			}
		};
		
		packet.assignThreads(thread, timeOut);
		
		thread.start();
		timeOut.start();
	}

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
	
	private void callAudioCheck() {
		try {
			Runtime.getRuntime().exec("python " + Controller.CONFIG_FILE_PATH + "read_audio.py " + currentPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
