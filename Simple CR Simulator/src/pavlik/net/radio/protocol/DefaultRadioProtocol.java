package pavlik.net.radio.protocol;

import java.util.logging.Logger;

import pavlik.net.Channel.Channel;

public class DefaultRadioProtocol implements RadioProtocol {
	private static final Logger	log		= Logger.getLogger(DefaultRadioProtocol.class.getName());
	String						id;
	volatile boolean			synced	= false;

	public DefaultRadioProtocol(String id) {
		this.id = id;
	}

	@Override
	public void receiveBroadcast(Channel currentChannel, String message) {
		if (message.startsWith(id)) return;
		log.info("Message received: " + message);
		if (message.contains("0HELLO")) {
			currentChannel.broadcastMessage(id + " 1" + "ACKHELLO on channel: "
					+ currentChannel.toString());
			pause();
		}
		if (message.contains("1ACKHELLO")) {
			currentChannel.broadcastMessage(id + " 2ACK");
			synced = true;
		}
		if (message.contains("2ACK")) {
			synced = true;
		}
	}

	// Wait for a response
	private void pause() {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void broadcastSync(Channel currentChannel) {
		currentChannel.broadcastMessage(id + " 0" + "HELLO on channel: "
				+ currentChannel.toString());
		pause();
	}


	public boolean isSynced() {
		return synced;
	}
}
