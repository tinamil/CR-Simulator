package pavlik.net;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import pavlik.net.Simulation.SimListener;

public class TextInterface {
	private static final Logger	log				= Logger.getLogger(TextInterface.class.getName());
	private static int			totalRunCount	= 350000;
	static int					totalChannels	= 1000;
	private static String[]		configFiles		= { totalChannels + "DRSEQConfig.xml",
			totalChannels + "EJSConfig.xml", totalChannels + "JSConfig.xml",
			totalChannels + "MCConfig.xml", totalChannels + "MMCConfig.xml",
			totalChannels + "RandomConfig.xml", totalChannels + "SSBConfig.xml" };
	private static int			index			= 0;

	public static void main(String[] args) throws IOException {
		log.fine("Begin Main");
		executeSim();
	}

	private static void executeSim() {
		index %= configFiles.length;
		Simulation sim = ConfigurationLoader.loadConfiguration(configFiles[index]);
		index += 1;
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
			try (final BufferedWriter writer = new BufferedWriter(new FileWriter("output"
					+ totalChannels + "/" + sim.getRendezvousString() + ".txt", true))) {
				writer.write(Long.toString(sim.getTimeSpent()) + "\t"
						+ Long.toString(sim.getRounds()));
				writer.newLine();
				writer.flush();
				if (--totalRunCount > 0) {
					executeSim();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
