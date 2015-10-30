package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.algorithms.asynchronous.EnhancedJumpStay;
import pavlik.net.radio.algorithms.asynchronous.MultiHop;
import pavlik.net.radio.algorithms.asynchronous.JumpStay;
import pavlik.net.radio.algorithms.asynchronous.ModifiedModularClock;
import pavlik.net.radio.algorithms.asynchronous.RandomAlgorithm;
import pavlik.net.radio.algorithms.asynchronous.ShortSequenceBased;
import pavlik.net.radio.algorithms.synchronous.DRSEQ;
import pavlik.net.radio.algorithms.synchronous.GeneratedOrthogonalSequence;
import pavlik.net.radio.algorithms.synchronous.ModularClock;
import pavlik.net.radio.protocol.RadioProtocol;

public abstract class RendezvousAlgorithm implements RadioProtocol {

	private static final Logger	log			= Logger.getLogger(RendezvousAlgorithm.class.getName());
	// protected long lastHopTime = System.currentTimeMillis();
	// /**
	// * 9 ms / 111 hz by default, can be overriden by a specific implementation
	// */
	// protected static long HOP_RATE = 9;
	boolean						synced		= false;
	protected String			id;

	public abstract Channel nextChannel();

	public RendezvousAlgorithm(String id) {
		this.id = id;
	}

	public static RendezvousAlgorithm getAlgorithm(String rendezvousString, String id,
			Channel[] channels, boolean firstRadio) {
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
				return new MultiHop(id, channels, firstRadio ? MultiHop.State.MasterNetworkRadio : MultiHop.State.SeekingRendezvous);
			default:
				return null;
		}
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: " + currentChannel
					.toString());
		}
		if (message.contains("1ACKHELLO")) {
			currentChannel.broadcastMessage(id + " 2ACK");
			synced = true;
		}
		if (message.contains("2ACK")) {
			synced = true;
		}
	}

	@Override
	public void broadcastSync(Channel currentChannel) {
		currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel
				.toString());
	}

	public boolean isSynced() {
		return synced;
	}

	// public void pauseForHop() {
	// long currentTime = System.currentTimeMillis();
	// while (currentTime - lastHopTime < HOP_RATE) {
	// try {
	// Thread.sleep(currentTime - lastHopTime);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// lastHopTime = currentTime;
	// }
}
