package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * Need to adjust this so that the Port we use is dynamic and we can communicate a different port to the python program.
 * 
 */

public class AudioReading {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int START_PORT = 5439;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private AudioLevelPasser passTo;
	private double audioAdjustment;
	
	public AudioReading(AudioLevelPasser reference) {
		audioAdjustment = 1;
		verifyPythonFileNear();
		passTo = reference;
		setUpListening();
	}
	
	public void setAudioLevelAdjustment(double in) {
		audioAdjustment = in;
	}
	
	private void setUpListening() {
		startLocalListener(passTo);
		callAudioCheck();
	}
	
	private void startLocalListener(AudioLevelPasser reference){
		Thread thread = new Thread() {
			@Override
			public void run() {
				System.out.println("Starting Local Listener Service");
				ServerSocket server = null;
				Socket client = null;
				try {
					server = new ServerSocket(START_PORT);
					client = server.accept();
					System.out.println(client);
					BufferedReader receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String received = receiver.readLine();
					while(received != null && !received.equals("exit")) {
						if(!received.equals(""))
							reference.receiveAudio((int)(audioAdjustment * Integer.parseInt(received)));
						received = receiver.readLine();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					System.out.println("Connection Died, Restarting Listener Processes");
					try {
						server.close();
						client.close();
						setUpListening();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	
	public double getCurrentAudio() {
		File f = new File("pngassets/audio_level.txt");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner sc = null;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(sc.hasNextLine()) {
			String line = sc.nextLine();
			return Double.parseDouble(line);
		}
		return 0;
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
			System.out.println("read_audio.py file not validated, rewrite; error occured");
			return false;
		}
		
	}
	
	private ArrayList<String> getTemplatePythonContents() {
		InputStream is = null;
		Scanner sc;
		try {
			is = AudioReading.class.getResourceAsStream("assets/read_audio.txt");
			sc = new Scanner(is);
		}
		catch(Exception e) {
			try {
				File f;
				f = new File(Controller.CONFIG_FILE_PATH + "read_audio.txt");
				sc = new Scanner(f);
			}
			catch(Exception e1) {
				e1.printStackTrace();
				return null;
			}
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
			Runtime.getRuntime().exec("python " + Controller.CONFIG_FILE_PATH + "read_audio.py");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
