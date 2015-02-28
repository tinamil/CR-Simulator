package pavlik.net;

public class Main {

	public static void main(String[] args) {
		Simulation sim = ConfigurationLoader.loadConfiguration();
		sim.start();
	}

}
