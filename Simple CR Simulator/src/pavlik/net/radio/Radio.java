package pavlik.net.radio;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.Channel.ChannelListener;

public class Radio extends Thread implements ChannelListener {

	private static final Logger	log		= Logger.getLogger(Radio.class.getName());

	Channel						currentChannel;
	String						id;
	RendezvousAlgorithm			algorithm;
	boolean						running	= true;

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
		log.fine(id + " now on channel :" + currentChannel);
		currentChannel.addListener(this);
	}

	@Override
	public void run() {
		super.run();
		while (running) {
			nextChannel();
			currentChannel.broadcastMessage(id + " " + "HELLO");
		}
	}

	@Override
	public void receiveBroadcast(String message) {
		
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		currentChannel.broadcastMessage(id + " " + "HELLO");
		running = false;
	}

	public void stopSimulation() {
		running = false;
	}
}
