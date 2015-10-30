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
 *   while(isSynced() == false){
 * 	 nextChannel();
 * 	 broadcastSync();
 * 	 pauseForHop();
 *   }
 * </pre>
 * 
 * @author John
 *
 */
public class MultiHop extends RendezvousAlgorithm {

	private static final Logger		log						= Logger.getLogger(MultiHop.class
																	.getName());

	// Seed will be generated the first time this class is instantiated
	private static byte[]			SEED					= null;
	private static final int		SEED_SIZE				= 512;

	// Whether to use a 1/X or uniform probability distribution
	private static final boolean	USE_BIAS				= false;

	// The radios clock will be set to between the current time and the current time +
	// MAX_ROUND_OFFSET
	private static int				MAX_ROUND_OFFSET		= 5;
	// private long timeOffset;
	// private long startTime;
	private int						currentHopRound;
	private int						currentShortRound		= 0;

	// Modifier applied to base HOP_RATE in order to search for the right network during the Seeking
	// phase
	public static double			SEARCH_SPEED			= 2;

	// Uncomment to override and slow down to 1 second hops for debug
	// protected static long HOP_RATE = 1000;

	// Number of channels in the sliding window
	// private static int WINDOW_CHANNEL_COUNT = ((int) (MAX_TIME_OFFSET / HOP_RATE) + 1);
	private static int				WINDOW_CHANNEL_COUNT	= (2 * MAX_ROUND_OFFSET) + 1;

	// A sliding time window of indices into the channels[]
	int[]							slidingWindow			= new int[WINDOW_CHANNEL_COUNT];

	// Index to the sliding window of the current estimated channel
	int								currentSlidingIndex;

	// The index to the last update of the sliding window
	int								lastWindowUpdate;

	RoundType						currentRound			= RoundType.bothRound;

	enum RoundType {
		shortRound, hopRound, bothRound;
	}

	public enum State {
		MasterNetworkRadio, SeekingRendezvous, OperatingNetwork, Syncing;

		// The count of how many hops have been made in the current sync attempt
		private long				currentHop			= 0;

		// Number of successful hop attempts
		private long				hitCount			= 0;

		// Number of required hops to be correct in order to synchronize
		private static final long	REQUIRED_SYNC_HOPS	= 10;

		// Maximum number of hop attempts to synchronize before aborting
		private static final long	MAX_SYNC_HOPS		= 20;

	}

	State						state;
	public Channel[]			channels;

	java.security.SecureRandom	secureRand;
	int[]						bias;

