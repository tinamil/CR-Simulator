package pavlik.net.radio.protocol;

import pavlik.net.Channel.ChannelListener;
import pavlik.net.Channel.Channel;

public interface RadioProtocol extends ChannelListener {

	public Channel nextChannel();
	
//	public void pauseForHop();
	
	public void receiveBroadcast(Channel channel, String message);

	public void broadcastSync(Channel channel);

	public boolean isSynced();
}