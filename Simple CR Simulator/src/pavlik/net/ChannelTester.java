package pavlik.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import pavlik.net.Channel.Channel;
import pavlik.net.radio.Radio;
import pavlik.net.radio.algorithms.asynchronous.MultiHop;

public class ChannelTester {
	private static final Logger	log			= Logger.getLogger(ChannelTester.class.getName());

	public static final int		count		= 500;
	public static String		configFile	= "config/async/10";

	public static void main(String[] args) {

		log.fine("Begin Main");
		File dir = new File(configFile);
		File[] files = TextInterface.loadConfigFiles(dir, "FreqHopConfig.xml");
		for (File file : files) {
			double[] overlaps = new double[count];
			for (int j = 0; j < count; ++j) {
				Simulation sim = ConfigurationLoader.loadConfiguration(file, null);
				Iterator<Radio> radioIt = sim.allRadios.iterator();
				Radio radio1 = radioIt.next();
				Radio radio2 = radioIt.next();
				int count = 0;
				Channel[] channel1 = ((MultiHop) radio1.algorithm).channels;
				for (int i = 0; i < channel1.length; ++i) {
					if (channel1[i] == ((MultiHop) radio2.algorithm).channels[i]) {
						count += 1;
					}
				}
				double percentage = (new Double(count) / channel1.length);
				// log.severe(file.getPath() + " Overlap % = " + percentage);
				overlaps[j] = percentage;
			}
			complete(overlaps);
		}

	}

	public static void complete(double[] overlaps) {
		String filename = "output/correlationData.txt";
		File check = new File(filename);
		if (!check.exists()) try {
			check.getParentFile().mkdirs();
			check.createNewFile();
		} catch (IOException e1) {
			log.severe(e1.getMessage());
			e1.printStackTrace();
		}
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
			writer.write(Arrays.toString(overlaps));
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
