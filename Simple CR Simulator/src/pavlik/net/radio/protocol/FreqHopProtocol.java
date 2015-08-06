package pavlik.net.radio.protocol;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;

public class FreqHopProtocol implements RadioProtocol {
	
	private static final Logger	log					= Logger.getLogger(FreqHopProtocol.class
			.getName());
	
	private static final long	REQUIRED_SYNC_HOPS	= 10;
	String						id;
	volatile boolean			synced				= false;
	long						hitCount			= 0;

	public FreqHopProtocol(String id) {
		this.id = id;
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			hitCount += 1;
			currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: " + currentChannel
					.toString());
		}
		if (message.contains("1ACKHELLO")) {
			hitCount += 1;
		}

		if (hitCount >= REQUIRED_SYNC_HOPS) synced = true;
	}

	@Override
	public void broadcastSync(Channel currentChannel) {
		currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: " + currentChannel
				.toString());
	}

	public boolean isSynced() {
		return synced;
	}

}
