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

import pavlik.net.Channel.Channel;
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

	public static Simulation loadConfiguration(File configFile, String channelsOverride,
			String timingOverride) {
		log.fine("Loading configuration");
		File config;
		if (configFile == null) config = new File(defaultConfig);
		else config = configFile;
		Document document = readXML(config);
		if (document == null) return null;

		Simulation simulation = loadNetworkConfiguration(document);
		if (timingOverride != null) {
			simulation.setTiming(timingOverride);
		}

		loadRadiosConfiguration(document, simulation, channelsOverride);
		return simulation;
	}

	private static Document readXML(File xmlFile) {
		try { // DOM setup
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document document = dBuilder.parse(xmlFile);

			document.getDocumentElement().normalize();
			return document;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			log.severe("Exception! " + e.toString());
			return null;
		}
	}

	private static Simulation loadNetworkConfiguration(Document doc) {
		Simulation sim = new Simulation();
		NodeList networkList = doc.getElementsByTagName("network");
		Node root = networkList.item(0);
		if (root.getNodeType() == Node.ELEMENT_NODE) {
			Element rootElement = (Element) root;
			String timingString = rootElement.getAttribute("timing");
			sim.setTiming(timingString);
			String rendezvousString = rootElement.getAttribute("algorithm");
			sim.setRendezvousString(rendezvousString);
		} else {
			throw new RuntimeException("Root was not an element");
		}
		return sim;
	}

	private static void loadRadiosConfiguration(Document doc, Simulation simulation,
			String channelOverride) {
		log.fine("Loading radios");
		Set<Radio> radioSet = new HashSet<>();
		NodeList nodeList = doc.getElementsByTagName("radio");

		for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
			Node node = nodeList.item(nodeIndex);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				String name = eElement.getAttribute("name");
				String channelString = eElement.getAttribute("channels");
				if (channelOverride != null) channelString = channelOverride;
				Channel[] channels = simulation.getSpectrum().buildChannels(channelString);
				RendezvousAlgorithm algorithm = RendezvousAlgorithm.getAlgorithm(simulation
						.getRendezvousString(), name, channels);
				Radio radio = new Radio(name, algorithm);
				radioSet.add(radio);
			} else {
				throw new RuntimeException("Identified non-element node: " + node.getNodeName()
						+ " type: " + node.getNodeType());
			}
		}
		simulation.addRadios(radioSet);
	}
}
