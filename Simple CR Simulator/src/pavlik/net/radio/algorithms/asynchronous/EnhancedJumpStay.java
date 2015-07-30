package pavlik.net.radio.algorithms.asynchronous;

import java.math.BigInteger;
import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class EnhancedJumpStay extends RendezvousAlgorithm {

	Channel[]	channels;
	int			prime;
	int			r0, i0, t;
	Random		rand	= new Random();

	public EnhancedJumpStay(Channel[] channels) {
		super(channels);
		this.channels = channels;
		this.prime = BigInteger.valueOf(channels.length).nextProbablePrime().intValue();
		this.r0 = rand.nextInt(channels.length) + 1;
		this.i0 = rand.nextInt(prime) + 1;
		this.t = 0;
	}

	@Override
	public Channel nextChannel() {
		int n = Math.floorDiv(t, (4 * prime));
		int i = ((i0 + n - 1) % prime) + 1;
		Channel channel = EJSHopping(i, t);
		t += 1;
		return channel;
	}

	private Channel EJSHopping(int i, int t) {
		t %= 4 * prime;
		int j;
		if (t < 3 * prime) {
			j = ((i + t * r0 - 1) % prime) + 1;
		} else {
			j = r0;
		}
		if (j >= channels.length) j %= channels.length;
		return channels[j];
	}
}
