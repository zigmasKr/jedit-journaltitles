/*
 * JournalData.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2008-2014-2019 Zigmantas Kryzius
 * zigmas.kr@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package journaltitles;

/*
 * The class for handling journals' data.
*/

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.Collections;

import org.gjt.sp.jedit.*;

// import org.gjt.sp.jedit.Macros;
// import org.gjt.sp.jedit.View;

public class JournalData {
	
	// Class fields:
	String jdSource;
		// -- the name of the journals' data list
	String jdDataLocation = jEdit.getProperty("options.journaltitles.home");
		// -- the folder where the journals' data list resides
	String jdDataPath;
		// -- the path to the journals' data list
	String jdDataPathSuppl;
		// -- the path to the suppemental journals' data list
	String jdUserChoicePath;
		// -- the path to the "user's choice" data list
	ArrayList<ArrayList<String>> jdData = new ArrayList<ArrayList<String>>();
		// -- the ArrayList containing all journals' data, including variations
	ArrayList<ArrayList<String>> jdDataSuppl = new ArrayList<ArrayList<String>>();
		// -- the ArrayList containing supplemental journals' data, including variations
	ArrayList<ArrayList<String>> jdDataASCII = new ArrayList<ArrayList<String>>();
		// -- the ArrayList of journal data (including variations) in plain ascii
	ArrayList<ArrayList<String>> jdDataASCIISuppl = new ArrayList<ArrayList<String>>();
		// -- the ArrayList of suppemental journal data (including variations) in plain ascii
	int jdTitlePosition;
		// -- the position in the data string where the "normalized" journal title resides
	boolean isDataSuppl;
		// -- the mark that supplemental data are present
	String tracingJdData;
	
	ExtString xs = new ExtString();

	// Constructor without (or with default) argument.
	// The object is created based on the data named "SerialsDB:SPR".
	// That is, the program always will start 
	// with the data "SerialsDB:SPR".
	public JournalData() {
		jdSource = "SerialsDB:SPR"; // "AMS"; 
		isDataSuppl = false;
		tracingJdData = "";
	}

	// data source setter:
	public void setDataSource(String jdsource) {
		jdSource = jdsource;
	}
	
	// The method rolling out the JournalData object completely.
	// Other ways to define the object completely are possible.
	public void rollJournalData() {
		rollJdDataPath();
		rollJdUserChoicePath();
		rollJdData();
		rollJdDataASCII();
		if (isDataSuppl) {
			rollJdDataSuppl();
			rollJdDataASCIISuppl();
		}
	}

	public void clearJournalData() {
		jdData.clear();
		jdDataASCII.clear();
		jdDataSuppl.clear();
		jdDataASCIISuppl.clear();
		isDataSuppl = false;
	}

	// Method defines the path jdDataPath;
	// it is handy to set jdTitlePosition here:
	public void rollJdDataPath() {
		if (jdSource.equals("AMS")) {
			jdDataPath = jdDataLocation + "annser_short.csv";
			/*
			The main file has 2763+ entries (lines) of the following format:
			"","1562-2479","p","Int. J. Fuzzy Syst.","","|","International Journal of Fuzzy Systems","",""
			"","0018-9340","p","IEEE Trans. Comput.","","|","Institute of Electrical and Electronics Engineers. Transactions on Computers","",""
			<details> <details> <details> <accepted abbrev. title> <user?> <|> <details (title variant)> <details (title variant)> ...
			The format is the same as in case of Springer:DB.
			*/
			jdDataPathSuppl = jdDataLocation + "ams-data.txt";
			/*
			The additional file has 2669 entries of the following format:
			"Islam. Math. Astron.","Islamic Mathematics and Astronomy","","Inst. Hist. Arabic-Islam. Sci., Frankfurt am Main.","","1437-241X","N","N","Y","N","N","N"
			"Berkeley Math. Lect. Notes","Berkeley Mathematics Lecture Notes","","Amer. Math. Soc., Providence, RI.","","","N","N","Y","N","N","N"
			<accepted abbrev. title> <complete, or to be abbreviated, title> <details (other data)> <details (other data)>
			*/
			jdTitlePosition = 0;
			isDataSuppl = true;
		}
		else if (jdSource.equals("SerialsDB:SPR")) {
			jdDataPath = jdDataLocation + "Springer-export.csv";
			/*
			The main file has 8805+ entries (lines) of the following format:
			"8880","0198-6821","m","Pap. Phys. Oceanogr. Meteorol.","remi","|","Papers in Physical Oceanography and Meteorology"
			"1517","1350-4495","p","Infrared Phys. Technol.","daivab","|","Infrared Physics & Technology","Infrared Physics \& Technology","Infrared Physics and Technology"
			<details> <details> <details> <accepted abbrev. title> <user> <|> <variant of title> <...>
			*/
			jdTitlePosition = 0;
			isDataSuppl = false;
		}
		else if (jdSource.equals("JEL")) {
			jdDataPath = jdDataLocation + "jel-data.txt";
			jdTitlePosition = 2;   // to be checked
			isDataSuppl = false;
		}
		else if (jdSource.equals("UCB")) {
			jdDataPath = jdDataLocation + "ucb-data.txt";
			jdTitlePosition = 0;
			isDataSuppl = false;
		}
		else if (jdSource.equals("PHYSREV")) {
			jdDataPath = jdDataLocation + "physrev-data.txt";
			jdTitlePosition = 0;
			isDataSuppl = false;
		}
		else {
			jdDataPath = jdDataLocation + "annser_short.csv";
			jdTitlePosition = 0;
			isDataSuppl = false;
		}
	}

