import java.util.ArrayList;
import java.util.Comparator;


public class StataRow implements Comparable<StataRow> {
	
	public static Comparator<StataRow> compare;
	/**
	 * StataRows produces the rows for our csv data structure
	 * 
	 * @author Michael Comerford michael@secretplan4b.co.uk
	 *	
	 *
	 * @param <T> generic type of StataRow 
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

		private Integer id;
		private ArrayList<StataCell> values;
		private Double unique;
		public enum Types {S,I,D,E};
		private Types type;
		public int record;
		
		public StataRow(Integer myid) {
			id = myid;
			values = new ArrayList<StataCell>();
		}
		
		public StataRow() {
			this(new Integer(-9));
		}
		
		// get row id method
		public Integer getid() {
			return id;
		}
		
		// get row size method
		public int size() {
			return values.size();
		}
		
		// add values method for rows
		public void add(StataCell c) {
			values.add(c);
			
		}
		
		public void setCapacity(Integer cap){
			values.ensureCapacity(cap);
		}
		
		// get method for row values
		public StataCell get(int i) {
			return values.get(i);
		}
		// get method for values arraylist
		public ArrayList<StataCell> getValues() {
			return values;
		}
		
		// creates an array of values used in risk tests
		public Object[] toArray() {
			return values.toArray();
		}
		
		// set and get methods for uniqueness test results 
		public void setUnique(double unique) {
			this.unique = unique;
		}
		
		public double getUnique() {
			return unique;
		}
		
		// set method for row values used after SDC
		public StataCell set(int i, StataCell c) {
			return values.set(i, c);
		}

		// set and get for row types using enums
		public void setType(Types type) {
			this.type = type;
		}
		public Types getType() {
			return type;
		}
		public int compare(ArrayList<String> r1, ArrayList<String> r2) {
			int k = Niah.k;
			return r1.get(k).compareTo(r2.get(k));
		}

		@Override
		public int compareTo(StataRow arg0) {
			return 0;
		}

}
class StataCell implements Comparable<StataCell> {
	
	private byte[] value;
	public enum Types {S,I,D,E};
	private Types t;
	private Integer lineNo;
	
	public StataCell(byte[] s) {
		this.value = s;
	}
	public StataCell(int i) {
		this.lineNo = i;
	}
	
	public int compareTo(StataCell stataCell) {
		return this.value.toString().compareTo(stataCell.getValue().toString());
	}
	public void setValue(byte[] s) {
		this.value = s;
	}
	public void setType(Types t) {
		this.t = t;
	}
	
	public Types getType() {
		return t;
	}
	public byte[] getValue() {
		return value;
	}
	public Integer getLineNo() {
		return lineNo;
	}
}