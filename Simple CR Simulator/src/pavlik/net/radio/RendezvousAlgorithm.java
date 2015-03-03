package pavlik.net.radio;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.algorithms.asynchronous.RandomAlgorithm;
import pavlik.net.radio.algorithms.synchronous.GeneratedOrthogonalSequenceAlgorithm;
import pavlik.net.radio.algorithms.synchronous.ModularClock;

public abstract class RendezvousAlgorithm {

	public abstract Channel nextChannel();

	public RendezvousAlgorithm(Channel[] channels) {

	}

	public static RendezvousAlgorithm getAlgorithm(String rendezvousString,
			Channel[] channels) {
		switch (rendezvousString) {
		case "random":
			return new RandomAlgorithm(channels);
		case "orthogonal":
			return new GeneratedOrthogonalSequenceAlgorithm(channels);
		case "modularclock":
			return new ModularClock(channels);
		default:
			return null;
		}
	}
}
