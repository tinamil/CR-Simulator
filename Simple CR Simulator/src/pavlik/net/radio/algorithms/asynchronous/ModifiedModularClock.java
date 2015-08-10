package pavlik.net.radio.algorithms.asynchronous;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class ModifiedModularClock extends RendezvousAlgorithm {

	Channel[]		channels;
	int				index;
	int				prime;
	int				rate;
	int				timeCount	= 0;
	static Random	rand		= new Random();

	public ModifiedModularClock(String id, Channel[] channels) {
		super(id);
		this.channels = channels;
		this.index = rand.nextInt(channels.length);
		this.prime = randomPrime(channels.length, channels.length * 2);
		this.rate = rand.nextInt(channels.length-2) + 2;
	}

	@Override
	public Channel nextChannel() {
		timeCount += 1;
		if (timeCount > 2 * (prime * prime)) {
			this.prime = randomPrime(channels.length, channels.length * 2);
			this.rate = rand.nextInt(channels.length - 2) + 2;
			timeCount = 0;
		}
		index += rate;
		index %= prime;
		if (index < channels.length) {
			return channels[index];
		} else {
			return channels[rand.nextInt(channels.length)];
		}
	}

	public static int randomPrime(int start, int end) {
		List<Integer> primes = new ArrayList<>();
		for (int current = start; current <= end; current++) {
			long sqr_root = (long) Math.sqrt(current);
			boolean is_prime = true;
			for (long i = 2; i <= sqr_root; i++) {
				if (current % i == 0) {
					is_prime = false; // Current is not prime.
					break;
				}
			}
			if (is_prime) {
				primes.add(current);
			}
		}
		return primes.get(rand.nextInt(primes.size()));
	}
}
