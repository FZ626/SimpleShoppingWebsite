package policeTrackerSimulation;

import java.awt.Point;

public class Suspect {
	private String id;
	private Point suspectLocation;
	private String status;
	private String policeId;

	public Suspect(final String id, final Point suspectLocation, final String status, final String policeId) {
		this.id = id;
		this.suspectLocation = suspectLocation;
		this.status = status;
		this.policeId = policeId;
	}

	public String getSuspectId() {
		return this.id;
	}

	public void setSuspectId(final String id) {
		this.id = id;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getPoliceId() {
		return this.policeId;
	}

	public void setPoliceId(final String policeId) {
		this.policeId = policeId;
	}

	@Override
	public String toString() {
		return "Suspect(id=" + this.id + "), location:" + this.suspectLocation + ", Status: " + this.status + ", policeId=" + this.policeId;
	}

	public Point getSuspectLocation() {
		return this.suspectLocation;
	}

	public void setSuspectLocation(final Point suspectLocation) {
		this.suspectLocation = suspectLocation;
	}

}
