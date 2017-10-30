package policeTrackerGUI;

import java.awt.Point;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import policeTrackerSimulation.Police;

public class SimulationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final String[] columnNames = { "ID", "Location", "Status", "Police Dog", "Suspect" };
	private final List<Police> polices;

	public SimulationTableModel(final List<Police> polices) {
		this.polices = polices;
	}

	@Override
	public int getRowCount() {
		return this.polices.size();
	}

	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Police police = this.polices.get(rowIndex);
		switch (columnIndex) {
			case 0:
				return police.getId();
			case 1:
				final Point policeLocation = police.getPoliceLocation();
				final int px = (int) policeLocation.getX();
				final int py = (int) policeLocation.getY();
				return String.format("(%d,%d)", px, py);
			case 2:
				return police.getStatus();
			case 3:
				return police.hasDog() ? "Yes" : "No";
			case 4:
				return police.getSuspectId().isEmpty() ? "-" : police.getSuspectId();
		}
		return null;
	}

	@Override
	public String getColumnName(final int col) {
		return this.columnNames[col];
	}
}

