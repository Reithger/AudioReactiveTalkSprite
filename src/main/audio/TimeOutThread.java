package main.audio;

public class TimeOutThread extends Thread{

	private volatile ListenerPacket packet;
	private InitiateListening reference;
	private int checkRate;
	private int timeoutPeriod;
	
	private volatile boolean keepAlive;
	
	public TimeOutThread(ListenerPacket context, InitiateListening refIn, int checkTimer, int timeout) {
		packet = context;
		reference = refIn;
		checkRate = checkTimer;
		timeoutPeriod = timeout;
		keepAlive = true;
	}
	
	@Override
	public void run() {
		while(keepAlive) {
			try {
				Thread.sleep(checkRate);
				if(System.currentTimeMillis() - packet.getLastReceived() > timeoutPeriod && !packet.getLastReceived().equals(0L)) {
					System.out.println("Listener Thread timed out on communication with Python process, restarting");
					reference.setUpListening();
					break;
				}
				else {
					System.out.println("Listener Thread still in communication with Python process, last check in: " + packet.getLastReceived());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void end() {
		keepAlive = false;
	}
	
}
