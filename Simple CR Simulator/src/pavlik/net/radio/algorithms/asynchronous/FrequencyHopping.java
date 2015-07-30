package pavlik.net.radio.algorithms.asynchronous;

import java.security.NoSuchAlgorithmException;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;
import pavlik.net.radio.protocol.FreqHopProtocol;
import pavlik.net.radio.protocol.RadioProtocol;

public class FrequencyHopping extends RendezvousAlgorithm {

	// Seed will be generated the first time this class is instantiated
	private static byte[]		SEED			= null;
	// The radios clock will be set to current time plus/minus the MAX_TIME_OFFSET
	private static long			MAX_TIME_OFFSET	= 4000;

	Channel[]					channels;

	private static int			SEED_SIZE		= 250;
	java.security.SecureRandom	rand;
	private long				timeOffset;
	State						state;

	public FrequencyHopping(Channel[] channels) {
		super(channels);
		// If this is the first radio in the network, generate a shared seed for ALL radios
		if (FrequencyHopping.SEED == null) try {
			FrequencyHopping.SEED = java.security.SecureRandom.getInstanceStrong().generateSeed(
					SEED_SIZE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		rand = new java.security.SecureRandom(SEED);
		timeOffset = rand.nextLong() % MAX_TIME_OFFSET;
		if (rand.nextBoolean()) timeOffset = -timeOffset;
		this.channels = channels;
		state = State.SeekingRendezvous;
	}

	@Override
	public Channel nextChannel() {
		return channels[rand.nextInt(channels.length)];
	}

	@Override
	public RadioProtocol getProtocol(String id) {
		return new FreqHopProtocol(id);
	}

	private enum State {
		SeekingRendezvous, OperatingNetwork, Syncing
	}
}
