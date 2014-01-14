import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
//import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class Niah {

	/**
	 * This program carries out a simple k-anonymity disclosure risk test
	 * on individual level data.
	 * 
	 * @param args
	 * 
	 * @author Michael Comerford <michael@secretplan4b.co.uk>
	 * 
	 * Copyright (C) 2012 Michael Comerford
	 * 
	 * This File is part of Niah.
	 * 
	 * Niah is free software: you can redistribute it and/or modify it under the terms
	 * of the GNU General Public License as published by the Free Software Foundation,
	 * either version 3 of the License, or (at your option) any later version.
	 * 
	 * Niah is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
	 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
	 * PURPOSE. See the GNU General Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along with Niah.
	 * If not, see <http://www.gnu.org/licenses/>.
	 * 
	 * Michael Comerford
	 * National e-Science Centre
	 * Kelvin Building
	 * University of Glasgow
	 * University Avenue
	 * Glasgow
	 * G12 8QQ
	 * UNITED KINGDOM
	 * 
	 */

	// create key variable, threshold and filename values to be used later
	static long time = System.currentTimeMillis();
	static ArrayList<Integer> kv = new ArrayList<Integer>();
	static int threshold = 2;
	static File csv = null; // data in
	static File safe = null; // safe records out
	static File atrisk = null; // at risk records out
	static String output = null; // for user specified output file
	static int rN = 0; // total number of records
	static List<StataRow> source = null; // copy of data for sorting
	static ArrayList<Integer> uniques = null; // to hold id's of unique records
	static ArrayList<StataRow> atriskrecs = new ArrayList<StataRow>(); // to hold at risk records
	public static int k = 0; // to pass comparator column to StataRow
	static ArrayList<SRComparator> kvars = new ArrayList<SRComparator>();

	public static void main(String[] args) throws Exception {

		// declare usage statement string
		String usage = new String("\nUSAGE: Niah -kv \"VAR1,...,VARx\" [-th N] [-o OUTPUTNAME] FILE\n" +
				"\nOPTIONS: \n-kv \"VAR1,...,VARx\"	specify the column number of the key variables (comma seperated) \n" +
				"-th N			(optional) specify the threshold for k-anonymity (defaults to 2)\n" +
				"-o OUTPUTNAME		(optional) specify the output file names (without extension)\n" +
				"FILE			specify the filename for the input data (csv format required)\n");
		// obtain and translate command line args

		// check for command line args
		if (args.length <= 1) {
			System.out.println("No arguments provided, see usage statement.\n" + usage);
			System.exit(0);
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf("-") == 0) {
				// detect key variable option
				if (args[i].contains("th")){
					threshold = Integer.parseInt(args[i+1]);
				} else if (args[i].contains("o")) {
					output = args[i+1];
				}
				else if (!args[i].contains("kv")) {
					System.out.println("No key variables have been specified, see usage statement:" + usage);
					System.exit(0);
				}
				else {
					// add key variable column no's to kv
					String kvars = MyString.stripLeadingAndTrailingQuotes(args[i+1].toString());
					String[] ktokens = kvars.split(",");

					int kN = ktokens.length;
					for (int j = 0; j < kN; j++) {
						kv.add(Integer.parseInt(ktokens[j]));
					}
				}
			} else if (args[i].contains(".csv")) {
				csv = new File(args[i]);
				// name output file
				if (output != null) {
					safe = new File((output).concat("_safe.csv"));
					atrisk = new File((output).concat("_atrisk.csv"));
				} else {
					safe = new File(args[i].substring(0, args[i].indexOf(".")).concat("_safe.csv"));
					atrisk = new File(args[i].substring(0, args[i].indexOf(".")).concat("_atrisk.csv"));
				}
			}
		}
		if (csv == null) {
			System.out.println("No csv data file has been specified, see usage statement:" + usage);
			System.exit(0);
		}
		// output key variable indices
		System.out.println("The key variable indices are: " + kv.toString());
		// print threshold
		System.out.println("The threshold for k-anonymity test is set at: " + threshold);
		// output filenames
		System.out.println("Data in = " + csv.getName() + "\nsafe data = " + safe.getName() 
				+ "\nat risk data = " + atrisk.getName());

		// create initial data structure of data rows
		ArrayList<StataRow> datain = new ArrayList<StataRow>();
		ArrayList<String> headers = new ArrayList<String>();

		// create scanner and read in all data
		Scanner dcsv = null;
		try {
			dcsv = new Scanner(csv);
		} catch (FileNotFoundException e) {
			System.out.println("The CSV file was not found.");
			System.exit(0);
		}
		Integer dN = 0;
		Integer dlineNo = 0;
		while (dcsv.hasNextLine()) {
			String dline = new String(dcsv.nextLine());
			String [] dtokens = MyString.split(dline);
			dN = dtokens.length;

			if (dlineNo == 0) {
				for (int i = 0; i < dN; i++) {
					headers.add(new String(dtokens[i]));
				}
				dlineNo++;
				System.out.println();
				System.out.println();
			}

			else if (dlineNo > 0) {
				datain.add(new StataRow((dlineNo-1)));
				datain.get(dlineNo-1).setCapacity(headers.size()+1);
				// add lineNo for sorting output at index 0
				datain.get(dlineNo-1).add(new StataCell(dlineNo));
				for (int i = 0; i < headers.size(); i++) {
					try {
					datain.get(dlineNo-1).add(new StataCell(dtokens[i].getBytes()));
					} catch (IndexOutOfBoundsException e) {
						// deal with empty cells in csv by entering whitespace
						datain.get(dlineNo-1).add(new StataCell(new String(" ").getBytes()));
					}
				}
				dlineNo++;
			}
		}
		dcsv.close(); // close csv scanner

		// copy data into List for sorting and record retrieval 
		source = datain;
		rN = source.size(); // total number of records

		// create all the comparators needed for the key variables
		for (int i = 0; i < kv.size(); i++) {
			SRComparator c = new SRComparator(kv.get(i));
			kvars.add(c);
		}
		// test for minimal uniques and extract records by id
			// key vars are dealt with in reverse to obtain ordered tuples
		for (int i = (kvars.size()-1); i >= 0; i--) {
			while (true) {
				uniques = scanForUniques(source, kvars.get(i));
				if (uniques.size() == 0) {
					break;
				}
				// add those at risk records to atriskrecs ArrayList
				for (int x = 0; x < uniques.size(); x++) {
					for (int j = 0; j < rN; j++) {
						if (source.get(j).getid() == uniques.get(x)) {
							atriskrecs.add(source.get(j));
							source.remove(j);
							rN = source.size();
							break;
						}
					}
				}
			}
		}

		// create arraylist to store atrisk tuple indexes
		ArrayList<Integer> testunituples = null;

		// add all those that do not meet the threshold 
		testunituples = scanForTuples(source, kvars);
		for (int x = 0; x < testunituples.size(); x++) {
			for (int j = 0; j < rN; j++) {
				if (source.get(j).getid() == testunituples.get(x)) {
					atriskrecs.add(source.get(j));
					source.remove(j);
					rN = source.size();
				}
			}
		}

		// create safe and at risk output files
		// safe output
		
		//Sort output for comparison with original
		LNComparator l = new LNComparator(0);
		Collections.sort(source, l);
		PrintWriter q = new PrintWriter(new FileOutputStream(safe),
				true);
		int j;
		for (int i = 0; i < headers.size(); i++) {
			if (i != 0) 
				q.print(",");
			q.print(headers.get(i));
		}
		q.println();
		for (int i = 0; i < source.size(); i++) {
			for (j = 1; j < source.get(0).size(); j++) {
				if (j != 1)
					q.print(",");
				q.print(new String(source.get(i).get(j).getValue()));
			}
			q.println();
		}
		q.close();

		// at risk output
		//Sort output for comparison with original
				LNComparator ar = new LNComparator(0);
				Collections.sort(atriskrecs, ar);
				
		PrintWriter q2 = new PrintWriter(new FileOutputStream(atrisk),
				true);
		for (int i = 0; i < headers.size(); i++) {
			if (i != 0) 
				q2.print(",");
			q2.print(headers.get(i));
		}
		q2.println();
		for (int i = 0; i < atriskrecs.size(); i++) {
			for (j = 1; j < atriskrecs.get(0).size(); j++) {
				if (j != 1)
					q2.print(",");
				q2.print(new String(atriskrecs.get(i).get(j).getValue()));
			}
			q2.println();
		}
		q2.close();

		System.out.println();
		System.out.println("Output files created");
		System.out.println("The execution time : " + (System.currentTimeMillis()-time)/1000 +" Secs.");
	}
	/*
	 * This method finds all minimal uniques in a the dataset given key variables
	 */
	private static ArrayList<Integer> scanForUniques(List<StataRow> source,SRComparator Comp1) {
		ArrayList<Integer> uniques = new ArrayList<Integer>();
		Collections.sort(source, Comp1);
		int i = 0;
		while (i < rN) {
			int j; 
			int count = 1;
			for (j = i+1; j < rN; j++) {
				if (new String(source.get(i).get(Comp1.getIndex()).getValue()).compareTo(new String(source.get(j).get(Comp1.getIndex()).getValue())) == 0) {
					count++;
				}
				else
					break;
			}
			if (count < threshold) {
				uniques.add(source.get(i).getid());
				i = j;
			}
			else 
				i = j;
		}
		return uniques;
	}
	/*
	 * This method finds those tuples that do not meet the threshold
	 */
	private static ArrayList<Integer> scanForTuples(List<StataRow> source,ArrayList<SRComparator> comparators) throws FileNotFoundException {
		ArrayList<Integer> uniques = new ArrayList<Integer>();
		int i = 0; 
		int combinationtracker = 0; // tracks combinations of the same tuple
		while (i < rN) {
			int count = 0;
			// compare values for each key variable in each cell for each record and it's adjacent (i+1)
			for (int l = 0; l < kvars.size(); l++) {
				try {
					if (new String(source.get(i).get(kvars.get(l).getIndex()).getValue()).compareTo(new String(source.get(i+1).get(kvars.get(l).getIndex()).getValue())) == 0) {
						count++;
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("end of records");
					break;
				}
			}
			// if count is less than the number of key variables the tuples are different
			if (count < kvars.size()) {
				int reverse = 0;
				if (i !=0) { // test for first record
					for (int l = 0; l < kvars.size(); l++) {
						try{
							if (new String(source.get(i).get(kvars.get(l).getIndex()).getValue()).compareTo(new String(source.get(i-1).get(kvars.get(l).getIndex()).getValue())) == 0) {
								reverse++;
							}
						} catch (IndexOutOfBoundsException e) {
							System.out.println("this is the first sorted tuple.");
						}
					}
					// compare tuple with the previous record (i-1)
					if (reverse < kvars.size()) {
						uniques.add(source.get(i).getid());
						combinationtracker = 0;
						i++;
					} else {
						combinationtracker++;
						if (combinationtracker < threshold) { // check to see how many equal tuples have been before
							//if (combinationtracker != 1) { // 1 combination of a tuple needs different treatment.
							for (int g = i-(combinationtracker-1); g <= i; g++) {
								try {
									uniques.add(source.get(g).getid());
								} catch (IndexOutOfBoundsException e) {
									System.out.println("end of records");
									break;
								}
							}
							combinationtracker = 0;
						}
						i++;
					}
					combinationtracker = 0;
				} else {
					uniques.add(source.get(i).getid());
					combinationtracker = 0;
					i++;
				}

			} else {
				combinationtracker++;
				i++;
			}
		}
		return uniques;
	}
}
/*
 *  SRComparator class allows us to create all key variable comparators 
 *  
 */
class SRComparator implements Comparator<StataRow> {
	private int index;

	public SRComparator(int i){
		this.index = i;
	}
	public void setIndex(int i) {
		this.index = i;
	}
	public int getIndex() {
		return this.index;
	}
	public int compare(StataRow r1, StataRow r2) {
		return (new String(r1.get(index).getValue())).compareTo(new String(r2.get(index).getValue()));
	}
}
/*
 *  LNComparator class allows comparison of lineNo int values
 */
class LNComparator implements Comparator<StataRow> {
	private int index;
	
	public LNComparator(int i) {
		this.index = i;
	}
	@Override
	public int compare(StataRow r1, StataRow r2) {
		return (r1.get(index).getLineNo().compareTo(r2.get(index).getLineNo()));
	}
	
}