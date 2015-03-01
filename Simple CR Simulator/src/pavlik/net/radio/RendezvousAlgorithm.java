package pavlik.net.radio;

import pavlik.net.Channel.Spectrum.Channel;
import pavlik.net.radio.algorithms.Random;

public abstract class RendezvousAlgorithm {

	public abstract Channel nextChannel();

	protected RendezvousAlgorithm(Channel[] channels) {

	}

	public static RendezvousAlgorithm getAlgorithm(String rendezvousString, Channel[] channels) {
		switch (rendezvousString) {
			case "random":
				return new Random(channels);
			default:
				return null;
		}
	}
}
