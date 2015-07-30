package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.protocol.RadioProtocol;

public class Radio extends Thread {

	private static final Logger	log		= Logger.getLogger(Radio.class.getName());

	String						id;
	RadioProtocol				listener;
	volatile boolean			running	= true;
	Channel						currentChannel;
	RendezvousAlgorithm			algorithm;

	public Radio(String name, RendezvousAlgorithm algorithm) {
		log.info("Radio created: " + name);
		this.id = name;
		this.algorithm = algorithm;
		this.listener = algorithm.getProtocol(name);
	}

	@Override
	public void run() {
		while (running) {
			nextStep();
		}
	}

	public void stopSimulation() {
		running = false;
	}

	public void nextStep() {
		if (running) {
			nextChannel();
			listener.broadcastSync(currentChannel);
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

	public boolean isSyncComplete() {
		return listener.isSynced();
	}
}
