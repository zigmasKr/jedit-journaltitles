/*
 * JournalDataDynamic.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2008-2013-2020 Z.K.
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
* Class where all basic relations among principal components are programmed.
* GUI is programmed in JournalDataGUI.java
*/

import java.io.*;
import java.util.*;

public class JournalDataDynamic {
	/*
	* This class comprises all points related to JournalData and GUI 
	* functionality, but GUI elements themselves are described in JournalDataGUI.
	* (The word 'Change' is omitted for brevity.)
	*/

	static String[] arrayOfDataSources = {
			"SerialsDB:SPR",
			"Mathematical Reviews (AMS)",
			"Journal of Economical Literature",
			"UBC Library",
			"Physical Review",
			"ZZZ"
			};

	JournalData jd;
		// a component of class object, being an object of JournalData
		// constructed from the journal data file
	int indexOfDataSource;
		// an index of a chosen element from arrayOfDataSources
	String selectedSource;
		// a chosen jdSource
	String inputTitleOrig;
		// the journal title that appears in the field
		// "Duotas zurnalo pavadinimas", directly from the LaTeX file
	String inputTitle;
		// the journal title for which the 'approved' title is searched;
		// it is seen in the field "Input journal title";
		// it can be the edited inputTitleOrig
	boolean matchBeginning;
		// the variable which defines the way how the journal title is searched:
		// "from the start" (a more restrictive way)
		// or "in other way";
		// used in JournalData
	ArrayList<ArrayList<String>> jnDataMatched;
		// the array of data of journals with 'matching' titles
	ArrayList<ArrayList<String>> jnUserData;
		// a component of class object made from the "user-choice"
		// data on journal titles
	ArrayList<ArrayList<String>> jnUserDataMatched;
		// the array of data of journals with 'matching' titles, from jdUserData
	ArrayList<ArrayList<String>> jnAllDataMatched;
	ArrayList<String> jnTitlesMatched;
		// the list of 'matching' journal titles
	int indexTitleSelected;
		// the index of the selected title in the list 
		// of 'matching' journal titles
	ArrayList<String> jnTitleDetails;
		// details of the journal, selected from the list
		// of 'matching' journals
	String selectedTitle;
		// the journal title, selected as 'approved'
	String selectedTitleOutput;
		// can be modified, e.g., for the Vancouver mode
		
	/* The file of UserChoice records and the tracing file are best suitable as
	components of JournalDataDynamic object */
	FileWriter userChoiceFile;
		// user choice file
	FileWriter tracingFile;  // ?
		// tracing/debug file
	boolean isTracingOn;
	StringBuilder tracingStrb;
	String userChoiceDataAccumulator;
	String tracingAccumulator;
	// strings for messages via Macro.message class ... in GUI
	String userChoiceFileExceptionMessage;
	//String userChoiceFileCloseExceptionMessage;
	String tracingFileExceptionMessage;
	String tracingFileCloseExceptionMessage;
	String userChoiceWriteExceptionMessage;
	// the variable for Vancouver mode, vis GUI toggle button "NoDots"
	boolean isVancouverMode;
	// for the control:
	String userChoiceTrace;
	
	ExtString xs = new ExtString();

	public JournalDataDynamic() {
		jd = new JournalData();
		jd.rollJournalData();
		indexOfDataSource = 0;
		selectedSource = "ZZZ";
		inputTitle = "";
		inputTitleOrig = "";
		selectedTitle = "";
		matchBeginning = true;
		isVancouverMode = false;
		indexTitleSelected = -1;
		jnDataMatched = new ArrayList<ArrayList<String>>();
		jnUserData = new ArrayList<ArrayList<String>>();
		jnUserDataMatched = new ArrayList<ArrayList<String>>();
		jnAllDataMatched = new ArrayList<ArrayList<String>>();
		jnTitlesMatched = new ArrayList<String>();
		jnTitleDetails = new ArrayList<String>();
		//
		userChoiceDataAccumulator = "";
		tracingAccumulator = "";  // ?
		isTracingOn = false;
		tracingStrb = new StringBuilder();
		//
		userChoiceFileExceptionMessage = "";
		tracingFileExceptionMessage = "";
		tracingFileCloseExceptionMessage = "";
		//
		rollUserChoiceFile();
		//rollTracingFile();
		//
		userChoiceTrace = "";
	}

