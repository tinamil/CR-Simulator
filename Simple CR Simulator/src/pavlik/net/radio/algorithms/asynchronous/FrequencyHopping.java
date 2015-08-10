package pavlik.net.radio.algorithms.asynchronous;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

/**
 * Algorithm will be operated by a radio as such:
 * 
 * <pre>
 * {@code
  while(isSynced() == false){
	 nextChannel();
	 broadcastSync();
	 pauseForHop();
  }
 * </pre>
 * 
 * @author John
 *
 */
public class FrequencyHopping extends RendezvousAlgorithm {

	private static final Logger log = Logger.getLogger(FrequencyHopping.class.getName());

	// Seed will be generated the first time this class is instantiated
	private static byte[]	SEED		= null;
	private static int		SEED_SIZE	= 512;

	// The radios clock will be set to current time plus/minus the MAX_TIME_OFFSET
	private static long	MAX_TIME_OFFSET	= 4000;
	private long		timeOffset;
	private long		startTime;

	// Number of required hops to be correct in order to synchronize
	private static final long REQUIRED_SYNC_HOPS = 10;

	// Maximum number of hop attempts to synchronize before aborting
	private static final long MAX_SYNC_HOPS = 20;

	// Modifier applied to base HOP_RATE in order to search for the right network during the Seeking
	// phase
	private static final double SEARCH_SPEED = 0.5;

	// Number of successful hop attempts
	long hitCount = 0;

	boolean synced = false;

	// Number of channels in the sliding window
	private static int WINDOW_CHANNEL_COUNT = ((int) (MAX_TIME_OFFSET / HOP_RATE) + 1) * 2;

	// A sliding time window of indices into the channels[]
	int[] slidingWindow = new int[WINDOW_CHANNEL_COUNT];

	// Index to the sliding window of the current estimated channel
	int currentSlidingIndex = 0;

	// The index to the last update of the sliding window
	int lastWindowUpdate;

	private enum State {
		SeekingRendezvous, OperatingNetwork, Syncing
	}

	State		state;
	Channel[]	channels;

	java.security.SecureRandom secureRand;

	public FrequencyHopping(String id, Channel[] channels) {
		super(id);
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
		timeOffset = (new Random().nextLong() % (2 * MAX_TIME_OFFSET + 1)) - MAX_TIME_OFFSET;

		startTime = getCurrentMillis();

		// Build ranking table with Channels array
		this.channels = channels;
		Arrays.sort(channels, new Comparator<Channel>() {
			public int compare(Channel o1, Channel o2) {
				return o1.compareTo(o2);
			};
		});

		// Pre-load the sliding window with valid channels
		initializeSlidingWindow();

		// Default to a Seeking Rendezvous state
		this.state = State.SeekingRendezvous;
	}

	@Override
	public Channel nextChannel() {
		updateSlidingWindow();

		int tmpIndex = currentSlidingIndex;
		currentSlidingIndex = (currentSlidingIndex + 1) % WINDOW_CHANNEL_COUNT;
		return channels[slidingWindow[tmpIndex]];
	}

	private int generateSecureRandomInt() {
		byte[] bytes = new byte[4];
		secureRand.nextBytes(bytes);
		int nextVal = ByteBuffer.wrap(bytes).getInt();
		return nextVal;
	}

	private void initializeSlidingWindow() {
		for (int i = 0; i < WINDOW_CHANNEL_COUNT; ++i) {
			slidingWindow[i] = generateSecureRandomInt() % channels.length;
		}
		lastWindowUpdate = WINDOW_CHANNEL_COUNT;
	}

	private void updateSlidingWindow() {
		long currentNetworkRound = (getCurrentMillis() - startTime) / HOP_RATE;
		while (lastWindowUpdate <= currentNetworkRound) {
			slidingWindow[lastWindowUpdate % WINDOW_CHANNEL_COUNT] = generateSecureRandomInt()
					% channels.length;
			lastWindowUpdate++;
		}
	}

	private long getCurrentMillis() {
		return System.currentTimeMillis() + timeOffset;
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			state = State.Syncing;
			//TODO Transition to Syncing state
			hitCount += 1;
			currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: " + currentChannel
					.toString());
		}
		if (message.contains("1ACKHELLO")) {
			hitCount += 1;
		}

		if (hitCount >= REQUIRED_SYNC_HOPS) synced = true;
	}

	@Override
	public void broadcastSync(Channel currentChannel) {
		currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel
				.toString());
	}

	@Override
	public boolean isSynced() {
		return synced;
	}

	private long getStateHopRate() {
		long freqHopRate;
		switch (state) {
			case Syncing:
			case OperatingNetwork:
				freqHopRate = HOP_RATE;
			case SeekingRendezvous:
				freqHopRate = (long) Math.ceil(SEARCH_SPEED * HOP_RATE);
			default:
				freqHopRate = HOP_RATE;
		}
		return freqHopRate;
	}

	@Override
	public void pauseForHop() {
		long freqHopRate = getStateHopRate();
		long currentTime = getCurrentMillis();
		if (currentTime - lastHopTime < freqHopRate) {
			try {
				Thread.sleep(freqHopRate - (currentTime - lastHopTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastHopTime = currentTime;
	}
}
