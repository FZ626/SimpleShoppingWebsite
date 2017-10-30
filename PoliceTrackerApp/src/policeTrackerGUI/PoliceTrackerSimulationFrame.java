package policeTrackerGUI;

import java.awt.FlowLayout;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import policeTrackerSimulation.Police;

public class PoliceTrackerSimulationFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private List<Police> polices = null;
	private SimulationTableModel tableModel = null;
	private SimulationTablePanel tablePanel = null;

	public PoliceTrackerSimulationFrame(final String title, final List<Police> polices) {
		super(title);
		setLayout(new FlowLayout());
		setSize(1000, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.polices = polices;
		this.tableModel = new SimulationTableModel(polices);
		this.tablePanel = new SimulationTablePanel(this.tableModel);
		getContentPane().add(this.tablePanel);
		setVisible(true);
		start();
	}

	private void start() {
		final SwingWorker<Void, SimulationTableModel> worker = new SwingWorker<Void, SimulationTableModel>() {
			@Override
			protected Void doInBackground() throws Exception {
				final ExecutorService executorService = Executors.newCachedThreadPool();

				int count = 0;
				final int maxCycles = 60;
				long lastCycle = 0;

				while (count < maxCycles) {
					if (System.currentTimeMillis() - lastCycle >= 1000) {
						for (final Police police : PoliceTrackerSimulationFrame.this.polices) {
							executorService.execute(police);
						}

						lastCycle = System.currentTimeMillis();
						count++;
						publish(PoliceTrackerSimulationFrame.this.tableModel);
					 }
				 }

				executorService.shutdown();
				return null;
			}

			@Override
			protected void process(final List<SimulationTableModel> tableModels) {
				for (final SimulationTableModel model : tableModels) {
					model.fireTableDataChanged();
				}
			}
		};

		worker.execute();

	}
}