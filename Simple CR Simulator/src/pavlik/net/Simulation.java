package pavlik.net;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import pavlik.net.Channel.Spectrum;
import pavlik.net.radio.Radio;

public class Simulation extends Thread {
	private static final Logger log = Logger.getLogger(Simulation.class
			.getName());
	long currentTime = 0;
	final Spectrum spectrum = new Spectrum();
	Set<Radio> allRadios = new HashSet<>();
	private volatile boolean running = true;
	Set<SimListener> simList = new HashSet<>();

	public void addRadios(Set<Radio> radios) {
		allRadios.addAll(radios);
	}

	@Override
	public void run() {
		log.info("Begin simulation");
		long start = System.nanoTime();
		for (Radio radio : allRadios) {
			radio.start();
		}
		while (running) {
			try {
				Thread.sleep(100);
				boolean done = true;
				for (Radio radio : allRadios) {
					if (radio.running)
						done = false;
				}
				running = !done;
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.severe("Exception: " + e.toString());
			}
		}
		long end = System.nanoTime();
		log.info("End simulation");
		log.info("Time spent: " + (end - start) / 1000000);
		complete();
	}

	public Spectrum getSpectrum() {
		return spectrum;
	}

	public void stopSimulation() {
		running = false;
		for (Radio radio : allRadios) {
			radio.stopSimulation();
		}
	}

	private void complete() {
		for (SimListener simListener : simList) {
			simListener.complete();
		}
	}

	public boolean addListener(SimListener listener) {
		return simList.add(listener);
	}

	public boolean removeListener(SimListener listener) {
		return simList.remove(listener);
	}

	interface SimListener {
		public void complete();
	}
}
