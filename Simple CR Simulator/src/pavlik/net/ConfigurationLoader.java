package pavlik.net;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pavlik.net.Channel.Spectrum;

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

	public static String	defaultConfiguration	= "DefaultConfiguration.xml";

	public static Simulation loadConfiguration() {
		File defaultConfig = new File("DefaultConfiguration.xml");
		Simulation simulation = new Simulation();
		Set<Radio> radios = loadRadiosConfiguration(defaultConfig);
		simulation.addRadios(radios);
		return simulation;
	}

	private static Set<Radio> loadRadiosConfiguration(File xmlFile) {
		Set<Radio> radioSet = new HashSet<>();
		try {
			// DOM setup
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document root = dBuilder.parse(xmlFile);

			// Normalizing DOM to convert:
			// Element foo
			// Text node: ""
			// Text node: "Hello "
			// Text node: "wor"
			// Text node: "ld"
			// to Element foo
			// Text node: "Hello world"
			root.getDocumentElement().normalize();

			NodeList nodeList = root.getElementsByTagName("radio");

			for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
				Node node = nodeList.item(nodeIndex);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					String name = eElement.getAttribute("name");
					String channelString = eElement.getAttribute("channels");
					Radio radio = new Radio(name, Spectrum.buildChannels(channelString));
					radioSet.add(radio);
				} else {
					throw new RuntimeException("Identified non-element node: " + node.getNodeName()
							+ " type: " + node.getNodeType());
				}
			}

			return radioSet;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
