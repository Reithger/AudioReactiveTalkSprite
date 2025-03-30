package main.audio;

import java.net.ServerSocket;
import java.net.Socket;

public class ListenerPacket {

	private ServerSocket server;
	private Socket client;
	private Long lastReceived;
	
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
	
	public ServerSocket getServer() {
		return server;
	}
	
	public Socket getClient() {
		return client;
	}
	
	public Long getLastReceived() {
		return lastReceived;
	}
	
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
	
}
