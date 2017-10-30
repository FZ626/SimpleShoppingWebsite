package policeTrackerGUI;

import static policeTrackerSimulation.Constant.DOWNTOWN;
import static policeTrackerSimulation.Constant.KENNEL;
import static policeTrackerSimulation.Constant.LAZYTOWN;
import static policeTrackerSimulation.Constant.MIDTOWN;
import static policeTrackerSimulation.Constant.POLICE_CSV_READ_PATH;
import static policeTrackerSimulation.Constant.STANDBY;
import static policeTrackerSimulation.Constant.SUSPECT_CSV_READ_PATH;
import static policeTrackerSimulation.Constant.UPTOWN;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import policeTrackerSimulation.Dog;
import policeTrackerSimulation.Kennel;
import policeTrackerSimulation.Police;
import policeTrackerSimulation.PoliceStation;
import policeTrackerSimulation.Suspect;

public class PoliceTrackerHelper {
	final List<PoliceStation> policeStations = new LinkedList<>();

	// Get all the suspects from csv file
	final List<Suspect> suspects = readSuspectsFromCSV(SUSPECT_CSV_READ_PATH);

	final int capacityOfKennel = (int) Math.ceil((double) this.suspects.size() / 2);
	final ArrayBlockingQueue<Dog> dogs = creatSeatsForSuspects();
	final Kennel kenel = new Kennel(KENNEL, this.capacityOfKennel, this.dogs);

	final List<Police> polices = readPolicesFromCSV(POLICE_CSV_READ_PATH, this.suspects, this.kenel, this.policeStations);
	public PoliceTrackerHelper() {
	}

	public List<Police> getAllPolices() {
		return this.polices;
	}

	@SuppressWarnings("null")
	public String[] getHeaderFromCSV(final String csvFilePath) {
		BufferedReader br = null;
		String[] attributes = null;
		try {
			// skip the first line from the csv file since it is the header
			br = new BufferedReader(new FileReader(csvFilePath));
			attributes = br.readLine().split(",", -1);
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return attributes;
	}

	@SuppressWarnings("null")
	private static List<Suspect> readSuspectsFromCSV(final String fileName) {
		final List<Suspect> suspects = new ArrayList<>();
		BufferedReader br = null;
		// create an instance of BufferedReader
		try {
			// skip the first line from the csv file since it is the header
			br = new BufferedReader(new FileReader(fileName));
			br.readLine();

			// loop until all lines are read
			String line = null;

			while ((line = br.readLine()) != null) {
				// use string.split to load a string array with the values from each line of the file, using a comma as the delimiter
				final String[] attributes = line.split(",", -1);
				final Suspect suspect = createSuspect(attributes);
				// adding suspect into ArrayList
				suspects.add(suspect);
			}
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return suspects;
	}

	@SuppressWarnings("null")
	private static List<Police> readPolicesFromCSV(final String fileName, final List<Suspect> suspects, final Kennel kennel,
			final List<PoliceStation> policeStations) {
		final List<Police> polices = new ArrayList<>();
		BufferedReader br = null;

		try {
			final List<PoliceStation> policeStationList = getPoliceStations(fileName, policeStations);
			// skip the first line from the csv file since it is the header
			br = new BufferedReader(new FileReader(fileName));
			br.readLine();

			// loop until all lines are read
			String line = null;

			while ((line = br.readLine()) != null) {
				// use string.split to load a string array with the values from each line of the file, using a comma as the delimiter
				final String[] attributes = line.split(",", -1);
				final Police police = createPolice(attributes, suspects, policeStationList, kennel);
				// adding book into ArrayList
				polices.add(police);
			}
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return polices;
	}

	private static Police createPolice(final String[] metadata, final List<Suspect> suspects, final List<PoliceStation> policeStations,
			final Kennel kennel) {
		final String policeId = metadata[0];
		final int px = Integer.parseInt(metadata[1]);
		final int py = Integer.parseInt(metadata[2]);
		final Point orignalLocation = new Point(px, py);
		final Point policeLocation = new Point(px, py);
		// initial police status is standby
		final String status = STANDBY;
		final boolean hasDog = metadata[4].equals("No") ? false : true;
		final String suspectId = metadata[5];
		// create and return police of this metadata
		return new Police(policeId, orignalLocation, policeLocation, status, hasDog, suspectId, suspects, policeStations, kennel);
	}

	private static Suspect createSuspect(final String[] metadata) {
		final String id = metadata[0];
		final int px = Integer.parseInt(metadata[1]);
		final int py = Integer.parseInt(metadata[2]);
		final Point suspectLocation = new Point(px, py);
		final String status = metadata[3];
		final String policeId = metadata[4];
		// create and return suspect of this metadata
		return new Suspect(id, suspectLocation, status, policeId);
	}

	private static List<PoliceStation> getPoliceStations(final String fileName, final List<PoliceStation> policeStations)
			throws FileNotFoundException {
		final LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
		try {
			final int capacityOfPoliceStation = (int) Math.ceil((double) (lnr.lines().count() - 1) / 4);
			policeStations.add(new PoliceStation(DOWNTOWN, capacityOfPoliceStation));
			policeStations.add(new PoliceStation(UPTOWN, capacityOfPoliceStation));
			policeStations.add(new PoliceStation(MIDTOWN, capacityOfPoliceStation));
			policeStations.add(new PoliceStation(LAZYTOWN, capacityOfPoliceStation));
		} finally {
			try {
				lnr.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return policeStations;
	}

	private ArrayBlockingQueue<Dog> creatSeatsForSuspects() {
		final ArrayBlockingQueue<Dog> policeDogs = new ArrayBlockingQueue<>(this.capacityOfKennel);
		for (int i = 0; i < this.capacityOfKennel; i++) {
			policeDogs.add(new Dog());
		}
		return policeDogs;
	}
}

