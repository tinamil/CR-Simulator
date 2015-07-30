package pavlik.net.Channel;

public interface ChannelListener {
	void receiveBroadcast(Channel channel, String message);
}
