package pavlik.net.radio.algorithms.asynchronous;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
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

	private static final Logger log = Logger.getLogger(MultiHop.class.getName());

	// Whether to use a 1/X or uniform probability distribution
	private static final boolean USE_BIAS = false;

	// The radios clock will be set to between the current time and the current
	// time + MAX_ROUND_OFFSET
	private static final int MAX_ROUND_OFFSET = 500;

	// private long timeOffset;
	// private long startTime;
	private int currentHopRound;
	private int currentShortRound = 0;

	// Modifier applied to base HOP_RATE in order to search for the right
	// network during the Seeking phase
	public static final double SEARCH_SPEED = 2;

	// Uncomment to override and slow down to 1 second hops for debug
	// protected static long HOP_RATE = 1000;

	// Number of channels in the sliding window
	// private static int WINDOW_CHANNEL_COUNT = ((int) (MAX_TIME_OFFSET /
	// HOP_RATE) + 1);
	private static final int WINDOW_CHANNEL_COUNT = (2 * MAX_ROUND_OFFSET) + 1;

	// A sliding time window of indices into the channels[]
	int[] slidingWindow = new int[WINDOW_CHANNEL_COUNT];

	// Index to the sliding window of the current estimated channel
	int currentSlidingIndex;

	// The index to the last update of the sliding window
	int lastWindowUpdate;

	// RoundTypes are used to allow the joining radio hop at a different rate
	// than the frequency hop rate of the network. Due to the discrete and
	// serial
	// nature of this simulation they are necessary to track which types of
	// radios
	// are hopping on each iteration.
	RoundType currentRound = RoundType.bothRound;

	enum RoundType {
		shortRound, hopRound, bothRound;
	}

	public enum State {
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

	State state;
	public Channel[] channels;

	SecureRandom secureRand;
	int[] bias;

	public MultiHop(String id, Channel[] channels, State startingState, byte[] seed) {
		super(id);
		secureRand = new SecureRandom(seed);

		currentHopRound = Math.abs(new Random().nextInt()) % MAX_ROUND_OFFSET;
		if (startingState == State.SeekingRendezvous) {
			currentHopRound += MAX_ROUND_OFFSET;
		}
		currentSlidingIndex = currentHopRound;

		log.info("Starting Hop/Sliding Index: " + currentHopRound);
		// Build ranking table with Channels array
		this.channels = channels;
		Arrays.sort(channels, new Comparator<Channel>() {
			public int compare(Channel o1, Channel o2) {
				// return Double.compare(o1.noise, o2.noise);
				return o1.compareTo(o2);
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
		initializeSlidingWindow(startingState);

		this.state = startingState;
	}

	@SuppressWarnings("unused")
	private void incrementRound() {
		int previousShortRound = currentShortRound;
		int previousHopRound = currentHopRound;
		if (SEARCH_SPEED >= 1) {
			currentShortRound += 1;
			if (currentShortRound % SEARCH_SPEED == 0) {
				currentHopRound += 1;
			}
		} else /* SEARCH_SPEED < 1 */ {
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
				log.info(id + " Sliding Window: " + Arrays.toString(slidingWindow));
				log.info(id + " Sliding index = " + currentSlidingIndex);
			}
			break;
		case SeekingRendezvous:
			if (currentRound == RoundType.bothRound || currentRound == RoundType.shortRound) {
				// currentSlidingIndex = (currentSlidingIndex + 1) %
				// (WINDOW_CHANNEL_COUNT - 1);
				log.info(id + " Last window update: " + lastWindowUpdate);
				log.info(id + " Sliding Window: " + Arrays.toString(slidingWindow));
				log.info(id + " Sliding index = " + currentSlidingIndex);
			}
			break;
		default:
			throw new RuntimeException("Undefined state: " + state);
		}
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

	private void initializeSlidingWindow(State startingState) {
		int targetIndex;
		switch (startingState) {
		case MasterNetworkRadio:
			targetIndex = currentHopRound;
			break;
		case OperatingNetwork:
		case SeekingRendezvous:
		case Syncing:
		default:
			targetIndex = WINDOW_CHANNEL_COUNT;
		}
		for (int i = 0; i < targetIndex; ++i) {
			slidingWindow[i] = generateSecureRandomInt() % channels.length;
		}
		lastWindowUpdate = targetIndex;
	}

	private void updateSlidingWindow() {
		while (lastWindowUpdate <= currentHopRound) {
			slidingWindow[lastWindowUpdate % WINDOW_CHANNEL_COUNT] = generateSecureRandomInt() % channels.length;
			lastWindowUpdate++;
		}
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		// Ignore any messages sent from this radio (every radio always hears
		// its own broadcast)
		if (message.startsWith(id))
			return;

		log.info("Message received: " + message);

		switch (state) {

		case MasterNetworkRadio:
			break;

		case OperatingNetwork:
			break;

		case SeekingRendezvous:
			if (message.contains("0HELLO")) {
				log.info("Radio " + id + " switching SYNCING state");
				// currentHopRound = lastWindowUpdate;
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
			currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel.toString());
			break;

		case OperatingNetwork:
			break;

		case SeekingRendezvous:
			// Don't broadcast anything, just listen for messages from the
			// master network
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
			throw new RuntimeException("Undefined state: " + state + " received in radio: " + id);
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
