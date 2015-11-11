package pavlik.net.real;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RadioTransmissionGenerator {

	private static final int SAMPLE_RATE = 10000;
	private static final int[] FREQUENCIES = { 500, 600, 700, 800, 900, 1000 };
	private static final String NEWLINE = System.getProperty("line.separator");

	public static void main(String[] args) {
		StringBuilder output = new StringBuilder();
		for (int freq : FREQUENCIES) {
			for (int t = 0; t < SAMPLE_RATE; ++t) {
				Double val = 2 * Math.PI * freq * t / 1E6;
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
