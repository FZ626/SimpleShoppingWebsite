package policeTrackerSimulation;

import static policeTrackerSimulation.Constant.APPROACHING_KENNEL;
import static policeTrackerSimulation.Constant.APPROACHING_SUSPECT;
import static policeTrackerSimulation.Constant.ASSIGNED;
import static policeTrackerSimulation.Constant.AT_KENNEL;
import static policeTrackerSimulation.Constant.AT_SCENE;
import static policeTrackerSimulation.Constant.CAUGHT;
import static policeTrackerSimulation.Constant.JAILED;
import static policeTrackerSimulation.Constant.KENNEL;
import static policeTrackerSimulation.Constant.RETURNING;
import static policeTrackerSimulation.Constant.STANDBY;
import static policeTrackerSimulation.Constant.UNASSIGNED;

import java.awt.Point;
import java.util.List;

public class Police implements Runnable {
	private String id;
	private String suspectId;
	private Point policeLocation;
	private Point originalLocation;
	private String status;
	private boolean hasDog;
	private final List<Suspect> suspects;
	private Suspect assignedSuspect;
	private List<String> suspectIds;
	private final List<PoliceStation> policeStations;
	private final Kennel kennel;
	private int times = 0;

	public Police(final String id, final Point originalLocation, final Point policeLocation, final String status, final boolean hasDog,
			final String suspectId, final List<Suspect> suspects, final List<PoliceStation> policeStations, final Kennel kennel) {
		this.id = id;
		this.policeLocation = policeLocation;
		this.originalLocation = originalLocation;
		this.status = status;
		this.hasDog = hasDog;
		this.suspectId = suspectId;
		this.suspects = suspects;
		this.policeStations = policeStations;
		this.kennel = kennel;
	}

	public String getId() {
		return this.id;
	}
	public void setId(final String id) {
		this.id = id;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(final String status) {
		this.status = status;
	}
	public String getSuspectId() {
		return this.suspectId;
	}
	public void setSuspectId(final String suspectId) {
		this.suspectId = suspectId;
	}

	public boolean hasDog() {
		return this.hasDog;
	}

	public void setDog(final boolean hasDog) {
		this.hasDog = hasDog;
	}

	public Point getPoliceLocation() {
		return this.policeLocation;
	}

	public void setPoliceLocation(final Point policeLocation) {
		this.policeLocation = policeLocation;
	}

	public List<String> getSuspectIds() {
		return this.suspectIds;
	}

	public void setSuspectIds(final List<String> suspectIds) {
		this.suspectIds = suspectIds;
	}

	public List<PoliceStation> getPoliceStations() {
		return this.policeStations;
	}

	public Kennel getKennel() {
		return this.kennel;
	}

	public Suspect getAssignedSuspect() {
		return this.assignedSuspect;
	}

	public void setAssignedSuspect(final Suspect assignedSuspect) {
		this.assignedSuspect = assignedSuspect;
	}

	@Override
	public String toString() {
		return "Police(" + this.id + ") at position(" + this.policeLocation + "), status=" + this.status
				+ ", hasDog=" + this.hasDog + ", suspectId=" + this.suspectId + "]";
	}

	@Override
	public void run() {
		synchronized (this.suspects) {
			if (this.suspects.size() > 0 && this.assignedSuspect == null) {
				this.assignedSuspect = getClosestSuspect(this.originalLocation, this.suspects);
			}
			switch (this.status) {
				case STANDBY:
					if (this.assignedSuspect != null) {
						this.suspectId = this.assignedSuspect.getSuspectId();
						this.status = APPROACHING_KENNEL;
						this.suspects.get(this.suspects.indexOf(this.assignedSuspect)).setStatus(ASSIGNED);

						System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
								+ this.assignedSuspect.getStatus() + " with dog" + this.hasDog + ") Status:" + this.status + " At"
								+ this.policeLocation.toString() + " Suspect location:");
					}
					break;
				case APPROACHING_KENNEL:
					moveToKennel();
					System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
							+ this.assignedSuspect.getStatus() + " with dog" + this.hasDog + ") Status:" + this.status + " At"
							+ this.policeLocation.toString() + " Suspect location:");
					break;
				case AT_KENNEL:
					if (!this.hasDog) {
						final Dog dog = this.kennel.consume();
						if (dog != null) {
							this.hasDog = true;
							this.status = APPROACHING_SUSPECT;
							break;
						}
					} else {
						this.kennel.produce(new Dog());
						this.hasDog = false;
						this.status = RETURNING;
					}
					System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
							+ this.assignedSuspect.getStatus() + " with dog" + this.hasDog + ") Status:" + this.status + " At"
							+ this.policeLocation.toString() + " Suspect location:");
					break;
				case APPROACHING_SUSPECT:
					moveToSuspect(this.assignedSuspect);
					System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
							+ this.assignedSuspect.getStatus() + " with dog" + this.hasDog + ") Status:" + this.status + " At"
							+ this.policeLocation.toString() + " Suspect location:");
					break;
				case AT_SCENE:
					waitForPeriod();

