package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.Channel.ChannelListener;

public class Radio extends Thread {

	private static final Logger	log				= Logger.getLogger(Radio.class.getName());

	Channel						currentChannel;
	String						id;
	RendezvousAlgorithm			algorithm;
	public volatile boolean		running			= true;
	RadioListener				listener;
	public boolean				syncComplete	= false;

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
			if (message.startsWith(id)) return;
			log.info("Message received: " + message);
			if (message.contains("0HELLO") && !syncComplete) {
				currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: "
						+ currentChannel.toString());
				running = false;
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (message.contains("1ACKHELLO")) {
				currentChannel.broadcastMessage(id + " 2ACK");
				running = false;
			}
			if (message.contains("2ACK")) {
				running = false;
			}
		}

	}

	public void nextChannel() {
		if (currentChannel != null) {
			currentChannel.removeListener(listener);
		}
		currentChannel = algorithm.nextChannel();
		log.info(id + " now on channel :" + currentChannel);
		currentChannel.addListener(listener);
	}

	@Override
	public void run() {
		while (running) {
			nextStep();

			// Pause to listen on the channel for a response, otherwise the radio spends half of
			// its time changing channels, assuming that all of the time is spent acquiring
			// synchronization locks on the channel.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopSimulation() {
		running = false;
	}

	public void nextStep() {
		if (running) {
			nextChannel();
		}
		if (running) {
			currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: "
					+ currentChannel.toString());
		}
	}
}
