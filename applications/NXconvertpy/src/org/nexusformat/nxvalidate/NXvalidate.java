package org.nexusformat.nxvalidate;

import java.io.File;
import java.util.Vector;

public class NXvalidate {

	static final String VERSION = "0.1 alpha";

	private Vector<String> filenames;
	private String schematron;
	private boolean keepTemp;
	private boolean convertNxs;
	private int verbose;

	private Vector<Report> reports;

	NXvalidate() {
		this.filenames = new Vector<String>();
		this.schematron = new String("");
		this.keepTemp = false;
		this.convertNxs = true;
		this.verbose = 0;
		this.reports = new Vector<Report>();
	}

	public Vector<Report> getReports() {
		return reports;
	}

	public Vector<String> getFilenames() {
		return filenames;
	}

	public void setFilenames(Vector<String> filenames) {
		this.filenames = filenames;
	}

	public void setFilename(String filename) {
		this.filenames.add(filename);
	}

	public String getSchematron() {
		return schematron;
	}

	public void setSchematron(String schematron) {
		this.schematron = schematron;
	}

	public boolean isKeepTemp() {
		return keepTemp;
	}

	public void setKeepTemp(boolean keepTemp) {
		this.keepTemp = keepTemp;
	}

	public boolean isConvertNxs() {
		return convertNxs;
	}

	public void setConvertNxs(boolean convertNxs) {
		this.convertNxs = convertNxs;
	}

	public int getVerbose() {
		return verbose;
	}

	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}

	public static String getVersion() {
		return VERSION;
	}

	void parseArgs(final String[] args) {
		// check that the help and version arguments aren't specified
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-h") || args[i].equals("--help")) {
				this.printHelp(2);
				System.exit(0);
			}
			if (args[i].equals("--version")) {
				this.printVersion();
				System.exit(0);
			}
		}

		// go through the arguments for real
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("--verbose")) {
				this.verbose += 1;
			} else if (args[i].equals("-k") || args[i].equals("--keep")) {
				this.keepTemp = true;
			} else if (args[i].equals("-d") || args[i].equals("--dfn")) {
				this.schematron = args[i + 1];
				i++;
			} else if (args[i].equals("--noconvert")) {
				this.convertNxs = false;
			} else {
				this.filenames.add(args[i]);
			}
		}
		this.setLoggingLevel();

		// confirm that the manditory arguments are there
		if (this.filenames.size() <= 0) {
			System.out.println("Must specify at least one nexus file");
			this.printHelp(0);
			System.exit(-1);
		}
		if (this.schematron.length() <= 0) {
			System.out.println("Must specify a schematron file");
			this.printHelp(0);
			System.exit(-1);
		}
	}

	void setLoggingLevel() {
		if (this.verbose == 1)
			Logger.setLevel(Logger.INFO);
		else if (this.verbose == 2)
			Logger.setLevel(Logger.DEBUG);
		else if (this.verbose > 2)
			Logger.setLevel(Logger.TRACE);
	}

	void process() {
		if (this.verbose > 0) {
			System.out.println("Running NXvalidate (version:" + VERSION + ")");
		}
		int size = this.filenames.size();
		for (int i = 0; i < size; i++) {
			this.process(this.filenames.get(i));
		}
	}

	private static String toAbsFile(final String filename) {
		File file = new File(filename);
		return file.getAbsolutePath();
	}

	private void process(final String filename) throws Error {
		if (this.verbose > 0) {
			System.out.println("Validating " + filename);
		}

		// reduce the nexus file
		String reduced = "";
		if (this.convertNxs) {
			try {
				NXconvert converter = new NXconvert(filename, this.keepTemp);
			    reduced = converter.convert();
		    } catch (Exception e) {
				throw new Error("While converting \"" + filename
		            + "\" to reduced xml format", e);
			}
		} else {
			reduced = toAbsFile(filename);
		}

		// create the validation setup
		NXschematron schematron = new NXschematron(reduced,
				this.schematron, this.keepTemp);

		String result = "";
        try {
			result  = schematron.validate();
		} catch (Exception e) {
		    throw new Error("While creating validation report", e);
		}

		// create the report
		Report report = null;
		try {
		    report = new Report(reduced, result);
		} catch (Exception e) {
		    throw new Error("While generating the report object", e);
		}

		// Add to vector of reports (one for each input file)
		reports.add(report);
		System.out.println("===== Tree");
		report.printTree();
		int numErrors = report.numErrors();
		if (numErrors > 0) {
			System.out.println("===== Report");
			report.printReport();
		}
		System.out.println("===== Found " + report.numErrors() + " errors");

		System.out.println(result);
		/*} catch (Exception e) {
		    throw new Error("While processing " + filename, e);
		}*/
	}

	private void printVersion() {
		System.out.println("NXvalidate version " + VERSION);
	}

	private void printHelp(final int level) {
		System.out.println("usage: nxvalidate [options] <nxsfile>");
		if (level <= 0) {
			return;
		}

		System.out.println();
		System.out.println("Validate nexus files against the nexus definitions");
		this.printVersion();
		if (level <= 1) {
			return;
		}

		System.out.println();
		System.out.println("-h, --help    print this help information");
		System.out.println("-v, --verbose increase verbose printing");
		System.out.println("-d, --dfn     specify the definition file");
		System.out.println("-k, --keep    keep temporary files");
		System.out.println("--noconvert   do not reduce the nexus file");
	}

	public static void main(String[] args) {
		NXvalidate validate = new NXvalidate();
		validate.parseArgs(args);
		validate.process();
	}
}