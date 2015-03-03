package pavlik.net;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import pavlik.net.Channel.Spectrum;
import pavlik.net.radio.Radio;

public class Simulation extends Thread {
	private static final Logger	log				= Logger.getLogger(Simulation.class.getName());
	public static final int		SYNC			= 0;
	public static final int		ASYNC			= 1;

	public int					timing;
	private Set<Radio>			allRadios		= new HashSet<>();
	private volatile boolean	running			= true;
	private Set<SimListener>	simList			= new HashSet<>();
	private String				rendezvousString	= "";
	public long					timeSpent		= 0;
	private Spectrum			spectrum		= new Spectrum();

	public void addRadios(Set<Radio> radios) {
		allRadios.addAll(radios);
	}

	@Override
	public void run() {
		log.info("Begin simulation");
		long start = System.nanoTime();
		if (timing == ASYNC) {
			for (Radio radio : allRadios) {
				radio.start();
			}
		}
		while (running) {
			try {
				Thread.sleep(100);
				boolean done = true;
				for (Radio radio : allRadios) {
					if (timing == SYNC) {
						radio.nextStep();
					}
					if (radio.running) done = false;
				}
				running = !done;
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.severe("Exception: " + e.toString());
			}
		}
		timeSpent = System.nanoTime() - start;
		log.info("End simulation");
		log.info("Time spent: " + timeSpent);
		complete(timeSpent);
	}

	public void stopSimulation() {
		running = false;
		for (Radio radio : allRadios) {
			radio.stopSimulation();
		}
	}

	private void complete(long timeSpent) {
		for (SimListener simListener : simList) {
			simListener.complete(timeSpent);
		}
	}

	public boolean addListener(SimListener listener) {
		return simList.add(listener);
	}

	public boolean removeListener(SimListener listener) {
		return simList.remove(listener);
	}

	interface SimListener {
		public void complete(long timeSpent);
	}

	public void setTiming(int model) {
		this.timing = model;
	}

	public String getRendezvousString() {
		return rendezvousString;
	}

	public long getTimeSpent() {
		return timeSpent;
	}

	public Spectrum getSpectrum() {
		return spectrum;
	}

	public void setRendezvousString(String rendezvousString) {
		this.rendezvousString = rendezvousString;
	}
}
