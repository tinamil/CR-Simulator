package pavlik.net;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import pavlik.net.Channel.Spectrum;
import pavlik.net.radio.Radio;

public class Simulation extends Thread {
	private static final Logger	log					= Logger.getLogger(Simulation.class.getName());
	public static final int		SYNC				= 0;
	public static final int		ASYNC				= 1;

	public int					timingType;
	private Set<Radio>			allRadios			= new HashSet<>();
	private volatile boolean	running				= true;
	private Set<SimListener>	simList				= new HashSet<>();
	private String				rendezvousString	= "";
	private long				clock				= 0;
	private long				rounds				= 0;
	private Spectrum			spectrum			= new Spectrum();

	public void addRadios(Set<Radio> radios) {
		allRadios.addAll(radios);
	}

	@Override
	public void run() {
		log.info("Begin simulation");
		long start = System.nanoTime();
		if (timingType == ASYNC) {
			for (Radio radio : allRadios) {
				radio.start();
			}
		}
		while (running) {
			try {
				if (timingType == ASYNC) Thread.sleep(100);
				boolean done = true;
				rounds += 1;
				for (Radio radio : allRadios) {
					if (timingType == SYNC) {
						radio.nextStep();
					}
					if (!radio.isSyncComplete()) done = false;
				}
				running = !done;
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.severe("Exception: " + e.toString());
			}
		}
		clock = System.nanoTime() - start;
		log.info("End simulation");
		log.info("Time spent: " + clock);
		complete(clock);
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
		this.timingType = model;
	}

	public String getRendezvousString() {
		return rendezvousString;
	}

	public long getTimeSpent() {
		return clock;
	}

	public long getRounds() {
		return rounds;
	}

	public Spectrum getSpectrum() {
		return spectrum;
	}

	public void setRendezvousString(String rendezvousString) {
		this.rendezvousString = rendezvousString;
	}

	public void setTiming(String timingString) {
		switch (timingString) {
			case "asynchronous":
			case "async":
				setTiming(Simulation.ASYNC);
				break;
			case "slotted":
			case "sync":
			case "synchronous":
				setTiming(Simulation.SYNC);
				break;
			default:
				setTiming(Simulation.SYNC);
		}
	}
}
