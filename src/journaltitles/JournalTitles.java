/*
 * JournalTitles.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2008-2013 Z.K.
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
 * An application that requires the following files:
 * JournalDataGUI.java
 * JournalData.java
 * WrpString.java
 * ===
 * Mimics Calculator.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.jEdit.*;

@SuppressWarnings("serial")
public class JournalTitles extends JournalDataGUI {
	
	// Main program started
	public static void main(String[] args) {
		JFrame frm = new JFrame();
		JournalTitles jt = new JournalTitles();
		//jt.guiLanguage = "EN";
		frm.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		frm.setContentPane(jt);
		frm.pack();
		frm.setVisible(true);
	}
}