	/* Method which is used to get the new, right value of 
	selectedSource when the value of indexOfDataSource is changed.
	*/
	public void rollSelectedSource() {
		if (indexOfDataSource == 0) {
			selectedSource = "SerialsDB:SPR";
		} else if (indexOfDataSource == 1) {
			selectedSource = "AMS";
		} else if (indexOfDataSource == 2) {
			selectedSource = "JEL";
		} else if (indexOfDataSource == 3) {
			selectedSource = "UCB";
		} else if (indexOfDataSource == 4) {
			selectedSource = "PHYSREV";
		} else if (indexOfDataSource == 5) {
			selectedSource = "ZZZ";
		}
	}

	//Method rolljnDataMatched
	public void rolljnDataMatched() {
		// It is assumed that jd.jdData is already defined.
		jnDataMatched.clear();
		jnDataMatched = jd.jdMatchingJournalsData(
			inputTitle,
			matchBeginning,
			0              // level
			);
	}

	//Method rolljnUserDataMatched
	public void rolljnUserDataMatched() {
		/* From the list jnUserData we select records that
		'match' the given journal title inputTitle.
		This is simple: we just check if inputTitle
		coincides with the position [5] of the record jnUserData
		*/
		jnUserDataMatched.clear();
		for (int m = 0; m < jnUserData.size(); m++) {
			if(inputTitle.equals(jnUserData.get(m).get(5))) {
				jnUserDataMatched.add(jnUserData.get(m));
			}
		}
	}

	public void appendUserData() {
		// Only nonrepeating records are appended.
		boolean isInUserData = false;
		userChoiceTrace = "";
		for (int q = 0; q < jnUserData.size(); q ++) {
			if ((jnUserData.get(q).get(0).equals(selectedTitle)) &&
				(jnUserData.get(q).get(5).equals(inputTitleOrig))) {
				isInUserData = true;
			}
		}
		if (!isInUserData) {
			// new ArrayList<String> is needed
			ArrayList<String> userChoice = new ArrayList<String>();
			userChoice.add(selectedTitle);                 // [0]
			userChoice.add("=ACCEPTED=");                  // [1]
			userChoice.add("DB id: ");                     // [2]
			userChoice.add("DB user: " + "=user choice="); // [3]
			userChoice.add("ISSN: ");                      // [4]
			userChoice.add(inputTitleOrig);                // [5]
			if (selectedTitle.equals(inputTitleOrig)) {
				userChoice.set(1, "=EXACT=");
			}
			// here jnUserData is appended:
			jnUserData.add(userChoice);
			// Here is a string for a possible tracing:
			//for (int m = 0; m < userChoice.size(); m++) {
			//	userChoiceTrace = userChoiceTrace + userChoice.get(m) + " $ ";
			//}
		}
	}

	public void appendUserDataForFile() {
		/*
		* A sample from the file Springer-export.csv:
		* "9","0022-040X","p","J. Differ. Geom.","daivab","|","Journal of Differential Geometry","J. Differential Geom."
		* The "user-choice" data file is filled in with the record of the same form.
		* "","","","<jdd.selectedTitle>","<user-choice>","|","<jdd.inputTitleOrig>"
		* The "user-choice" data file is opened for appending with 
		* the "START" button, and closed with the "STOP" button.
		*/
		userChoiceDataAccumulator = userChoiceDataAccumulator +
			"\"\",\"\",\"\",\"" +
			selectedTitle + "\",\"" +
			"user-choice" + "\",\"|\",\"" +
			inputTitleOrig + "\"\n";
	}

