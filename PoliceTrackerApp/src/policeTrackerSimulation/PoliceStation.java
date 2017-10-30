package policeTrackerSimulation;

import java.awt.Point;
import java.util.concurrent.ArrayBlockingQueue;

public class PoliceStation {

	private Point policeStationLocation;
	private int policeStationCap;
	private final ArrayBlockingQueue<Seat> seats;

	public PoliceStation(final Point policeStationLocation, final int policeStationCap) {
		this.policeStationCap = policeStationCap;
		this.policeStationLocation = policeStationLocation;
		this.seats = new ArrayBlockingQueue<Seat>(policeStationCap);
	}

	public ArrayBlockingQueue<Seat> getSeats() {
		return this.seats;
	}

	public Point getPoliceStationLocation() {
		return this.policeStationLocation;
	}

	public void setPoliceStationLocation(final Point policeStationLocation) {
		this.policeStationLocation = policeStationLocation;
	}

	public int getPoliceStationCap() {
		return this.policeStationCap;
	}

	public void setPoliceStationCap(final int policeStationCap) {
		this.policeStationCap = policeStationCap;
	}

	public synchronized void produce(final Seat seat) {
		try {
			this.seats.put(seat);
			System.out.println("Put one suspect into jail, " + (this.policeStationCap - this.seats.size()) + " seats left for suspects at:"
					+ this.policeStationLocation);
		} catch (final InterruptedException e) {
			System.err.println("Wait was interrupted");
		}
	}
}