	// Method defines jdUserChoicePath:
	public void rollJdUserChoicePath() {
		if (jdSource.equals("SerialsDB:SPR")) {
			jdUserChoicePath = jdDataLocation + "Springer-user-choice.csv";
		}
		else if (jdSource.equals("AMS")) {
			jdUserChoicePath = jdDataLocation + "annser-user-choice.csv";
		}
		else {
			jdUserChoicePath = jdDataLocation + "user-choice.csv";
		}
	}

	// Method rolls out data:
	public void rollJdData() {
		// two-dimensional list of data:
		File dataFile = new File(jdDataPath);
		// list of file lines:
		ArrayList<String> linesList = getListOfLinesFromFile(dataFile);
		for (int i = 0; i < linesList.size(); i++) {
			if (!linesList.get(i).equals("")) {
				// if line is not empty:
				ArrayList<String> lineChunks = splitLineToChunks(linesList.get(i));
				jdData.add(lineChunks);
			}
		}
	}

	// Method rolls out supplemental data:
	public void rollJdDataSuppl() {
		// two-dimensional list of data;
		// the same as in rollJdData, but the path to the file is different
		File dataFile = new File(jdDataPathSuppl);
		// list of file lines:
		ArrayList<String> linesList = getListOfLinesFromFile(dataFile);
		for (int i = 0; i < linesList.size(); i++) {
			if (!linesList.get(i).equals("")) {
				// if line is not empty:
				ArrayList<String> lineChunks = splitLineToChunks(linesList.get(i));
				jdDataSuppl.add(lineChunks);
			}
		}
	}
	
	public ArrayList<String> splitLineToChunks(String line) {
		/*
		String-line, read from file, is rendered to ArrayList<String>.
		public String[] split(String regex, int limit) or
		public String[] split(String regex)
		*
		The first word starts with quot. mark, the last word ends with quot. mark.
		Quot. marks are to be deleted.
		*/
		ArrayList<String> chunksOfLine = new ArrayList<String>();
		line = line.substring(1, line.length() - 1);
		String[] chunks = line.split("\",\"");
		for (String chk : chunks) {
			chunksOfLine.add(chk);
		}
		return chunksOfLine;
	}
	
