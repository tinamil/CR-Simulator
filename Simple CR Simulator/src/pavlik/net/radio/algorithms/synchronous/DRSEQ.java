package pavlik.net.radio.algorithms.synchronous;

import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class DRSEQ extends RendezvousAlgorithm {

	Channel[]	channels;
	int			index;
	Random		rand	= new Random();

	public DRSEQ(Channel[] channels) {
		super(channels);
		this.channels = channels;
		this.index = rand.nextInt(channels.length);
	}

	@Override
	public Channel nextChannel() {
		index += 1;
		index %= 2*channels.length + 1;
		if (index < channels.length) {
			return channels[index];
		} else if (index == channels.length) return channels[rand.nextInt(channels.length)];
		else return channels[2 * channels.length - index];
	}

}
