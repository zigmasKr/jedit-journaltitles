/*
 * GuiLabels.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2019 Zigmantas Kryzius
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

public class GuiLabels {
	
	String lbButtonStart;
	String lbButtonAuto;
	String lbMenuExtra;
	String lbButtonNoDots;
	String lbButtonStop;
	String lbButtonFind; 
	String lbSearchFromBeginning;
	String lbButtonAccept;
	String lbButtonNext;
	String lbMenuItemMark;
	String lbMenuItemDistinguishMarked;
	String lbMenuItemDistinguishAll;
	String lbMenuItemDataGathered;
	String lbMenuItemAutoPlus;
	String lbMenuItemEditTitles;
	String lbBorderSearch;
	String lbLabelInputJournalTitle;
	String lbBorderResults;
	String lbLabelJournalTitles;
	String lbLabelDetails;
	String lbBorderDataSource;
	String lbTextNotFound;
	String lbDummyElement;
	String lbDummyElementA;
	String lbDummyElementB;
	String lbErrorMessageA;
	String lbErrorMessageB;
	String lbMessageHomeNotFound;
	String lbMessageStopped;
	String lbLabelJournalTitlesList;
	
	public GuiLabels(String str) {
		if (str.equals("EN")) {
			lbButtonStart = "START";
			lbButtonAuto = "AUTO";
			lbMenuExtra = "MENU";
			lbButtonNoDots = "No Dots";
			lbButtonStop = "STOP";
			lbButtonFind = "Find";
			lbSearchFromBeginning = "Search from beginning";
			lbButtonAccept = "Accept";
			lbButtonNext = "Next";
			lbMenuItemMark = "Mark";
			lbMenuItemDistinguishMarked = "Distinguish marked";
			lbMenuItemDistinguishAll = "Distinguish all";
			lbMenuItemDataGathered = "Data gathered";
			lbMenuItemAutoPlus = "AUTO+";
			lbMenuItemEditTitles = "Edit Titles";
			lbBorderSearch = "Search";
			lbLabelInputJournalTitle = "Input journal title:";
			lbBorderResults = "Results of search";
			lbLabelJournalTitles = "Journal titles:";
			lbLabelDetails = "Details:";
			lbBorderDataSource = "Journal data source";
			lbTextNotFound = "Could not find \\bseries ... \\btitle";
			lbDummyElement = "... ...";
			lbDummyElementA = "...";
			lbDummyElementB = "...";
			lbErrorMessageA = "Journal not found. Error 1";
			lbErrorMessageB = "Journal not found. Error 2";
			lbMessageHomeNotFound = "In plugin's options select the custom place for Journal Titles Editor data.";
			lbMessageStopped = "Stopped";
			lbLabelJournalTitlesList = "<html>Items of Journal titles list are editable.<br> Auxiliary line(s) showing \"...\" help to make this editing easier.</html>";
		} 
		else if (str.equals("LT")) {
			lbButtonStart = "START";
			lbButtonAuto = "AUTO";
			lbMenuExtra = "MENIU";
			lbButtonNoDots = "Be ta\u0161k\u0173"; //"Be Ta\u0161k\u0173"
			lbButtonStop = "STOP";
			lbButtonFind = "Rasti";
			lbSearchFromBeginning = "Ie\u0161koti nuo prad\u017Eios";
			lbButtonAccept = "Priimti";
			lbButtonNext = "Sekantis";
			lbMenuItemMark = "Pa\u017Eym\u0117ti";
			lbMenuItemDistinguishMarked = "I\u0161skirti pa\u017Eym.";
			lbMenuItemDistinguishAll = "I\u0161skirti visus";
			lbMenuItemDataGathered = "Sukaupti duomenys";
			lbMenuItemAutoPlus = "AUTO+";
			lbMenuItemEditTitles = "Redaguoti pavadinimus";
			lbBorderSearch = "Paie\u0161ka";
			lbLabelInputJournalTitle = "Duotas \u017Eurnalo pavadinimas:";
			lbBorderResults = "Paie\u0161kos rezultatai";
			lbLabelJournalTitles = "\u017Durnal\u0173 pavadinimai:";
			lbLabelDetails = "Detal\u0117s:";
			lbBorderDataSource = "\u017Durnal\u0173 duomen\u0173 \u0161altinis";
			lbTextNotFound = "Nerasta \\bseries ... \\btitle";
			lbDummyElement = "... ...";
			lbDummyElementA = "...";
			lbDummyElementB = "... ...";
			lbErrorMessageA = "\u017Durnalas nerastas. Klaida 1";
			lbErrorMessageB = "\u017Durnalas nerastas. Klaida 2";
			lbMessageHomeNotFound = "Plugino nustatymuose nurodykite Journal Titles Editor duomen\u0173 viet\u0105.";
			lbMessageStopped = "Sustabdyta";
			lbLabelJournalTitlesList = "<html>\u017Durnal\u0173 pavadinimai yra redaguojami.<br> Pagalbin\u0117s eilut\u0117s su \"...\" palengvina redagavim\u0105.</html>";
		}
	}
}

