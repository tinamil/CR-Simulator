package pavlik.net;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import pavlik.net.Channel.Spectrum;
import pavlik.net.radio.Radio;

public class Simulation extends Thread {
	private static final Logger	log			= Logger.getLogger(Simulation.class.getName());
	long						currentTime	= 0;
	final Spectrum				spectrum	= new Spectrum();
	Set<Radio>					allRadios	= new HashSet<Radio>();
	Set<Spectrum.Channel>		allChannels	= spectrum.getChannels();
	private volatile boolean	running		= true;

	public void addRadios(Set<Radio> radios) {
		allRadios.addAll(radios);
	}

	@Override
	public void run() {
		log.info("Begin simulation");
		while (running) {
			for (Radio radio : allRadios) {
				radio.start();
			}
		}
		log.info("End simulation");
	}

	public Spectrum getSpectrum() {
		return spectrum;
	}

	public void stopSimulation() {
		running = false;
	}
}
