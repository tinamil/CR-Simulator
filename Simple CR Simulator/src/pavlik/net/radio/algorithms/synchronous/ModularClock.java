package pavlik.net.radio.algorithms.synchronous;

import java.math.BigInteger;
import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class ModularClock extends RendezvousAlgorithm {

	Channel[] channels;
	int index;
	int prime;
	int rate;
	int timeCount = 0;
	Random rand = new Random();

	public ModularClock(Channel[] channels) {
		super(channels);
		this.channels = channels;
		this.prime = BigInteger.valueOf(channels.length).nextProbablePrime()
				.intValue();
		this.index = rand.nextInt(channels.length);
		this.rate = rand.nextInt(channels.length - 2) +2;
	}

	@Override
	public Channel nextChannel() {
		timeCount += 1;
		if (timeCount > 2 * prime) {
			rate = rand.nextInt(prime);
			timeCount = 0;
		}
		index += rate;
		index %= prime;
		if (index < channels.length) {
			return channels[index];
		} else {
			return channels[index % channels.length];
		}
	}

}
