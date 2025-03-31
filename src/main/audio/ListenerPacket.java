package main.audio;

import java.net.ServerSocket;
import java.net.Socket;

public class ListenerPacket {
	
//---  Instance Variables   -------------------------------------------------------------------

	private ServerSocket server;
	private Socket client;
	private Long lastReceived;
	private Thread listener;
	private Thread timeOut;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ListenerPacket() {
		lastReceived = 0L;
	}
	
	public ListenerPacket(int port) {
		try {
			server = new ServerSocket(port);
			client = server.accept();
			lastReceived = System.currentTimeMillis();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void updateLastReceived() {
		lastReceived = System.currentTimeMillis();
	}
	
	public void restartServer(int port) {
		try {
			if(server != null) {
				server.close();
				client.close();
			}
			server = new ServerSocket(port);
			client = server.accept();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeServers() {
		try {
			if(server != null) {
				server.close();
				client.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void assignThreads(Thread listen, Thread time) {
		listener = listen;
		timeOut = time;
	}
	
	public void killListenerThread() {
		if(listener != null) {
			listener.interrupt();
		}
	}
	
	public void killTimerThread() {
		if(timeOut != null) {
			timeOut.interrupt();
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------0
	
	public ServerSocket getServer() {
		return server;
	}
	
	public Socket getClient() {
		return client;
	}
	
	public Long getLastReceived() {
		return lastReceived;
	}

}
