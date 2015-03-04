package pavlik.net.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Spectrum {

	Map<Integer, Channel>		channelSet	= new HashMap<>();
	private static final Logger	log			= Logger.getLogger(Spectrum.class.getName());

	/**
	 * Build a set of channels and add them to the global list and return a set for local use
	 * 
	 * @param channelString
	 *            A string that must contain integers that are comma separated and dash separated,
	 *            e.g. "1-3,5".
	 */
	public Channel[] buildChannels(String channelString) {
		log.fine("Building channels");
		Set<Channel> channelSet = new HashSet<>();
		String[] channelCommaSplits = channelString.split(",");
		for (String commaChannel : channelCommaSplits) {
			String[] dashSplit = commaChannel.split("-");
			try {
				if (dashSplit.length == 1) {
					int num1 = Integer.parseInt(dashSplit[0].trim());
					channelSet.add(buildChannel(num1));
				} else if (dashSplit.length == 2) {
					int num1 = Integer.parseInt(dashSplit[0].trim());
					int num2 = Integer.parseInt(dashSplit[1].trim());
					/**
					 * Swap if necessary so that num1 <= num2 after this
					 */
					if (num1 > num2) {
						int tmpNum = num2;
						num2 = num1;
						num1 = tmpNum;
					}
					for (int channel = num1; channel <= num2; ++channel) {
						channelSet.add(buildChannel(channel));
					}
				} else {
					System.err.println("Unable to parse a dashSplit: ");
					for (String s : dashSplit) {
						System.err.println(s);
					}
				}
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
		}
		return channelSet.toArray(new Channel[0]);
	}

	private Channel buildChannel(int channelNum) {
		if (channelSet.containsKey(channelNum)) {
			return channelSet.get(channelNum);
		} else {
			Channel channel = new Channel(channelNum);
			channelSet.put(channelNum, channel);
			return channel;
		}
	}

	// /**
	// * Get a set of all channels in the spectrum
	// *
	// * @return a Set of Channel
	// */
	// public Set<Channel> getChannels() {
	// return channelSet;
	// }
}