	public MultiHop(String id, Channel[] channels, State startingState) {
		super(id);
		// If this is the first radio in the network, generate a shared seed for ALL radios
		if (MultiHop.SEED == null) try {
			MultiHop.SEED = java.security.SecureRandom.getInstanceStrong().generateSeed(
					SEED_SIZE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Initialize radios with a cryptographically secure PRNG and shared SEED
		secureRand = new java.security.SecureRandom(MultiHop.SEED);

		// Setup a random time offset that is plus or minus the MAX_TIME_OFFSET
		// timeOffset = (Math.abs(new Random().nextLong()) % MAX_TIME_OFFSET);
		// startTime = getCurrentMillis();
		currentHopRound = Math.abs(new Random().nextInt()) % MAX_ROUND_OFFSET;
		currentSlidingIndex = currentHopRound;
		log.info("Starting Hop/Sliding Index: " + currentHopRound);
		// Build ranking table with Channels array
		this.channels = channels;
		Arrays.sort(channels, new Comparator<Channel>() {
			public int compare(Channel o1, Channel o2) {
				return Double.compare(o1.noise, o2.noise);
				// return o1.compareTo(o2);
			};
		});
		int biasCount = (channels.length * (channels.length + 1)) / 2;
		bias = new int[biasCount];
		int index = 0;
		for (int i = channels.length; i > 0; --i) {
			for (int j = 0; j < i; ++j) {
				bias[index++] = channels.length - i;
			}
		}
		// Pre-load the sliding window with valid channels
		initializeSlidingWindow();

		this.state = startingState;
	}

	private void incrementRound() {
		int previousShortRound = currentShortRound;
		int previousHopRound = currentHopRound;
		if (SEARCH_SPEED >= 1) {
			currentShortRound += 1;
			if (currentShortRound % SEARCH_SPEED == 0) {
				currentHopRound += 1;
			}
		} else /* SEARCH_SPEED < 1 */{
			currentHopRound += 1;
			if (currentHopRound % (1 / SEARCH_SPEED) == 0) {
				currentShortRound += 1;
			}
		}
		if (currentShortRound == previousShortRound) {
			currentRound = RoundType.hopRound;
		} else if (currentHopRound == previousHopRound) {
			currentRound = RoundType.shortRound;
		} else {
			currentRound = RoundType.bothRound;
		}
	}

	@Override
	public Channel nextChannel() {
		incrementRound();
		updateSlidingWindow();
		switch (state) {
			case MasterNetworkRadio:
			case OperatingNetwork:
			case Syncing:
				if (currentRound == RoundType.bothRound || currentRound == RoundType.hopRound) {
					currentSlidingIndex = (currentSlidingIndex + 1) % WINDOW_CHANNEL_COUNT;
				}
				break;
			case SeekingRendezvous:
				if (currentRound == RoundType.bothRound || currentRound == RoundType.shortRound) {
					currentSlidingIndex = (currentSlidingIndex + 1) % WINDOW_CHANNEL_COUNT;
				}
				break;
			default:
				throw new RuntimeException("Undefined state: " + state);
		}
		log.info(id + " Sliding Window: " + Arrays.toString(slidingWindow));
		log.info(id + " Channels = " + Arrays.toString(channels));
		log.info(id + " Sliding index = " + currentSlidingIndex);
		return channels[slidingWindow[currentSlidingIndex]];
	}

	private int generateSecureRandomInt() {
		byte[] bytes = new byte[4];
		secureRand.nextBytes(bytes);
		int nextVal = Math.abs(ByteBuffer.wrap(bytes).getInt());
		if (USE_BIAS) {
			return bias[nextVal % bias.length];
		} else {
			return nextVal;
		}
	}

	private void initializeSlidingWindow() {
		for (int i = 0; i < WINDOW_CHANNEL_COUNT; ++i) {
			slidingWindow[i] = generateSecureRandomInt() % channels.length;
		}
		lastWindowUpdate = WINDOW_CHANNEL_COUNT;
	}

	private void updateSlidingWindow() {
		while (lastWindowUpdate <= currentHopRound) {
			slidingWindow[lastWindowUpdate % WINDOW_CHANNEL_COUNT] = generateSecureRandomInt()
					% channels.length;
			lastWindowUpdate++;
		}
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
					//currentHopRound = lastWindowUpdate;
					state = State.Syncing;
					state.currentHop = 0;
					state.hitCount = 0;
				}
				break;

			case Syncing:
				if (currentRound == RoundType.bothRound || currentRound == RoundType.hopRound) {
					if (message.contains("0HELLO")) {
						log.info("Syncing success, up to " + state.hitCount);
						state.hitCount += 1;
					}
					if (state.hitCount >= State.REQUIRED_SYNC_HOPS) {
						log.info("Radio " + id + " switching DONE state");
						state = State.OperatingNetwork;
					}
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
				currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: "
						+ currentChannel.toString());
				break;

			case OperatingNetwork:
				break;

			case SeekingRendezvous:
				// Don't broadcast anything, just listen for messages from the master network
				break;

			case Syncing:
				if (currentRound == RoundType.bothRound || currentRound == RoundType.hopRound) {
					state.currentHop += 1;
					if (state.currentHop > State.MAX_SYNC_HOPS) {
						log.info("Radio " + id + " switching back to SEEKING state");
						state = State.SeekingRendezvous;
					}
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

}
