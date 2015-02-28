package pavlik.net.Channel;

public class Channel {
	int	id;

	Channel(int channel) {
		this.id = channel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Channel) {
			return id == ((Channel) obj).id;
		}
		return super.equals(obj);
	}
}