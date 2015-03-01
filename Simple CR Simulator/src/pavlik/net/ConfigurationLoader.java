package pavlik.net;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pavlik.net.Channel.Spectrum.Channel;
import pavlik.net.radio.Radio;
import pavlik.net.radio.RendezvousAlgorithm;

/**
 * XML DOM Parser class that converts XML configuration files into Java objects.
 * 
 * Reference code used:
 * http://docs.oracle.com/cd/B28359_01/appdev.111/b28394/adx_j_parser.htm#ADXDK3000
 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 * 
 * @author John
 *
 */
public class ConfigurationLoader {
	private static final Logger	log				= Logger.getLogger(ConfigurationLoader.class
														.getName());
	public static String		defaultConfig	= "DefaultConfiguration.xml";

	public static Simulation loadConfiguration() {
		log.fine("Loading configuration");
		File defaultConfig = new File("DefaultConfiguration.xml");
		Simulation simulation = new Simulation();
		Set<Radio> radios = loadRadiosConfiguration(defaultConfig, simulation);
		simulation.addRadios(radios);
		return simulation;
	}

	private static Set<Radio> loadRadiosConfiguration(File xmlFile, Simulation simulation) {
		log.fine("Loading radios");
		Set<Radio> radioSet = new HashSet<>();
		try {
			// DOM setup
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document root = dBuilder.parse(xmlFile);

			root.getDocumentElement().normalize();

			NodeList nodeList = root.getElementsByTagName("radio");

			for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
				Node node = nodeList.item(nodeIndex);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					String name = eElement.getAttribute("name");
					String channelString = eElement.getAttribute("channels");
					String rendezvousString = eElement.getAttribute("algorithm");
					Channel[] channels = simulation.getSpectrum().buildChannels(channelString);
					RendezvousAlgorithm algorithm = RendezvousAlgorithm.getAlgorithm(
							rendezvousString, channels);
					Radio radio = new Radio(name, algorithm);
					radioSet.add(radio);
				} else {
					throw new RuntimeException("Identified non-element node: " + node.getNodeName()
							+ " type: " + node.getNodeType());
				}
			}
			return radioSet;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			log.severe("Exception! " + e.toString());
			return null;
		}
	}
}
