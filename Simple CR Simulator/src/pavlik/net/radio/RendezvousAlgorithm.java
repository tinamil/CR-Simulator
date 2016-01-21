package pavlik.net.radio;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.protocol.RadioProtocol;

public abstract class RendezvousAlgorithm implements RadioProtocol {

	private static final Logger log = Logger.getLogger(RendezvousAlgorithm.class.getName());
	// protected long lastHopTime = System.currentTimeMillis();
	// /**
	// * 9 ms / 111 hz by default, can be overriden by a specific implementation
	// */
	// protected static long HOP_RATE = 9;
	boolean synced = false;
	protected String id;

	public abstract Channel nextChannel();

	public RendezvousAlgorithm(String id) {
		this.id = id;
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		if (message.startsWith(id))
			return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: " + currentChannel.toString());
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
		currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel.toString());
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
