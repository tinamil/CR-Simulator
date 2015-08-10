package pavlik.net.radio.algorithms.asynchronous;

import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class ShortSequenceBased extends RendezvousAlgorithm {

	private Channel[]	channels;
	private int			index;
	private Random		rand	= new Random();

	public ShortSequenceBased(String id, Channel[] channels) {
		super(id);
		this.channels = channels;
		this.index = rand.nextInt(channels.length);
	}

	@Override
	public Channel nextChannel() {
		index += 1;
		if (index >= channels.length * 2) {
			index = 0;
		}
		if (0 <= index && index < channels.length) {
			return channels[index];
		} else {
			return channels[channels.length - 1 - index % channels.length];
		}
	}
}
