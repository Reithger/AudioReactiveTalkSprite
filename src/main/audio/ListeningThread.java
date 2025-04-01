package main.audio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ListeningThread extends Thread{

	private volatile ListenerPacket packet;
	private AudioLevelPasser reference;
	private int currentPort;
	
	private volatile boolean keepAlive;
	
	public ListeningThread(ListenerPacket context, AudioLevelPasser refSend, int currPort) {
		packet = context;
		reference = refSend;
		currentPort = currPort;
		keepAlive = true;
	}
	
	@Override
	public void run() {
		System.out.println("Starting Local Listener Service");
		try {
			packet.restartServer(currentPort);
			Socket client = packet.getClient();
			System.out.println(client);
			BufferedReader receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String received = receiver.readLine();
			while(received != null && !received.equals("exit") && keepAlive) {
				if(!received.equals(""))
					reference.receiveAudio((int)(Integer.parseInt(received)));
				received = receiver.readLine();
				packet.updateLastReceived();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("Connection Died, Restarting Listener Processes");
		}
	}
	
	public void end() {
		keepAlive = false;
	}
	
}