	public ArrayList<String> getListOfLinesFromFile(File pathToFile) {
		/*
		Journal data are read from the file dataFile.
		Data of any journal are written in one line, separated by commas.
		List of strings (lines) is returned.
		*/
		ArrayList<String> listOfLines = new ArrayList<String>();
		
		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			//use buffering, reading one line at a time;
			//FileReader always assumes that the default encoding is OK!
			input = new BufferedReader( new FileReader(pathToFile) );
			String line = null; //not declared within while loop
			/*
			readLine is a bit quirky:
			it returns the content of a line MINUS the newline;
			it returns null only for the END of the stream;
			it returns an empty String if two newlines appear in a row.
			*/
			while ((line = input.readLine()) != null) {
				listOfLines.add(line);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return listOfLines;
	}

	/*
	* What is jdDataASCII for?
	* It is to make the search/compare of journal titles more flexible
	* Depends on @option.
	*/
	public void rollJdDataASCII() {
		ArrayList<String> journalDataLine;
		if (jdSource.equals("SerialsDB:SPR") ||
			jdSource.equals("AMS")) {
			// The structure of data files in cases of Springer:DB and AMS is the same.
			for (int j = 0; j < jdData.size(); j++) {
				journalDataLine = jdData.get(j);
				ArrayList<String> currentJrnData = new ArrayList<String>();
				for (int jj = 0; jj < journalDataLine.size(); jj++) {
					currentJrnData.add(asciiString(journalDataLine.get(jj)));
				}
				jdDataASCII.add(currentJrnData);
			}
		}
		else {
			/*
			In other cases we take only 2 elements,
			but this is not elaborated completely.
			*/
			for (int j = 0; j < jdData.size(); j++) {
				journalDataLine = jdData.get(j);
				ArrayList<String> currentJrnData = new ArrayList<String>();
				for (int jj = 0; jj < 2; jj++) {
					currentJrnData.add(asciiString(journalDataLine.get(jj)));
				}
				jdDataASCII.add(currentJrnData);
			}
		}
	}

	/*
	This method is needed (so far) for the "AMS" case only.
	It is controlled by the variable isDataSuppl.
	For example, for the "AMS" case:
	"Islam. Math. Astron.","Islamic Mathematics and Astronomy","","Inst. Hist. Arabic-Islam. Sci., Frankfurt am Main.","","1437-241X","N","N","Y","N","N","N"
	"Berkeley Math. Lect. Notes","Berkeley Mathematics Lecture Notes","","Amer. Math. Soc., Providence, RI.","","","N","N","Y","N","N","N"
	<accepted abbrev. title> <complete, or to be abbreviated, title> <details (other data)> <details (other data)>
	*/
	public void rollJdDataASCIISuppl() {
		ArrayList<String> journalDataLine;
		if (jdSource.equals("AMS")) {
			for (int j = 0; j < jdDataSuppl.size(); j++) {
				journalDataLine = jdDataSuppl.get(j);
				ArrayList<String> currentJrnData = new ArrayList<String>();
				for (int jj = 0; jj < 2; jj++) {
					currentJrnData.add(asciiString(journalDataLine.get(jj)));
				}
				jdDataASCIISuppl.add(currentJrnData);
			}
		}
	}

	// Method: jdMatchingJournalsData
	public ArrayList<ArrayList<String>> jdMatchingJournalsData(
			String jrnName,
			boolean fromBeginning,
			int level) {
		/*
		* Method selects journal titles that 'match' to the given 
		* (or INPUT) string @jrnName.
		* @jrnName can be somehow abbreviated or complete (nonabbrev.) journal title.
		* We are looking for journal titles 'matching' to jrnName.
		* Special patterns are constructed from jrnName for the search.
		* The algorithm for search of the canonical abbrev. journal title 
		* depends on @jdSource.
		* The parameter @level means the flexibility level of the regex pattern @prepattern.
		* === Remainder on the data array of SerialsDB_SPR ===
		* A typical file line is of the form (written in several lines for a better view):
		* (1)
		* "153","0018-8646","p","IBM J. Res. Dev.","valdasd","|",
		* "IBM Journal of Research and Development","IBM J. Res. Develop.",
		* "International Business Machines Journal of Research and Development","Journal of Research and Development"
		* (2)
		* "344","0024-6107","p","J. Lond. Math. Soc.","valdasd",
		* "J. Lond. Math. Soc. (2)","remi","|",
		* "Journal of the London Mathematical Society","J. Lond. Math. Soc. (Print)",
		* "Journal of the London Mathematical Society (Print)","J. London Math. Soc. (2)",
		* "Journal of the London Mathematical Society. Second Series"
		* (3)
		* "695","1126-6708","p","J. High Energy Phys.","vilma","|",
		* "The Journal of High Energy Physics","Journal of High Energy Physics",
		* "JHEP","Journal of High Energy Physics. a SISSA Journal"
		* === The case of AMS ===
		* The data structure in the main file is the same as in case of Springer:DB:
		* (4) "","1562-2479","p","Int. J. Fuzzy Syst.","","|","International Journal of Fuzzy Systems","",""
		* (5) "","0018-9340","p","IEEE Trans. Comput.","","|","Institute of Electrical and Electronics Engineers. Transactions on Computers","",""
		* <details> <detailes> <details> <canonical abbrev. title> <user?> <|> <details (variant of title)> <details (variant of title)> ...
		* === Meaning of fields ===
		* Field [0] is a number in order.
		* Field [1] is ISSN number.
		* Field [2] is some mark, not important for our needs.
		* Field [3] is a approved (canonic) abbrev. journal title.
		* Field [4] is a name of the user who 'provided' the canonic title.
		* Other fields before "|": canonic abbrev. journal title is not unique;
		* there can be more than one such title, and each such title has its 'provider'.
		* Fields after "|": other observed forms of the journal title, complete or abbreviated.
		*  === Algorith of search of matching titles ===
		* If any title going after "|" 'matches' the 'prepattern', then
		* all abbrev. titles before "|" are included in the list of search results.
		* For every finding, as additional information, approved titles and their 'providers' 
		* are included, and then, all known forms of this title.
		* The auxiliary information (or details) will be distinguished in
		* JournalDataStructure.java.
		*/
		ArrayList<ArrayList<String>> matchingJournals = new ArrayList<ArrayList<String>>();
		// Data: jdSource, jdData, jdDataASCII
		String tOption = jdSource;
		String[] prePatternList = prepatternList(jrnName, level);
		String prePattern = "";
		Pattern pp;
		prePattern = prePatternList[0];
		// sequences of any symbols are introduced between the 'words' of jrnName:
		int listLength = prePatternList.length;
		int listPosition;
		boolean isAnyFound;
		boolean isAnyExact;
		if (listLength > 1) {
			for (int i = 1; i < listLength; i++) {
				prePattern = prePattern + ".*?" + prePatternList[i];
			}
		} else {
			prePattern = prePattern + ".*?";
		}
		// if the 'match' must be from beginning, then
		// the article can appear in the beginning :) :
		if (fromBeginning) {
			prePattern = "^T?h?e?\\s?" + prePattern;
		}
		pp = Pattern.compile(prePattern, Pattern.CASE_INSENSITIVE);
		if (tOption.equals("SerialsDB:SPR") ||
			tOption.equals("AMS")) {
			// For cases of Springer:DB and AMS basic data files, 
			// the algorithm of search of 'matching' journal titles is the same.
			/*
			* In the case of "SerialsDB:SPR", it is assumed that the journal title is 'suitable',
			* if at least one title version 'matches' @prepattern.
			* It is assumed that the complete journal title is the first after "|".
			* With some loss of effectiveness, but simplifying the algorithm,
			* one can perform the check of 'suitability' against all the elements jdData.get(k).
			*/
			/*
			* "344","0024-6107","p","J. Lond. Math. Soc.","valdasd",
			* "J. Lond. Math. Soc. (2)","remi","|",
			* "Journal of the London Mathematical Society","J. Lond. Math. Soc. (Print)",
			* "Journal of the London Mathematical Society (Print)","J. London Math. Soc. (2)",
			* "Journal of the London Mathematical Society. Second Series"
			*/
			ArrayList<String> currentJournalData = new ArrayList<String>();
			String currentID;
			String currentISSN;
			String currentUser;
			int currentSize;
			int positionOfVert = 0;
			// in the below loop this variable 
			// "might not have been initialized";
			for (int k = 0; k < jdDataASCII.size(); k++) {
				for (int kk = 0; kk < jdDataASCII.get(k).size(); kk++) {
					// if at least one ASCII version of the journal title 'matches' the pattern
					// made from jrnName, the journal title is taken from jdData:
					if (pp.matcher(jdDataASCII.get(k).get(kk)).find()) {
						currentJournalData = jdData.get(k);
						currentID = currentJournalData.get(0);
						currentISSN = currentJournalData.get(1);
						currentSize = currentJournalData.size();
						// 'type' is omitted;
						// find the position of "|":
						for (int j = 0; j < currentSize; j++) {
							if (currentJournalData.get(j).equals("|")) {
								positionOfVert = j;
								break;
							}
						}
						int position = 3; // initial position
						do {
							ArrayList<String> currentDataA = new ArrayList<String>();
							// '0' string is the approved abbrev. (canonic) journal title;
							// what follows is the "auxilliary information":
							currentDataA.add(currentJournalData.get(position));
							// '1' string is the 'technical' mark of the 'matching' level:
							currentDataA.add("=REGEX=");
							// '2-3-4' strings are 'technical' data:
							currentUser = currentJournalData.get(position + 1);
							if (tOption.equals("SerialsDB:SPR")) {
								currentDataA.add("DB id: " + currentID);
							}
							else if (tOption.equals("AMS")) {
								listPosition = k + 1;
								currentDataA.add("DB id: " + "AMS csv: " + listPosition);
							}
							currentDataA.add("DB user: " + currentUser);
							currentDataA.add("ISSN: " + currentISSN);
							// the further strings are variants of the journal title:
							for (int jj = positionOfVert + 1; jj < currentSize; jj++) {
								currentDataA.add(currentJournalData.get(jj));
							}
							matchingJournals.add(currentDataA);
							position = position + 2;
						}
						while (position + 2 < positionOfVert);
						// this loop most often takes 1 or 2 runs
					}
				}
			}
		}
		// In the case of AMS, we look for 'matching' journal titles by 
		// additional data.
		if (tOption.equals("AMS")) {
			/* The case of AMS additional data:
			0 position - abbreviated journal title;
			1 position - complete journal title; further - additional data.
			"Islam. Math. Astron.","Islamic Mathematics and Astronomy","","Inst. Hist. Arabic-Islam. Sci., Frankfurt am Main.","","1437-241X","N","N","Y","N","N","N"
			"Berkeley Math. Lect. Notes","Berkeley Mathematics Lecture Notes","","Amer. Math. Soc., Providence, RI.","","","N","N","Y","N","N","N"
			<canonical abbrev. title> <complete, or to be abbreviated, title> <details (other data)> <details (other data)> (kt. duom.)>
			*/
			for (int k = 0; k < jdDataASCIISuppl.size(); k++) {
				if (pp.matcher(jdDataASCIISuppl.get(k).get(0)).find() ||
					pp.matcher(jdDataASCIISuppl.get(k).get(1)).find()) {
					ArrayList<String> currentDataB = new ArrayList<String>();
					currentDataB.add(jdDataSuppl.get(k).get(0));
					currentDataB.add("=REGEX=");
					for (int kk = 1; kk < jdDataSuppl.get(k).size(); kk++) {
						currentDataB.add(jdDataSuppl.get(k).get(kk));
					}
					matchingJournals.add(currentDataB);
				}
			}
		}
		else if (tOption.equals("JEL") ||
				  tOption.equals("UCB") ||
				  tOption.equals("PHYSREV")) {
			/* Other cases: "PHYSREV", "UCB", "JEL".
			0 position - abbreviated journal title;
			1 position - complete, or to be abbreviated, title; no more positions.
			*/
			for (int k = 0; k < jdDataASCII.size(); k++) {
				if (pp.matcher(jdDataASCII.get(k).get(0)).find() ||
					pp.matcher(jdDataASCII.get(k).get(1)).find()) {
					ArrayList<String> currentDataC = new ArrayList<String>();
					currentDataC.add(jdData.get(k).get(0));
					currentDataC.add("=REGEX=");
					for (int kk = 1; kk < jdData.get(k).size(); kk++) {
						currentDataC.add(jdData.get(k).get(kk));
					}
					if (currentDataC.get(0).equals(jrnName)) {
						currentDataC.set(1, "=EXACT=");
					}
					else if (currentDataC.get(2).equals(jrnName)) {
						currentDataC.set(1, "=FOUND=");
					}
					matchingJournals.add(currentDataC);
				}
			}
		}
		/*
		* In the array of 'suitable' records, there can be coinciding ones.
		* If for some records the first two strings (in positions "0" and "2-3-4") coincide,
		* only one such record can be accepted.
		*/
		for (int k = 0; k < matchingJournals.size(); k++) {
			for(int mm = k + 1; mm < matchingJournals.size(); mm++) {
				if (matchingJournals.get(k).get(0).equals(matchingJournals.get(mm).get(0)) &&
					matchingJournals.get(k).get(2).equals(matchingJournals.get(mm).get(2)) &&
					matchingJournals.get(k).get(3).equals(matchingJournals.get(mm).get(3)) &&
					matchingJournals.get(k).get(4).equals(matchingJournals.get(mm).get(4))) {
					// the record with index mm is removed from matchingJournals:
					matchingJournals.remove(matchingJournals.get(mm));
					mm = mm - 1;
				}
			}
		}
		/*
		* If there are elements in the array matchingJournals for which the searched
		* journal title is found precisely (=FOUND=, and not =REGEX=), 
		* then we can limit ourselves to these elements.
		* The size of matchingJournals is small, therefore we can play
		* with it in any way.
		*/
		isAnyFound = false;
		for (int n = 0; n < matchingJournals.size(); n++) {
			for (int nn = 5; nn < matchingJournals.get(n).size(); nn++) {
				if (matchingJournals.get(n).get(nn).equals(jrnName)) {
					matchingJournals.get(n).set(1, "=FOUND=");
					isAnyFound = true;
				}
			}
		}
		/* If there are elements with the 'matching' level =FOUND= in title
		variants, then elements with the 'matching' level =REGEX= 
		are removed (only elements with =FOUND= remain). */
		if (isAnyFound) {
			for (int n = 0; n < matchingJournals.size(); n++) {
				// The check 
				// if (matchingJournals.get(n).get(1).equals("=REGEX=")) {
				// does not work well for some reason.
				if (!matchingJournals.get(n).get(1).equals("=FOUND=")) {
					matchingJournals.remove(matchingJournals.get(n));
					n = n - 1;
				}
			}
		}
		/*
		* Further, one can check if the searched journal title is true, 
		* i.e. coincides with the approved abbrev. title version (=EXACT=). 
		* This means that it can be found in some element
		* of matchingJournals in the position "0".
		*/
		isAnyExact = false;
		for (int n = 0; n < matchingJournals.size(); n++) {
			if (matchingJournals.get(n).get(0).equals(jrnName)) {
				matchingJournals.get(n).set(1, "=EXACT=");
				isAnyExact = true;
			}
		}
		/* If in some elements the searched journal title coincides with the 
		approved one, only these elements are retained, the other are removed
		*/
		if (isAnyExact) {
			for (int n = 0; n < matchingJournals.size(); n++) {
				if (!matchingJournals.get(n).get(1).equals("=EXACT=")) {
					matchingJournals.remove(matchingJournals.get(n));
					n = n - 1;
				}
			}
			/* If there are several elements with =EXACT=, only one is retained
			unless there are some unexpected possibilities.
			Such elements can differ in positions "DB id", "user" only.
			*/
			for (int n = 0; n < matchingJournals.size(); n++) {
				if (n > 0) {
					matchingJournals.remove(matchingJournals.get(n));
					n = n - 1;
				}
			}
		}

		return matchingJournals;
	}

	public void appendJdData(ArrayList<String> affix) {
		jdData.add(affix);
		ArrayList<String> tempAffix = new ArrayList<String>();
		for (int j = 0; j < affix.size(); j++) {
			tempAffix.add(asciiString(affix.get(j)));
		}
		jdDataASCII.add(tempAffix);
	}

	// ASCII variant of a given string.
	private String asciiString(String astr) {
		return xs.plainAscii(astr);
	}
	
	public String[] prepatternList(String aString, int trunckLevel) {
		/* Based on the given string the following 'pattern' Array 
		is constructed:
		ASCII variant is taken;
		fullstops, commas, dashes are replaced with spaces;
		( --> \(    ) --> \)
		split on spaces into Array.
		*/
		Pattern p_punctuation = Pattern.compile("[\\.,\\-:\\(\\)~]+?");
		// all grouping fences:
		Pattern p_braces = Pattern.compile("\\[|\\{|\\}|\\]");
		Pattern p_spaces = Pattern.compile("\\s+");
		Matcher m_punctuation = p_punctuation.matcher(aString);
		aString = m_punctuation.replaceAll(" ");
		Matcher m_braces = p_braces.matcher(aString);
		aString = m_braces.replaceAll("");

		String[] items = p_spaces.split(aString);
		int lengthLevel;
		if (trunckLevel > 0) {
			for (int k = 0; k < items.length; k++) {
				lengthLevel = items[k].length() - trunckLevel;
				if (lengthLevel < 1) {
					lengthLevel = 1;
				}
				// at least first original letter is retained
				items[k] = items[k].substring(0, lengthLevel);
			}
		}
		return items;
	}
}

