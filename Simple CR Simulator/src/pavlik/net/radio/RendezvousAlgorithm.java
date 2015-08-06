package pavlik.net.radio;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.algorithms.asynchronous.EnhancedJumpStay;
import pavlik.net.radio.algorithms.asynchronous.FrequencyHopping;
import pavlik.net.radio.algorithms.asynchronous.JumpStay;
import pavlik.net.radio.algorithms.asynchronous.ModifiedModularClock;
import pavlik.net.radio.algorithms.asynchronous.RandomAlgorithm;
import pavlik.net.radio.algorithms.asynchronous.ShortSequenceBased;
import pavlik.net.radio.algorithms.synchronous.DRSEQ;
import pavlik.net.radio.algorithms.synchronous.GeneratedOrthogonalSequence;
import pavlik.net.radio.algorithms.synchronous.ModularClock;
import pavlik.net.radio.protocol.DefaultRadioProtocol;
import pavlik.net.radio.protocol.RadioProtocol;

public abstract class RendezvousAlgorithm {

	volatile long	lastHopTime	= 0;
	long			HOP_RATE	= 9;	// ms

	public abstract Channel nextChannel();

	public RendezvousAlgorithm(Channel[] channels) {

	}

	public static RendezvousAlgorithm getAlgorithm(String rendezvousString, Channel[] channels) {
		switch (rendezvousString) {
			case "random":
				return new RandomAlgorithm(channels);
			case "orthogonal":
				return new GeneratedOrthogonalSequence(channels);
			case "mc":
				return new ModularClock(channels);
			case "mmc":
				return new ModifiedModularClock(channels);
			case "jumpstay":
			case "js":
				return new JumpStay(channels);
			case "enhancedjumpstay":
			case "ejs":
				return new EnhancedJumpStay(channels);
			case "drseq":
				return new DRSEQ(channels);
			case "ssb":
				return new ShortSequenceBased(channels);
			case "fh":
				return new FrequencyHopping(channels);
			default:
				return null;
		}
	}

	public RadioProtocol getProtocol(String id) {
		return new DefaultRadioProtocol(id);
	}

	public void pauseForHop() {
		long currentTime = System.currentTimeMillis();
		while (currentTime - lastHopTime < HOP_RATE) {
			try {
				Thread.sleep(currentTime - lastHopTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastHopTime = currentTime;
	}
}
