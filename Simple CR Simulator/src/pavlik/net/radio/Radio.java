package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.Channel.ChannelListener;

public class Radio extends Thread {

	private static final Logger log = Logger.getLogger(Radio.class.getName());

	Channel currentChannel;
	String id;
	RendezvousAlgorithm algorithm;
	public volatile boolean running = true;
	RadioListener listener;

	public Radio(String name, RendezvousAlgorithm algorithm) {
		log.info("Radio created: " + name);
		this.id = name;
		this.algorithm = algorithm;
		// Must set the algorithm before calling nextChannel
		listener = new RadioListener();
		nextChannel();
	}

	private class RadioListener implements ChannelListener {
		@Override
		public void receiveBroadcast(String message) {
			if (message.startsWith(id))
				return;
			log.info("Message received: " + message);
			if (message.contains("0HELLO")) {
				currentChannel.broadcastMessage(id + " 1" + "ACK on channel: "
						+ currentChannel.toString());
			}
			if (message.contains("1ACK")) {
				running = false;
			}
		}

	}

	public void nextChannel() {
		if(currentChannel != null){
			currentChannel.removeListener(listener);
		}
		currentChannel = algorithm.nextChannel();
		log.info(id + " now on channel :" + currentChannel);
		currentChannel.addListener(listener);
	}

	@Override
	public void run() {
		while (running) {
			nextChannel();
			currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: "
					+ currentChannel.toString());
		}
	}

	public void stopSimulation() {
		running = false;
	}
}
