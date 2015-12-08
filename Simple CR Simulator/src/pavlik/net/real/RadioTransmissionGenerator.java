package pavlik.net.real;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exploratory class designed to test generating tones for a real radio to transmit
 * @author John
 *
 */
public class RadioTransmissionGenerator {

	private static final double SAMPLE_RATE = 1E6;
	private static final int SYMBOL_RATE = 10000;
	private static final int[] FREQUENCIES = { 5000, 6000, 7000, 8000, 9000, 10000 };
	private static final String NEWLINE = System.getProperty("line.separator");

	public static void main(String[] args) {
		StringBuilder output = new StringBuilder();
		int t = 0;
		for (int f = 0; f < FREQUENCIES.length; ++f) {
			int freq = FREQUENCIES[f];
			for (; t < (f+1) * SYMBOL_RATE; ++t) {
				Double val = 2 * Math.PI * freq * t / SAMPLE_RATE;
				output.append(Math.cos(val));
				output.append(",");
				output.append(Math.sin(val));
				output.append("\n");
			}
		}
		File outputFile = new File("output.txt");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			writer.write(output.toString() + NEWLINE);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
