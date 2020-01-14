/*
 * JournalTitlesOptionPane.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2019-2020 Z.K.
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


/*
 * An option pane that can be used to configure the JournalTitles plugin.
 */

package journaltitles;

//{{{ Imports
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.jEdit;
//}}}

public class JournalTitlesOptionPane extends AbstractOptionPane {

	private JRadioButton btnInSettings;   // A
	private JRadioButton btnInJEdit;      // B
	private JRadioButton btnSelected;     // Ca
	private JButton buttonBrowse;         // Cb
	private JTextField jrnlTitlesHomePath;

	private String selectedJrnlTitlesHome = "";
	
	private JRadioButton langEN;
	private JRadioButton langLT;

	private JournalTitlesPlugin plugin;
	
	private class CompLabels {
		String lbHomeData;
		String lbHomeDefaultA;
		String lbHomeDefaultB;
		String lbHomeSelected;
		String lbHomeBrowse;
		String lbLanguage;
		String lbHomeDataToolTip;
		String lbLanguageToolTip;
		
		private CompLabels(String str) {
			if (str.equals("EN")) {
				lbHomeData = "Home (Data files):";
				lbHomeDefaultA = "Default A - in settings folder";
				lbHomeDefaultB = "Default B - in jEdit folder";
				lbHomeSelected = "Selected";
				lbHomeBrowse = "Browse";
				lbLanguage = "Language:";
				lbHomeDataToolTip = "Folder where Journal Titles data files are placed";
				lbLanguageToolTip = "GUI language";
			}
			else if (str.equals("LT")) {
				lbHomeData = "Duomenu fail\u0173 vieta:";
				lbHomeDefaultA = "Numatytoji A - nustatym\u0173 kataloge";
				lbHomeDefaultB = "Numatytoji B - jEdit kataloge";
				lbHomeSelected = "Pasirinktasis";
				lbHomeBrowse = "Ie\u0161koti";
				lbLanguage = "Kalba:";
				lbHomeDataToolTip = "Katalogas, kuriame yra Journal Titles duomen\u0173 failai";
				lbLanguageToolTip = "Grafin\u0117s s\u0105sajos kalba";
			}
		}
	}
	
	public JournalTitlesOptionPane() {
		super(JournalTitlesPlugin.NAME);
		plugin = (JournalTitlesPlugin) jEdit.getPlugin("journaltitles.JournalTitlesPlugin");
	}

