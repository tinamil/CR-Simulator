package pavlik.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import pavlik.net.Simulation.SimListener;

public class TextInterface {
	private static final Logger log = Logger.getLogger(TextInterface.class.getName());
	private static final int totalRunCount = 1000;

	private static final String channels = null;
	// private static final String timing = null;
	private static final String configDirectory = "config/asyncMHtest";

	public static void main(String[] args) throws IOException {
		log.fine("Begin Main");
		File dir = new File(configDirectory);
		File[] files = loadConfigFiles(dir);
		for (File file : files) {
			executeSim(file, 1);
		}
	}

	public static File[] loadConfigFiles(File directory) {
		return loadConfigFiles(directory, ".xml");
	}

	public static File[] loadConfigFiles(File directory, String acceptString) {
		if (!directory.isDirectory()) {
			return new File[] { directory };
		}
		File[] configFiles = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (!pathname.isDirectory() && pathname.getName().endsWith(acceptString));
			}
		});
		List<File> allConfigFiles = new ArrayList<>(Arrays.asList(configFiles));
		File[] subFolders = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File subdir : subFolders) {
			File[] subConfigs = loadConfigFiles(subdir, acceptString);
			allConfigFiles.addAll(Arrays.asList(subConfigs));
		}
		return allConfigFiles.toArray(new File[0]);
	}

	private static void executeSim(File configFile, int runs) {
		for (int i = 0; i < totalRunCount; ++i) {
			Simulation sim = ConfigurationLoader.loadConfiguration(configFile, channels);
			sim.addListener(new TextInterface().new TextListener(sim, configFile, runs));
			sim.run();
		}
	}

	private class TextListener implements SimListener {
		Simulation sim;
		//final int runs;
		final File configFile;

		public TextListener(Simulation sim, File configFile, int runs) {
			this.sim = sim;
			this.configFile = configFile;
			//this.runs = runs;
		}

		@Override
		public void complete(long timeSpent) {
			String filename = "output/" + configFile.getPath() + "/" + sim.getRendezvousString() + ".txt";
			File check = new File(filename);
			if (!check.exists())
				try {
					check.getParentFile().mkdirs();
					check.createNewFile();
				} catch (IOException e1) {
					log.severe(e1.getMessage());
					e1.printStackTrace();
				}
			try (final BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
				writer.write(Long.toString(sim.getRounds()));
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
