package pavlik.net;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class GraphicalInterface extends JFrame {

	private static final Logger	log	= Logger.getLogger(GraphicalInterface.class.getName());

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException
				| UnsupportedLookAndFeelException e) {
			// Fail silently and let the application continue to load with the default look and feel
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> new GraphicalInterface());
	}

	Simulation	sim;

	public GraphicalInterface() {
		super();
		sim = ConfigurationLoader.loadConfiguration();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		/**
		 * Adding window listener to allow controlled shutdown of the swing application
		 */
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				sim.stopSimulation();
				dispose();
				System.exit(0);
			}
		});
		setSize(700, 500);

		setTitle("Cognitive Radio Simulator");

		setLayout(new BorderLayout());

		JTextArea textArea = new JTextArea();
		add(textArea, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
	}
}
