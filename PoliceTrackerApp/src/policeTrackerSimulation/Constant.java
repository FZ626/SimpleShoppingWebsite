package policeTrackerSimulation;

import java.awt.Point;

public class Constant {
	public static final String STANDBY = "Standby";
	public static final String APPROACHING_KENNEL = "Approaching Kennel";
	public static final String AT_KENNEL = "At Kennel";
	public static final String APPROACHING_SUSPECT = "Approaching Suspect";
	public static final String AT_SCENE = "At Scene";
	public static final String RETURNING = "Returning";

	public static final String UNASSIGNED = "Unassigned";
	public static final String ASSIGNED = "Assigned";
	public static final String CAUGHT = "Caught";
	public static final String JAILED = "Jailed";

	public final static Point KENNEL = new Point(50, 50);
	public final static Point MIDTOWN = new Point(80, 30);
	public final static Point UPTOWN = new Point(10, 90);
	public final static Point DOWNTOWN = new Point(25, 5);
	public final static Point LAZYTOWN = new Point(70, 80);

	// Modify the path to fit your local environment
	public final static String POLICE_CSV_READ_PATH = "/Users/francisz/Documents/230Assignment2/PoliceTrackerApp/test/police.csv";
	public final static String SUSPECT_CSV_READ_PATH = "/Users/francisz/Documents/230Assignment2/PoliceTrackerApp/test/suspects.csv";
	public final static String POLICE_CSV_WRITE_PATH = "/Users/francisz/Documents/230Assignment2/PoliceTrackerApp/test/police-output.csv";
	public final static String SUSPECT_CSV_WRITE_PATH = "/Users/francisz/Documents/230Assignment2/PoliceTrackerApp/test/suspects-output.csv";
}

