package pavlik.net;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import pavlik.net.Simulation.SimListener;

public class TextInterface {
	private static final Logger	log				= Logger.getLogger(TextInterface.class.getName());
	private static int			totalRunCount	= 10;

	public static void main(String[] args) throws IOException {
		log.fine("Begin Main");
		executeSim();
	}

	private static void executeSim() {
		Simulation sim = ConfigurationLoader.loadConfiguration();
		sim.addListener(new TextInterface().new TextListener(sim));
		sim.start();
	}

	private class TextListener implements SimListener {
		Simulation	sim;

		public TextListener(Simulation sim) {
			this.sim = sim;
		}

		@Override
		public void complete(long timeSpent) {
			try (final BufferedWriter writer = new BufferedWriter(new FileWriter(sim
					.getRendezvousString()
					+ ".txt", true))) {
				writer.write(Long.toString(sim.getTimeSpent()));
				writer.newLine();
				writer.flush();
				if (totalRunCount-- > 0) {
					executeSim();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