					System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
							+ this.assignedSuspect.getStatus() + " with dog" + this.hasDog + ") Status:" + this.status + " At"
							+ this.policeLocation.toString() + " Suspect location:");
					break;
				case RETURNING:

					final PoliceStation availablePoliceStation = getClosestAvailabeStation(KENNEL, this.policeStations);
					if (availablePoliceStation != null) {
						moveToPoliceStation(this.assignedSuspect, availablePoliceStation);
						System.out
								.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " with dog: " + this.hasDog + ") Status:"
										+ this.status + " At"
										+ this.policeLocation.toString() + " Police location:"
										+ availablePoliceStation.getPoliceStationLocation());
					}
					break;
			}
		}
	}

	@SuppressWarnings("hiding")
	private PoliceStation getClosestAvailabeStation(final Point kennel, final List<PoliceStation> policeStations) {
		PoliceStation closestAvailableStation = null;
		double cloestDis = Double.POSITIVE_INFINITY;

		for (final PoliceStation policeStation : policeStations) {
			if (policeStation.getSeats().size() < policeStation.getPoliceStationCap()) {
				final double dis = kennel.distance(policeStation.getPoliceStationLocation());
				if (dis < cloestDis) {
					cloestDis = dis;
					closestAvailableStation = policeStation;
				}
			}
		}
		return closestAvailableStation;
	}

	private void waitForPeriod() {
		if (this.times > 3) {
			this.status = APPROACHING_KENNEL;
			moveToKennel();
		}
		this.times++;
	}

	private void moveToSuspect(final Suspect closestSuspect) {
		if (moveToPoint(closestSuspect.getSuspectLocation(), 4)) {
			this.status = AT_SCENE;
			this.suspects.get(this.suspects.indexOf(closestSuspect)).setStatus(CAUGHT);
		}
	}

	private void moveToKennel() {
		if (moveToPoint(KENNEL, 3)) {
			this.status = AT_KENNEL;
		}
	}

	private void moveToPoliceStation(final Suspect closeSuspect, final PoliceStation closestPoliceStation) {
		if (moveToPoint(closestPoliceStation.getPoliceStationLocation(), 3)) {
			this.status = STANDBY;
			this.suspectId = "";
			this.suspects.get(this.suspects.indexOf(closeSuspect)).setStatus(JAILED);
			this.suspects.get(this.suspects.indexOf(closeSuspect)).setPoliceId("");
			this.suspects.get(this.suspects.indexOf(closeSuspect)).setSuspectLocation(closestPoliceStation.getPoliceStationLocation());
			this.policeStations.get(this.policeStations.indexOf(closestPoliceStation)).produce(new Seat());
			System.out.println("Police(id=" + this.id + " Suspect: " + this.suspectId + " status:"
					+ this.assignedSuspect.getStatus() + " with dog: " + this.hasDog + ") Status:" + this.status + " At "
					+ this.policeLocation.toString() + " Suspect location:");
			this.assignedSuspect = null;
			this.originalLocation = closestPoliceStation.getPoliceStationLocation();

		}
	}

	private boolean moveToPoint(final Point targetLocation, final int moves) {
		int counter = moves;
		while (counter > 0) {
			if (this.policeLocation.x < targetLocation.x) {
				this.policeLocation.translate(1, 0);
			} else if (this.policeLocation.x > targetLocation.x) {
				this.policeLocation.translate(-1, 0);
			} else if (this.policeLocation.y < targetLocation.y) {
				this.policeLocation.translate(0, 1);
			} else if (this.policeLocation.y > targetLocation.y) {
				this.policeLocation.translate(0, -1);
			}
			if (this.policeLocation.distance(targetLocation) == 0) {
				return true;
			}

			if (this.assignedSuspect.getStatus().equalsIgnoreCase(CAUGHT)) {
				this.assignedSuspect.setSuspectLocation(this.policeLocation);
			}

			counter--;
		}

		if (this.assignedSuspect.getStatus().equalsIgnoreCase(CAUGHT)) {
			this.assignedSuspect.setSuspectLocation(this.policeLocation);
		}

		return false;
	}

	private Suspect getClosestSuspect(final Point currentLocation, final List<Suspect> suspectLocations) {
		Suspect closestSuspect = null;
		double cloestDis = Double.POSITIVE_INFINITY;

		for (final Suspect suspect : suspectLocations) {
			if (suspect.getPoliceId().isEmpty() && suspect.getStatus().equalsIgnoreCase(UNASSIGNED)) {
				final double dis = currentLocation.distance(suspect.getSuspectLocation());
				if (dis < cloestDis) {
					cloestDis = dis;
					closestSuspect = suspect;
				}
			}
		}

		if (closestSuspect != null && closestSuspect.getPoliceId().isEmpty()) {
			this.suspects.get(this.suspects.indexOf(closestSuspect)).setPoliceId(this.id);
		}

		return closestSuspect;
	}
}