	protected void _init() {
		
		CompLabels cl = new CompLabels(jEdit.getProperty("options.journaltitles.gui-language"));

		ButtonHandlerA handlerA = new ButtonHandlerA();
		ButtonHandlerB handlerB = new ButtonHandlerB();
		ButtonHandlerC handlerC = new ButtonHandlerC();

		jrnlTitlesHomePath = 
			new JTextField(jEdit.getProperty("options.journaltitles.home"));

		// panelHome
		JPanel panelHome = new JPanel();
		panelHome.setLayout(new BoxLayout(panelHome, BoxLayout.Y_AXIS));

		JPanel panelInSettings = new JPanel();
		JPanel panelInJEdit = new JPanel();
		JPanel panelSelected = new JPanel();

		btnInSettings = new JRadioButton(cl.lbHomeDefaultA);
		BorderLayout layoutA = new BorderLayout();
		panelInSettings.setLayout(layoutA);
		panelInSettings.add(btnInSettings, BorderLayout.LINE_START); 

		btnInJEdit = new JRadioButton(cl.lbHomeDefaultB);
		BorderLayout layoutB = new BorderLayout();
		panelInJEdit.setLayout(layoutB);
		panelInJEdit.add(btnInJEdit, BorderLayout.LINE_START); 

		btnSelected = new JRadioButton(cl.lbHomeSelected);
		buttonBrowse = new JButton(cl.lbHomeBrowse);
		//BorderLayout layoutC = new BorderLayout();
		FlowLayout layoutC = new FlowLayout(FlowLayout.LEADING);
		panelSelected.setLayout(layoutC);
		//panelSelected.add(btnSelected, BorderLayout.LINE_START);
		panelSelected.add(btnSelected);
		panelSelected.add(buttonBrowse);
		
		// button group
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(btnInSettings);
		buttonGroup.add(btnInJEdit);
		buttonGroup.add(btnSelected);
		btnInSettings.addActionListener(handlerA);
		btnInJEdit.addActionListener(handlerB);
		//btnSelected.addActionListener(handlerC);
		buttonBrowse.addActionListener(handlerC);

		panelHome.add(panelInSettings);
		panelHome.add(panelInJEdit);
		panelHome.add(panelSelected);
		panelHome.add(jrnlTitlesHomePath);
		panelHome.setToolTipText(cl.lbHomeDataToolTip);
		
		if (jrnlTitlesHomePath.getText().equals(plugin.homeJteInSettings)) {
			btnInSettings.setSelected(true);
		} else if (jrnlTitlesHomePath.getText().equals(plugin.homeJteInJEdit)) {
			btnInJEdit.setSelected(true);
		} else {
			btnSelected.setSelected(true);
		}
		
		if (plugin.home.equals("settings")) {
			btnInSettings.setSelected(true);
		} else if (plugin.home.equals("jedit")) {
			btnInJEdit.setSelected(true);
		} else if (plugin.home.equals("by-property")) {
			btnSelected.setSelected(true);
		}

		addComponent(cl.lbHomeData, panelHome);
		
		// Languages
		ButtonHandlerEN handlerEN = new ButtonHandlerEN();
		ButtonHandlerLT handlerLT = new ButtonHandlerLT();
		
		langEN = new JRadioButton("EN");
		langLT = new JRadioButton("LT");
		langEN.addActionListener(handlerEN);
		langLT.addActionListener(handlerLT);
		ButtonGroup buttonGroupLang = new ButtonGroup();
		buttonGroupLang.add(langEN);
		buttonGroupLang.add(langLT);
		
		JPanel panelLang = new JPanel();
		panelLang.setLayout(new BoxLayout(panelLang, BoxLayout.Y_AXIS));
		//
		JPanel panelEN = new JPanel();
		BorderLayout layoutEN = new BorderLayout();
		panelEN.setLayout(layoutEN);
		panelEN.add(langEN, BorderLayout.LINE_START);
		//
		JPanel panelLT = new JPanel();
		BorderLayout layoutLT = new BorderLayout();
		panelLT.setLayout(layoutLT);
		panelLT.add(langLT, BorderLayout.LINE_START);
		//
		panelLang.add(panelEN);
		panelLang.add(panelLT);
		panelLang.setToolTipText(cl.lbLanguageToolTip);
		
		if (jEdit.getProperty("options.journaltitles.gui-language").equals("EN")) {
			langEN.setSelected(true);
		} else if (jEdit.getProperty("options.journaltitles.gui-language").equals("LT")) {
			langLT.setSelected(true);
		}
		
		addComponent(cl.lbLanguage, panelLang);
		
	}

	class ButtonHandlerA implements ActionListener {
		// JournalTitle HOME is selected to be within "user" folder
		public void actionPerformed(ActionEvent e) {
			selectedJrnlTitlesHome =
				jEdit.getSettingsDirectory() + File.separator + "plugins" +
				File.separator + "data.journaltitles" + File.separator;
			jrnlTitlesHomePath.setText(selectedJrnlTitlesHome);
			plugin.setJournalTitlesHome(selectedJrnlTitlesHome);
		}
	}

	class ButtonHandlerB implements ActionListener {
		// JournalTitle HOME is selected to be within "jEdit" folder
		public void actionPerformed(ActionEvent e) {
			selectedJrnlTitlesHome =
				jEdit.getJEditHome() + File.separator + "plugins" +
				File.separator + "data.journaltitles" + File.separator;
			jrnlTitlesHomePath.setText(selectedJrnlTitlesHome);
			plugin.setJournalTitlesHome(selectedJrnlTitlesHome);
		}
	}

	class ButtonHandlerC implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			VFSFileChooserDialog dialog = new VFSFileChooserDialog(
				jEdit.getActiveView(),
				System.getProperty("user.dir") + File.separator,
				VFSBrowser.CHOOSE_DIRECTORY_DIALOG,
				false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null && files.length == 1) {
				selectedJrnlTitlesHome = files[0] + File.separator;
				jrnlTitlesHomePath.setText(selectedJrnlTitlesHome);
				plugin.setJournalTitlesHome(selectedJrnlTitlesHome);
			}
		}
	}

	protected void _save() {
		plugin.setJournalTitlesHome(jrnlTitlesHomePath.getText());
	}
	
	// Languages
	class ButtonHandlerEN implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			plugin.setJournalTitlesLang("EN");
		}
	}
	
	class ButtonHandlerLT implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			plugin.setJournalTitlesLang("LT");
		}
	}

}
