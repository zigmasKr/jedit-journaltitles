/*
 * JournalTitlesPlugin.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 * Copyright (C) 2008-2013-2019 Z.K.
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

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Macros;

public class JournalTitlesPlugin extends EditPlugin {
	/** Name for plugin manager */
	public final static String NAME = "Journal Title Editor";
	
	// do all these files are needed for the plugin to start?
	// Jte - mnemonic for Journal Titles Editor
	private static final String[] filesJteData = new String[]{
		"ams-data.txt",
		"annser_short.csv",
		"annser-user-choice.csv",
		"jel_w.csv",
		"jel-data.txt",
		"physrev-data.txt",
		"Springer-export.csv",
		"Springer-user-choice.csv",
		"ucb-data.txt",
		"user-choice.csv"
	};
	
	public static final String settingsJEdit = jEdit.getSettingsDirectory();
	public static final String homeJEdit = jEdit.getJEditHome();

	public static final String homeJteInSettings = settingsJEdit +
		File.separator + "plugins" + File.separator + "journaltitles.data" + File.separator;
	public static final String homeJteInJEdit = homeJEdit +
		File.separator + "plugins" + File.separator + "journaltitles.data" + File.separator;
		
	public String home = "";
	public GuiLabels guLa;
	
	private static boolean findFiles(String[] fileList, String folder) {
		boolean isHere = true;
		for (int i = 0; i < fileList.length; i++) {
			if (!(new File(folder + fileList[i]).isFile())) {
					isHere = false;
			}
		}
		return isHere;
	}
	
	public void setJournalTitlesHome(String path) {
		jEdit.setProperty("options.journaltitles.home", path);
	}
	
	public void setJournalTitlesLang(String lang) {
		jEdit.setProperty("options.journaltitles.gui-language", lang);
	}
	
	public String getJournalTitlesHome() {
		return jEdit.getProperty("options.journaltitles.home");
	}
	
	private void homeNotFound(GuiLabels gg) {
		View cView = jEdit.getActiveView();
		Log.log(Log.ERROR, this, "Journal Titles Editor data files not found.");
		jEdit.setProperty("options.journaltitles.home", "");
		Macros.message(cView, gg.lbMessageHomeNotFound);
	}

	public void start() {
		// Trying to define GUI language:
		if (jEdit.getProperty("options.journaltitles.gui-language") != null) {
			// ok
		} else {
			jEdit.setProperty("options.journaltitles.gui-language", "EN");
		}
		String guiLanguage = jEdit.getProperty("options.journaltitles.gui-language") ;
		guLa = new GuiLabels(guiLanguage);
		
		// Trying to define plugin's home:
		if (jEdit.getProperty("options.journaltitles.home") != null) {
			// if home is supposed to be in a place defined by property
			if (findFiles(filesJteData, jEdit.getProperty("options.journaltitles.home"))) {
				// ok
				home = "by-property";
			} else {
				homeNotFound(guLa);
			}
		} else {	
			// looking for home in 'usual' places:
			if (findFiles(filesJteData, homeJteInSettings)) {
				setJournalTitlesHome(homeJteInSettings);
				home = "settings";
			} else if (findFiles(filesJteData, homeJteInJEdit)) {
				setJournalTitlesHome(homeJteInJEdit);
				home = "jedit";
			} else {
				homeNotFound(guLa);
			}
		}
	}
	
	public void stop() {}

}
