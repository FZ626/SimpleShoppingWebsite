package policeTrackerGUI;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class SimulationTablePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public SimulationTablePanel(final SimulationTableModel tableModel) {
		final JTable table = new JTable();

		table.setPreferredScrollableViewportSize(new Dimension(800, 200));
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(false);

		table.setModel(tableModel);

		final JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }
}

