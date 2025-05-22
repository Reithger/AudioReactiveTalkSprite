package main.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import main.AudioReading;
import main.Controller;

public class PythonListenerValidation {
	
	public static void verifyPythonFileNear() {
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
	
	private static boolean validateFileCorrect(File f) {
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
	
	private static ArrayList<String> getTemplatePythonContents() {
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
