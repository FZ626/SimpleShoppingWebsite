package policeTrackerGUI;

import java.util.List;

import javax.swing.SwingUtilities;

import policeTrackerSimulation.Police;

public class policeTrackerGUI {
	public static void main(final String[] args) {
		final List<Police> polices = new PoliceTrackerHelper().getAllPolices();
		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				new PoliceTrackerSimulationFrame("Police Simulation", polices);
			}
		});
	}

}
