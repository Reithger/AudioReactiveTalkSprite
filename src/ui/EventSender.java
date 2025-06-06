package ui;

import control.EventProcessor;

public class EventSender {

	private static EventProcessor reference;
	
	public static void sendEvent(int in) {
		reference.processEvent(in);
	}
	
	public static void assignEventProcessor(EventProcessor in) {
		reference = in;
	}
	
}
