package pavlik.net.radio.algorithms.asynchronous;

import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class ShortSequenceBased extends RendezvousAlgorithm {

	private Channel[]	channels;
	private int			index;
	private int			round	= 0;
	private Random		rand	= new Random();

	public ShortSequenceBased(Channel[] channels) {
		super(channels);
		this.channels = channels;
		this.index = rand.nextInt(channels.length);
	}

	@Override
	public Channel nextChannel() {
		index += 1;
		if (index % channels.length == 0) {
			round += 1;
		}
		if (round * (2 * channels.length - 1) <= index
				&& index < round * (2 * channels.length - 1) + channels.length) {
			return channels[(round + index) % channels.length];
		} else {
			return channels[channels.length - 2 - ((round + index) % channels.length)];
		}
	}
}
