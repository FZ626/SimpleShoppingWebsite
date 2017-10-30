package policeTrackerSimulation;

import static policeTrackerSimulation.Constant.DOWNTOWN;
import static policeTrackerSimulation.Constant.KENNEL;
import static policeTrackerSimulation.Constant.LAZYTOWN;
import static policeTrackerSimulation.Constant.MIDTOWN;
import static policeTrackerSimulation.Constant.POLICE_CSV_READ_PATH;
import static policeTrackerSimulation.Constant.POLICE_CSV_WRITE_PATH;
import static policeTrackerSimulation.Constant.STANDBY;
import static policeTrackerSimulation.Constant.SUSPECT_CSV_READ_PATH;
import static policeTrackerSimulation.Constant.SUSPECT_CSV_WRITE_PATH;
import static policeTrackerSimulation.Constant.UPTOWN;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PoliceTrackerApp {
	public static void main(final String... args) {
		final List<PoliceStation> policeStations = new LinkedList<>();
		// Get all the suspects from csv file
		final List<Suspect> suspects = readSuspectsFromCSV(SUSPECT_CSV_READ_PATH);

		// Set up kennel with dogs
		final int capacityOfKennel = (int) Math.ceil((double) suspects.size() / 2);
		final ArrayBlockingQueue<Dog> dogs = new ArrayBlockingQueue<Dog>(capacityOfKennel);
		for (int i = 0; i < capacityOfKennel; i++) {
			dogs.add(new Dog());
		}
		final Kennel kenel = new Kennel(KENNEL, capacityOfKennel, dogs);

		// Get all the polices from csv file and create a list of polices
		final List<Police> polices = readPolicesFromCSV(POLICE_CSV_READ_PATH, suspects, kenel, policeStations);

		// Get headers that are used for output file
		final String[] policeHeadersInCSV = getHeaderFromCSV(POLICE_CSV_READ_PATH);
		final String[] suspectHeadersInCSV = getHeaderFromCSV(SUSPECT_CSV_READ_PATH);

		final ExecutorService executorService = Executors.newCachedThreadPool();

		int currentCycle = 0;
		long lastCycleTime = 0;
		final int maxCycles = 3;

		System.out.println("Executing Simulation...");

		while (currentCycle < maxCycles) {
			// 10 is used for local test since it will be fast, change it to 1000 for assignment test requirement
			if (System.currentTimeMillis() - lastCycleTime >= 1000) {
				for (final Police police : polices) {
					executorService.execute(police);
				}

				currentCycle++;
				lastCycleTime = System.currentTimeMillis();
			}
		}

		executorService.shutdown();

		try {
			executorService.awaitTermination(30000, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Done");

		BufferedWriter bufferedWriterForPolice = null;
		BufferedWriter bufferedWriterForSuspect = null;
		try {
			final FileWriter policeFileWriter = new FileWriter(POLICE_CSV_WRITE_PATH);
			final FileWriter suspectFileWriter = new FileWriter(SUSPECT_CSV_WRITE_PATH);
			bufferedWriterForPolice = new BufferedWriter(policeFileWriter);
			bufferedWriterForSuspect = new BufferedWriter(suspectFileWriter);

			writeHeadersIntoCSV(policeHeadersInCSV, bufferedWriterForPolice);
			writeHeadersIntoCSV(suspectHeadersInCSV, bufferedWriterForSuspect);

			for (final Police police : polices) {
				bufferedWriterForPolice.write(police.getId());
				bufferedWriterForPolice.write(",");
				bufferedWriterForPolice.write(Integer.toString((int) police.getPoliceLocation().getX()));
				bufferedWriterForPolice.write(",");
				bufferedWriterForPolice.write(Integer.toString((int) police.getPoliceLocation().getY()));
				bufferedWriterForPolice.write(",");
				bufferedWriterForPolice.write(police.getStatus());
				bufferedWriterForPolice.write(",");
				bufferedWriterForPolice.write(police.hasDog() ? "Yes" : "No");
				bufferedWriterForPolice.write(",");
				bufferedWriterForPolice.write(police.getSuspectId());
				bufferedWriterForPolice.newLine();
			}

			for (final Suspect suspect : suspects) {
				bufferedWriterForSuspect.write(suspect.getSuspectId());
				bufferedWriterForSuspect.write(",");
				bufferedWriterForSuspect.write(Integer.toString((int) suspect.getSuspectLocation().getX()));
				bufferedWriterForSuspect.write(",");
				bufferedWriterForSuspect.write(Integer.toString((int) suspect.getSuspectLocation().getY()));
				bufferedWriterForSuspect.write(",");
				bufferedWriterForSuspect.write(suspect.getStatus());
				bufferedWriterForSuspect.write(",");
				bufferedWriterForSuspect.write(suspect.getPoliceId());
				bufferedWriterForSuspect.newLine();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedWriterForPolice != null) {
					bufferedWriterForPolice.flush();
					bufferedWriterForPolice.close();
				}
				if (bufferedWriterForSuspect != null) {
					bufferedWriterForSuspect.flush();
					bufferedWriterForSuspect.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void writeHeadersIntoCSV(final String[] policeHeaders, final BufferedWriter bufferedWriter) throws IOException {
		for (final String header : policeHeaders) {
			bufferedWriter.write(header);
			bufferedWriter.write(",");
		}
		bufferedWriter.newLine();
	}

	@SuppressWarnings("null")
	private static String[] getHeaderFromCSV(final String policeCsvFilePath) {

		BufferedReader br = null;
		String[] attributes = null;
		try {
			// skip the first line from the csv file since it is the header
			br = new BufferedReader(new FileReader(policeCsvFilePath));
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

	@SuppressWarnings("null")
	private static List<Suspect> readSuspectsFromCSV(final String fileName) {
		final List<Suspect> suspects = new ArrayList<>();
		BufferedReader br = null;
		try {
			// skip the first line from the csv file since it is the header
			br = new BufferedReader(new FileReader(fileName));
			br.readLine();
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
}
