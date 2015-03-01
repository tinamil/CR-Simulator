package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Spectrum.Channel;

public class Radio extends Thread {

	private static final Logger	log	= Logger.getLogger(Radio.class.getName());

	Channel						currentChannel;
	String						id;
	RendezvousAlgorithm			algorithm;

	public Radio(String name, RendezvousAlgorithm algorithm) {
		log.info("Radio created: " + name);
		this.id = name;
		this.algorithm = algorithm;
		// Must set the algorithm before calling nextChannel
		nextChannel();
	}

	public void nextChannel() {
		currentChannel = algorithm.nextChannel();
	}

	@Override
	public void run() {
		super.run();
	}
}
