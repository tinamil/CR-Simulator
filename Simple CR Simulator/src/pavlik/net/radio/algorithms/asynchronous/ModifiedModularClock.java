package pavlik.net.radio.algorithms.asynchronous;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.RendezvousAlgorithm;

public class ModifiedModularClock extends RendezvousAlgorithm {

	Channel[]			channels;
	java.util.Random	rand	= new java.util.Random();

	protected ModifiedModularClock(Channel[] channels) {
		super(channels);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Channel nextChannel() {
		// TODO Auto-generated method stub
		return null;
	}

}
