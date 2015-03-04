package pavlik.net.radio.algorithms.asynchronous;

import java.math.BigInteger;
import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class JumpStay extends RendezvousAlgorithm {

	Channel[]	channels;
	int			prime;
	int			r0, i0, t;
	Random		rand	= new Random();

	public JumpStay(Channel[] channels) {
		super(channels);
		this.channels = channels;
		this.prime = BigInteger.valueOf(channels.length).nextProbablePrime().intValue();
		this.r0 = rand.nextInt(channels.length) + 1;
		this.i0 = rand.nextInt(prime) + 1;
		this.t = 0;
	}

	@Override
	public Channel nextChannel() {
		int n = Math.floorDiv(t, (3 * prime));
		int r = ((r0 + n - 1) % channels.length) + 1;
		int m = Math.floorDiv(t, 3 * channels.length * prime);
		int i = ((i0 + m - 1) % prime) + 1;
		Channel channel = JSHopping(r, i, t);
		t += 1;
		return channel;
	}

	private Channel JSHopping(int r, int i, int t) {
		t %= 3 * prime;
		int j;
		if (t < 2 * prime) {
			j = ((i + t * r - 1) % prime) + 1;
		} else {
			j = r;
		}
		if (j > channels.length) j = ((j - 1) % channels.length) + 1;
		return channels[j - 1];
	}
}
