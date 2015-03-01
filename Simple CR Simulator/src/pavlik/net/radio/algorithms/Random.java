package pavlik.net.radio.algorithms;

import pavlik.net.Channel.Spectrum.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class Random extends RendezvousAlgorithm {

	Channel[]			channels;
	java.util.Random	rand	= new java.util.Random();

	public Random(Channel[] channels) {
		super(channels);
		this.channels = channels;
	}

	@Override
	public Channel nextChannel() {
		return channels[rand.nextInt(channels.length)];
	}

}
