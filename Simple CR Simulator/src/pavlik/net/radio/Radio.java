package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.Channel.ChannelListener;

public class Radio extends Thread implements ChannelListener {

	private static final Logger	log		= Logger.getLogger(Radio.class.getName());

	Channel						currentChannel;
	String						id;
	RendezvousAlgorithm			algorithm;
	public volatile boolean		running	= true;

	public Radio(String name, RendezvousAlgorithm algorithm) {
		log.info("Radio created: " + name);
		this.id = name;
		this.algorithm = algorithm;
		// Must set the algorithm before calling nextChannel
		nextChannel(false);
	}

	public void nextChannel() {
		nextChannel(true);
	}

	public void nextChannel(boolean removeListener) {
		if (removeListener) currentChannel.removeListener(this);
		currentChannel = algorithm.nextChannel();
		log.info(id + " now on channel :" + currentChannel);
		currentChannel.addListener(this);
	}

	@Override
	public void run() {
		while (running) {
			nextChannel();
			try {
				Thread.sleep(Math.round((Math.random() * 100)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: "
					+ currentChannel.toString());
		}
	}

	@Override
	public void receiveBroadcast(String message) {
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			currentChannel.broadcastMessage(id + " 1" + "ACK on channel: "
					+ currentChannel.toString());
		}
		if (message.contains("1ACK")) {
			running = false;
		}
	}

	public void stopSimulation() {
		running = false;
	}
}
