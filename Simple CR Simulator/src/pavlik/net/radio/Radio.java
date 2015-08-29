package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;

public class Radio {

	private static final Logger log = Logger.getLogger(Radio.class.getName());

	String				id;
	volatile boolean	running	= true;
	Channel				currentChannel;
	RendezvousAlgorithm	algorithm;

	public Radio(String name, RendezvousAlgorithm algorithm) {
		log.info("Radio created: " + name);
		this.id = name;
		this.algorithm = algorithm;
	}

	public void stopSimulation() {
		running = false;
	}

//	public void nextStep() {
//		if (running) {
//			nextChannel();
//			algorithm.broadcastSync(currentChannel);
//			algorithm.pauseForHop();
//		}
//	}

	public void sync(){
		algorithm.broadcastSync(currentChannel);
	}
	
	public void nextChannel() {
		if (currentChannel != null) {
			currentChannel.removeListener(algorithm);
		}
		currentChannel = algorithm.nextChannel();
		log.info(id + " now on channel :" + currentChannel);
		currentChannel.addListener(algorithm);
	}

	public boolean isSyncComplete() {
		return algorithm.isSynced();
	}
}
