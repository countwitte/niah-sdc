import java.util.ArrayList;

public class MyString {
	/**
	 * creates MyString to manipulate strings from csv files.
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
		private String s;

		public MyString(String s) {
			this.s = s;
		}

		public String toString() {
			return s;
		}
		
		// strip quotes from variable headers to use in finding SDC files
		static String stripLeadingAndTrailingQuotes(String str) {
		    if (str.startsWith("\""))
		      {
		          str = str.substring(1, str.length());
		      }
		      if (str.endsWith("\""))
		      {
		          str = str.substring(0, str.length() - 1);
		      }
		      else {
		    	  if (str.startsWith("[")) {
		    		  str = str.substring(1, str.length());
		    	  }
		    	  if (str.endsWith("]")) {
		    		  str = str.substring(0, str.length() - 1);
		    	  }
		    	  else {
		    		  if (str.startsWith("\\")) {
			    		  str = str.substring(1, str.length());
			    	  }
			    	  if (str.endsWith("\\")) {
			    		  str = str.substring(0, str.length() - 1);
		    	  }
		      }
		      } 	  
		      return str; 
		}
// split method for dealing with strings that might contain commas	 
 public static String[] split(String s) {
      int i;
      int f, l;
      ArrayList<String> array = new ArrayList<String>();
      String t = s + "|";	// add sentinel character at end of string

      i = 0;
      while (i < t.length()) {
         f = i;
         l = i;
         if (t.charAt(i) == ',' || t.charAt(i) == '|') { // empty field
            array.add(new String(""));
            i++;
         } else if (t.charAt(i) == '"') {	// start of quoted string
            for (l = f + 1; t.charAt(l) != '|'; l++) {
               if (t.charAt(l) == '"' ) {	// found end of quoted string
                  l++;
                  break;
               }
            }
            array.add(t.substring(f, l));
            i = l + 1;
         } else {
            for (l = f + 1; t.charAt(l) != '|'; l++)
               if (s.charAt(l) == ',')
                  break;
            array.add(s.substring(f, l));
	    i = l + 1;
         }
      }
      String[] ts = new String[array.size()];
      return array.toArray(ts);
   }

   static void dump(String[] array) {
      int i;

      for (i = 0; i < array.length; i++)
         System.out.println(i+": "+array[i]);
   }
	}
