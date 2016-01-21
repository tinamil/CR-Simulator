package pavlik.net.radio;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.algorithms.asynchronous.EnhancedJumpStay;
import pavlik.net.radio.algorithms.asynchronous.JumpStay;
import pavlik.net.radio.algorithms.asynchronous.ModifiedModularClock;
import pavlik.net.radio.algorithms.asynchronous.MultiHop;
import pavlik.net.radio.algorithms.asynchronous.RandomAlgorithm;
import pavlik.net.radio.algorithms.asynchronous.ShortSequenceBased;
import pavlik.net.radio.algorithms.synchronous.DRSEQ;
import pavlik.net.radio.algorithms.synchronous.GeneratedOrthogonalSequence;
import pavlik.net.radio.algorithms.synchronous.ModularClock;

public class AlgorithmFactory {
	private static byte[] seed;
	private static final int SEED_SIZE = 512;
 
	public AlgorithmFactory() {
		try {
			seed = SecureRandom.getInstanceStrong().generateSeed(SEED_SIZE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public RendezvousAlgorithm getAlgorithm(String rendezvousString, String id, Channel[] channels,
			boolean firstRadio) {
		switch (rendezvousString) {
		case "random":
			return new RandomAlgorithm(id, channels);
		case "orthogonal":
			return new GeneratedOrthogonalSequence(id, channels);
		case "mc":
			return new ModularClock(id, channels);
		case "mmc":
			return new ModifiedModularClock(id, channels);
		case "jumpstay":
		case "js":
			return new JumpStay(id, channels);
		case "enhancedjumpstay":
		case "ejs":
			return new EnhancedJumpStay(id, channels);
		case "drseq":
			return new DRSEQ(id, channels);
		case "ssb":
			return new ShortSequenceBased(id, channels);
		case "fh":
			return new MultiHop(id, channels,
					firstRadio ? MultiHop.State.MasterNetworkRadio : MultiHop.State.SeekingRendezvous, seed);
		default:
			return null;
		}
	}
}
