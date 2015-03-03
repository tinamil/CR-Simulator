package pavlik.net.radio.algorithms.asynchronous;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class RandomAlgorithm extends RendezvousAlgorithm {

	Channel[]			channels;
	java.util.Random	rand	= new java.util.Random();

	public RandomAlgorithm(Channel[] channels) {
		super(channels);
		this.channels = channels;
	}

	@Override
	public Channel nextChannel() {
		return channels[rand.nextInt(channels.length)];
	}

}
