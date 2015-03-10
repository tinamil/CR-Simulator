package pavlik.net.radio.algorithms.synchronous;

import java.util.Arrays;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

/**
 * Only guaranteed under synchronous model
 * @author jpavlik
 *
 */
public class GeneratedOrthogonalSequence extends RendezvousAlgorithm {

	Channel[] channels;
	java.util.Random rnd = new java.util.Random();
	int index;

	public GeneratedOrthogonalSequence(Channel[] channels) {
		super(channels);
		Arrays.sort(channels);
		this.channels = buildSequence(channels);
		this.index = rnd.nextInt(channels.length);
	}

	/**
	 * http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
	 * Durstenfield shuffle shuffle
	 * 
	 * @param channels2
	 * @return
	 */
//	private Channel[] permutation(Channel[] inputChannels) {
//		for (int i = inputChannels.length - 1; i > 0; i--) {
//			int index = rnd.nextInt(i + 1);
//			// Simple swap
//			Channel a = inputChannels[index];
//			inputChannels[index] = inputChannels[i];
//			inputChannels[i] = a;
//		}
//		return inputChannels;
//	}

	@Override
	public Channel nextChannel() {
		return channels[index++ % channels.length];
	}

	public Channel[] buildSequence(Channel[] observedChannels) {
		int length = observedChannels.length;
		Channel[] channelSequence = new Channel[length * length + length];
		int seqIndex = 0;
		for (int i = 0; i < length; ++i) {
			channelSequence[seqIndex++] = observedChannels[i];
			for (int j = 0; j < length; ++j) {
				channelSequence[seqIndex++] = observedChannels[j];
			}
		}
		return channelSequence;
	}

}
