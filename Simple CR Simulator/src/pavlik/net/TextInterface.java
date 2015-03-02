package pavlik.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class TextInterface {
	private static final Logger	log	= Logger.getLogger(TextInterface.class.getName());

	public static void main(String[] args) {
		log.fine("Begin Main");
		Simulation sim = ConfigurationLoader.loadConfiguration();
		sim.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Type 'quit' to stop the simulation");
			String input;
			do
				input = reader.readLine();
			while (!input.equalsIgnoreCase("quit"));
			sim.stopSimulation();
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Exception: " + e.toString());
		}
	}

}
