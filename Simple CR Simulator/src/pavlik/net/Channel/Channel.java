package pavlik.net.Channel;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Channel implements Comparable<Channel> {
	private static final Logger	log	= Logger.getLogger(Channel.class.getName());
	int							id;
	Set<ChannelListener>		listeners;

	/**
	 * Set the constructor protected in order to force use of the Spectrum.buildChannels factory
	 * that allows string parsing
	 */
	Channel(int channel) {
		this.id = channel;
		listeners = new HashSet<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Channel) {
			return id == ((Channel) obj).id;
		}
		return super.equals(obj);
	}

	public synchronized boolean addListener(ChannelListener listener) {
		return listeners.add(listener);
	}

	public synchronized boolean removeListener(ChannelListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void broadcastMessage(String string) {
		for (ChannelListener listener : listeners) {
			log.fine("Broadcasting string: " + string + " on channel: " + id);
			listener.receiveBroadcast(this, string);
		}
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	public int compareTo(Channel o) {
		return Integer.compare(id, o.id);
	}
}
