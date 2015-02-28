package pavlik.net;
import java.util.Random;

import pavlik.net.Channel.Channel;

public class Radio {
	public Radio(String name, Channel[] channels) {
		this.id = name;
		this.observedChannels = channels;
		int start = new Random().nextInt(channels.length);
		this.currentChannel = observedChannels[start];
	}

	String		id;
	Channel		currentChannel;
	Channel[]	observedChannels;
}
