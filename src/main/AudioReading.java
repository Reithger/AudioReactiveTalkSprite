package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class AudioReading {
	
	private static final int PERIODIC_AUDIO_CHECK_TIME = 250;
	
	public AudioReading() {
		verifyPythonFileNear();
		callAudioCheck();
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
		File f = new File("./pngassets/read_audio.py");
		if(!f.exists()) {
			try {
				ArrayList<String> contents = getTemplatePythonContents();
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
				f = new File("/assets/read_audio.txt");
				System.out.println(f.getAbsolutePath());
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
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				try {
					Runtime.getRuntime().exec("python pngassets/read_audio.py");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Timer time = new Timer();
		time.schedule(tt, 15, PERIODIC_AUDIO_CHECK_TIME);
	}

}
