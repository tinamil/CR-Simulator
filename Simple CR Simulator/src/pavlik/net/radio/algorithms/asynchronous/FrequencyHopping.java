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

	private static boolean firstRadioSetup = false;

	// Seed will be generated the first time this class is instantiated
	private static byte[]	SEED		= null;
	private static int		SEED_SIZE	= 512;

	// The radios clock will be set to between the current time and the current time + MAX_TIME_OFFSET
	private static long	MAX_TIME_OFFSET	= 4000;
	private long		timeOffset;
	private long		startTime;

	// Modifier applied to base HOP_RATE in order to search for the right network during the Seeking
	// phase
	private static final double SEARCH_SPEED = 0.5;

	// Uncomment to override and slow down to 1 second hops for debug
	protected static long HOP_RATE = 1000;

	// Number of channels in the sliding window
	private static int WINDOW_CHANNEL_COUNT = ((int) (MAX_TIME_OFFSET / HOP_RATE) + 1);

	// A sliding time window of indices into the channels[]
	int[] slidingWindow = new int[WINDOW_CHANNEL_COUNT];

	// Index to the sliding window of the current estimated channel
	int currentSlidingIndex = 0;

	// The index to the last update of the sliding window
	int lastWindowUpdate;

	private enum State {
		MasterNetworkRadio, SeekingRendezvous, OperatingNetwork, Syncing;

		// The count of how many hops have been made in the current sync attempt
		private long currentHop = 0;

		// Number of successful hop attempts
		private long hitCount = 0;

		// Number of required hops to be correct in order to synchronize
		private static final long REQUIRED_SYNC_HOPS = 10;

		// Maximum number of hop attempts to synchronize before aborting
		private static final long MAX_SYNC_HOPS = 20;

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
		timeOffset = (Math.abs(new Random().nextLong()) % MAX_TIME_OFFSET);

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
		if (!firstRadioSetup) {
			firstRadioSetup = true;
			this.state = State.MasterNetworkRadio;
			log.info("Radio " + id + " is the master network radio");
		} else {
			this.state = State.SeekingRendezvous;
		}
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
		int nextVal = Math.abs(ByteBuffer.wrap(bytes).getInt());
		return nextVal;
	}

	private void initializeSlidingWindow() {
		for (int i = 0; i < WINDOW_CHANNEL_COUNT; ++i) {
			slidingWindow[i] = generateSecureRandomInt() % channels.length;
		}
		lastWindowUpdate = WINDOW_CHANNEL_COUNT;
	}

	private void updateSlidingWindow() {
		//TODO: this the offset is being subtracted out, making radios start synchronized
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
		// Ignore any messages sent from this radio (every radio always hears its own broadcast)
		if (message.startsWith(id)) return;

		log.info("Message received: " + message);

		switch (state) {

			case MasterNetworkRadio:
				break;

			case OperatingNetwork:
				break;

			case SeekingRendezvous:
				if (message.contains("0HELLO")) {
					log.info("Radio " + id + " switching SYNCING state");
					state = State.Syncing;
					lastHopTime = getCurrentMillis() + 1;
					state.currentHop = 0;
					state.hitCount = 0;
					// currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: "
					// + currentChannel.toString());
				}
				break;

			case Syncing:
				if (message.contains("0HELLO")) {
					log.info("Syncing success, up to " + state.hitCount);
					state.hitCount += 1;
				}
				if (state.hitCount >= State.REQUIRED_SYNC_HOPS) {
					log.info("Radio " + id + " switching DONE state");
					state = State.OperatingNetwork;
				}
				break;

			default:
				throw new RuntimeException("Invalid state defined: " + state);
		}
	}

	@Override
	public void broadcastSync(Channel currentChannel) {
		switch (state) {

			case MasterNetworkRadio:
				currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel
						.toString());
				break;

			case OperatingNetwork:
				break;

			case SeekingRendezvous:
				// Don't broadcast anything, just listen for messages from the master network
				break;

			case Syncing:
				state.currentHop += 1;
				if (state.currentHop > State.MAX_SYNC_HOPS) {
					log.info("Radio " + id + " switching back to SEEKING state");
					state = State.SeekingRendezvous;
				}
				break;

			default:
				throw new RuntimeException("Undefined state: " + state + " received in radio: "
						+ id);
		}
	}

	@Override
	public boolean isSynced() {
		switch (state) {
			case MasterNetworkRadio:
			case OperatingNetwork:
				return true;

			case SeekingRendezvous:
			case Syncing:
				return false;
			default:
				throw new RuntimeException("Undefined state: " + state);
		}
	}

	private long getStateHopRate() {
		long freqHopRate;
		switch (state) {
			case MasterNetworkRadio:
			case Syncing:
			case OperatingNetwork:
				freqHopRate = HOP_RATE;
				break;
			case SeekingRendezvous:
				freqHopRate = (long) Math.ceil(SEARCH_SPEED * HOP_RATE);
				break;
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
				log.info("Sleeping for " + (currentTime - lastHopTime));
				Thread.sleep(freqHopRate - (currentTime - lastHopTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastHopTime = currentTime;
	}
}