	public void rolljnAllDataMatched() {
		/* In both lists - jnDataMatched and jnUserDataMatched -
		there can be records that could be considered coinciding.
		They are records of =EXACT= or =FOUND= type having real 'User ID' or just 'user choice'.
		In case of any problems, one could eliminate some records...
		At least one record can be of type =ACCEPTED=; if there is such a record, 
		it is retained, and others are removed.
		(This episode could moved into "auto next".)
		*/
		jnAllDataMatched.clear();
		for (int m = 0; m < jnUserDataMatched.size(); m++) {
			jnAllDataMatched.add(jnUserDataMatched.get(m));
		}
		for (int m = 0; m < jnDataMatched.size(); m++) {
			jnAllDataMatched.add(jnDataMatched.get(m));
		}
		boolean isAnyAccepted = false;
		for (int n = 0; n < jnAllDataMatched.size(); n++) {
			if (jnAllDataMatched.get(n).get(1).equals("=ACCEPTED=")) {
				isAnyAccepted = true;
			}
		}
		if (isAnyAccepted) {
			for (int n = 0; n < jnAllDataMatched.size(); n++) {
				if (!jnAllDataMatched.get(n).get(1).equals("=ACCEPTED=")) {
					jnAllDataMatched.remove(jnAllDataMatched.get(n));
					n = n - 1;
				}
			}
		}
	}

	//Method rolljnTitlesMatched
	public void rolljnTitlesMatched() {
		/* It is assumed that jnAllDataMatched is already available. */
		jnTitlesMatched.clear();
		// two lists are joined:
		for(int m = 0; m < jnAllDataMatched.size(); m++) {
			jnTitlesMatched.add(jnAllDataMatched.get(m).get(jd.jdTitlePosition));
		}
	}

	// Method: rolljnTitleDetais
	public void rolljnTitleDetais(int ind) {
		/* Details (including title): */
		jnTitleDetails = jnAllDataMatched.get(ind);
	}

	// Method sets userChoiceFile:
	public void rollUserChoiceFile() {
		//the @true will append the new data:
		try {
			userChoiceFile = new FileWriter(jd.jdUserChoicePath, true);
		} catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
	}

	// Method sets tracingFile:
	public void rollTracingFile() {
		/* The tracing file is fixed, but one can do this in a more flexible way. */
		// the @true will append the new data:
		try {
			tracingFile = new FileWriter("d:\\temp\\jtc-trace.txt", true);
		} catch(IOException io) {
			tracingFileExceptionMessage = "Tracing:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
			//
			tracingStrb.append("Tracing:: IOException" + io.getMessage() + "\n");
		}
	}

	public void appendTracingAccumulator(String where) {
		/* Records to tracingAccumulator are done here.
		Other records are possible. too.
		This is just for tracing/debuging.
		*/
		StringBuilder trA = new StringBuilder();
		if (where.equals("Find")) {
			trA.append("=== On FIND ===\n");
			trA.append("\ntracing jdd :: jnUserData\n\n");
			for (int k = 0; k < jnUserData.size(); k++) {
				trA.append("k=" + k + " [0]: " + jnUserData.get(k).get(0) + "\n");
				trA.append("k=" + k + " [5]: " + jnUserData.get(k).get(5) + "\n");
			}
			trA.append("\n\ntracing jdd :: jnUserDataMatched\n\n");
			for (int k = 0; k < jnUserDataMatched.size(); k++) {
				trA.append("k=" + k + " [0]: " + jnUserDataMatched.get(k).get(0) + "\n");
				trA.append("k=" + k + " [5]: " + jnUserDataMatched.get(k).get(5) + "\n");
			}
		} else if (where.equals("Accept")) {
			trA.append("=== On ACCEPT ===\n");
			trA.append("[0]: selectedTitle = " + selectedTitle + "\n");
			trA.append("[1]: =FOUND=" + "\n");
			trA.append("[2]: DB id: " + "\n");
			trA.append("[3]: =user choice=" + "\n");
			trA.append("[4]: ISSN: " + "\n");
			trA.append("[5]: inputTitle = " + inputTitleOrig + "\n\n");
		}
		tracingStrb.append(trA);
	}

