package pavlik.net.radio.algorithms.asynchronous;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;
import pavlik.net.radio.protocol.FreqHopProtocol;
import pavlik.net.radio.protocol.RadioProtocol;

public class FrequencyHopping extends RendezvousAlgorithm {

	// Seed will be generated the first time this class is instantiated
	private static byte[]	SEED		= null;
	private static int		SEED_SIZE	= 512;

	// The radios clock will be set to current time plus/minus the MAX_TIME_OFFSET
	private static long	MAX_TIME_OFFSET	= 4000;
	private long		timeOffset;

	FreqHopProtocol protocol;

	// State state;
	// private enum State {
	// SeekingRendezvous, OperatingNetwork, Syncing
	// }

	Channel[] channels;

	java.security.SecureRandom secureRand;

	public FrequencyHopping(Channel[] channels) {
		super(channels);
		// If this is the first radio in the network, generate a shared seed for ALL radios
		if (FrequencyHopping.SEED == null) try {
			FrequencyHopping.SEED = java.security.SecureRandom.getInstanceStrong().generateSeed(
					SEED_SIZE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Initialize radios with a cryptographically secure PRNG and shared SEED
		secureRand = new java.security.SecureRandom(SEED);

		// Setup a random time offset that is plus or minus the MAX_TIME_OFFSET
		timeOffset = (secureRand.nextLong() % (2 * MAX_TIME_OFFSET + 1)) - MAX_TIME_OFFSET;

		// Build ranking table with Channels array
		this.channels = channels;
		Arrays.sort(channels, new Comparator<Channel>() {
			public int compare(Channel o1, Channel o2) {
				return o1.compareTo(o2);
			};
		});
	}

	@Override
	public Channel nextChannel() {
		// TODO Sliding Window of channels
		byte[] bytes = new byte[4];
		secureRand.nextBytes(bytes);
		int nextVal = ByteBuffer.wrap(bytes).getInt();
		return channels[nextVal % channels.length];
	}

	@Override
	public RadioProtocol getProtocol(String id) {
		this.protocol = new FreqHopProtocol(id);
		return protocol;
	}

	private long getCurrentMillis() {
		return System.currentTimeMillis() + timeOffset;
	}

}