	// Method closes UserChoiceData file:
	public void writeAndCloseUserChoiceFile() {
		/* Writes accumulated 'user choice' data and closes the file. */
		try {
			userChoiceFile.write(userChoiceDataAccumulator);
			userChoiceFile.close();
		}  catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice 5:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
			//
			tracingStrb.append("=ERROR in writeAndCloseUserChoiceFile=\n");
			tracingStrb.append("User Choice File Exception Message:\n" 
				+ "UserChoice 5:: IOException" + io.getMessage() + "\n");
		}
	}


	// Method closes TracingFile:
	public void writeAndCloseTracingFile() {
		/* Writes accumulated tracing data and closes the file. */
		try {
			tracingFile.write(tracingAccumulator);
			tracingFile.close();
		}  catch(IOException io) {
			tracingFileCloseExceptionMessage = "Tracing Close:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
	}

	public void loadUserChoiceData() {
		/* The data are read from UserChoiceFile, lines are sorted,
		* repetitions are removed; the data in these lines are
		* joined to the array jnUserData.
		*/
		// (1) the data file UserChoice is read:
		File userDataFile = new File(jd.jdUserChoicePath);
		ArrayList<String> userChoiceLines = jd.getListOfLinesFromFile(userDataFile);
		// (2) data of UserChoice are put in order:
		Collections.sort(userChoiceLines);
		for (int c = 0; c < userChoiceLines.size() - 1; c++) {
			if (userChoiceLines.get(c).equals(userChoiceLines.get(c + 1))) {
				userChoiceLines.remove(userChoiceLines.get(c + 1));
				c = c - 1;
			}
		}
		// (3) the array jnUserData is appended:
		for (int c = 0; c < userChoiceLines.size(); c++) {
			ArrayList<String> usrChoice = new ArrayList<String>();
			usrChoice = jd.splitLineToChunks(userChoiceLines.get(c));
			// "","","",                               // [0], [1], [2]
			// "J. Biomed. Inform.","user-choice","|", // [3], [4], [5]
			// "Journal of Biomedical Informatics"     // [6]
			usrChoice.set(0, usrChoice.get(3));
			usrChoice.set(1, "=ACCEPTED=");
			usrChoice.set(2, "DB id: ");
			usrChoice.set(3, "DB user: " + "=user choice=");
			usrChoice.set(4, "ISSN: ");
			usrChoice.remove(usrChoice.get(5));
			jnUserData.add(usrChoice);
		}
		// (4) Ordered data are written back to the UserChoice data file:
		String userChoiceOrdered = userChoiceLines.get(0) + "\n";
		for (int d = 1; d < userChoiceLines.size(); d++) {
			userChoiceOrdered = userChoiceOrdered +
								 userChoiceLines.get(d) + "\n";
		}
		try {
			userChoiceFile.close();
		}  catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice 1:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
		try {
			userChoiceFile = new FileWriter(jd.jdUserChoicePath);
		} catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice 2:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
		try {
			userChoiceFile.write(userChoiceOrdered);
			userChoiceFile.close();
		}  catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice 3:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
		try {
			userChoiceFile = new FileWriter(jd.jdUserChoicePath, true);
		} catch(IOException io) {
			userChoiceFileExceptionMessage = "UserChoice 4:: IOException" + io.getMessage();
			System.out.println("IOException" + io.getMessage());
		}
	}

	public void makeSelectedTitleOutput() {
		/* Method constructs the component selectedTitleOutput
		from selectedTitle and isVancouverMode;
		other conditions of construction are possible.
		All fullstops '.' should be removed,
		unless are they are proceeded with 'backslash' \\.
		*/
		if (isVancouverMode) {
			selectedTitleOutput = xs.removeFullStops(selectedTitle);
		}
		else {
			selectedTitleOutput = selectedTitle;
		}
	}

}


