/*
 * JournalDataGUI.java
 * *
 * Part of the JournalTitles plugin for the jEdit text editor
 *
 * Copyright (C) 2008-2019 Z.K.
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

	/**
	* http://stackoverflow.com/questions/2770321/what-is-a-raw-type-and-why-shouldnt-we-use-it
	*
	* http://stackoverflow.com/questions/18162985/found-raw-type-jcombobox
	* Starting from Java 7 many Swing components use generics,
	* so older code will produce a warning for raw types.
	* For the combobox example you can eliminate the warning
	* if you provide the type of the objects it holds e.g. you should use
	* JComboBox<String> fruits = new JComboBox<>(fruitOptions);
	* if fruitOptions is a String[].
	* If you use some other type change it accordingly.
	*/


package journaltitles;
/*
 * An application that requires the following files:
 * JournalData.java, JournalDataDynamic.java,
 * TxState.java, TxCode.java, ExtString.java, QmString.java
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.DefaultListModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

// debug specific:
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// jEdit specific
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Macros;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.Selection;


@SuppressWarnings("serial")
public class JournalDataGUI
		extends JPanel 
		implements WindowConstants {

	// __version and __date:
	//static String __version = "ver. 0.9.1";
	//static String __date    = "2008.09.15";
	// ...
	static String __version = "ver. 0.9.5";
	static String __date    = "2019.12.30";

	//A border that puts 10 extra pixels at the sides and bottom of each pane:
	Border paneEdge = BorderFactory.createEmptyBorder(0,30,30,30);

	JournalDataDynamic jdd;
	JButton buttonStart;
	JToggleButton buttonAuto;
	JToggleButton buttonNoDots;
	JMenu menuExtra;
	JButton buttonStop;
	JTextField textfInputJournalTitle;
	final JCheckBox checkboxFromBeginning;
	JButton buttonFind;
	JButton buttonAccept;
	JButton buttonNext;
	JComboBox<String> comboboxDataSource;
	JScrollPane titlesScroller;
	JScrollPane detailsScroller;
	MutableList mlistTitles;
	MutableList	mlistDetails;
	// Smaller panels:
	JPanel panelStartStop;
	JPanel panelSearch;
	JPanel panelResults;
	JPanel panelDataSource;
	//
	JEditBuffer currentBuffer;
	QmString qms = new QmString();
	// The object qms allows the access to methods of QmString 
	// and is used as a placeholder of values of type QmString.
	
	String guiLanguage = jEdit.getProperty("options.journaltitles.gui-language");
	GuiLabels gl = new GuiLabels(guiLanguage);
	// The list of labels etc. introduced.
	
	String jrnlTitle = "";
	boolean isJrnlTitle = false;
	int jrnlTitleStart = 0;
	int jrnlTitleEnd = 0;
	int lookupStart;
	//
	Selection selectionCurrentJrnlTitle;
	ArrayList<int[]> jrnlTitlesFoundAll;
	ArrayList<int[]> jrnlTitlesMarked;
	//
	boolean started;
	// Is "START" already pushed.
	boolean allowedButtonAccept;
	boolean allowedButtonFind;
	boolean goOnAuto;
	boolean goOnAutoPlus;
	JMenuItem menuitemAutoPlus = new JMenuItem(gl.lbMenuItemAutoPlus);
	String previousInputTitle;
	// Action @edit and ListAction @listAction are two variables 
	// which help to implement the editing of proposed 
	// journal titles before some of them is accepted.
	Action edit;
	ListAction listAction;
		/** == PLUGIN related: BEGIN ==
		* In this case View currentView is needed for the working algorithm.
		* int close_operation and View currentView
		* become components of the object JournalDataGUI.
		*
		* Related to the PLUGIN programming:
		* the close operation, added this for jEdit plugin so closing the [plugin]
		* doesn't close jEdit.
		*/
	private int close_operation = EXIT_ON_CLOSE;
	private View currentView = jEdit.getActiveView();
		/**
		* In CalculatorPanel.java this method is a class method,
		* being outside of any block.
		* ===
		* From CalculatorPanel.java:
		* Sets the closeOperation attribute of the JournalTitleChangePanel object
		
		* @param operation  The new closeOperation value, one of DISPOSE_ON_CLOSE,
		* DO_NOTHING_ON_CLOSE, EXIT_ON_CLOSE, or HIDE_ON_CLOSE.
		*/
	public void setCloseOperation(int operation) {
		switch (operation) {
			case DISPOSE_ON_CLOSE:
			case DO_NOTHING_ON_CLOSE:
			case EXIT_ON_CLOSE:
			case HIDE_ON_CLOSE:
				break;
			default:
				throw new IllegalArgumentException("Invalid close operation, see javax.swing.WindowConstants.");
		}
		close_operation = operation;
	}
		/**
		* From CalculatorPanel.java:
		* Gets the closeOperation attribute of the
		* JournalTitleChangePanel object

		* @return   The closeOperation value, one of DISPOSE_ON_CLOSE,
		* DO_NOTHING_ON_CLOSE, EXIT_ON_CLOSE, or HIDE_ON_CLOSE.
		*/
	public int getCloseOperation() {
		return close_operation;
	}
		/** == PLUGIN related: END == */

	// FOR TESTING:
	//public String msg;

	/** == End of class variables == */

	/** Class object constructor */
	public JournalDataGUI() {
		/* The main panel [this] which contains other panels:
		// panelMain = new JPanel();  // JFrame->JPanel
		// panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS)); // JFrame->JPanel
		*/
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		/*
		* BoxLayout arranges components either horizontally or vertically
		* in a panel. You can control alignment and spacing of the components.
		* Complicated layouts can be made by combining many panels, some with
		* horizontal layout and some with vertical layouts.
		* Unlike other layouts, the panel/container must be passed
		* to the BoxLayout constructor.
		*/
		//
		jdd = new JournalDataDynamic();
		/* This new JournalDataDynamic(); creates and rolls out
		new JournalData(), which is initiated with "SpringerDB:SPR".
		Later comboboxDataSource.setSelectedIndex(0) does the same once again.
		Therefore comboboxDataSource.setSelectedIndex(0) is commented out - 2014-01-28,
		see below.
		*/
		/** GUI components: */
		buttonStart = new JButton(gl.lbButtonStart);
		buttonAuto = new JToggleButton(gl.lbButtonAuto);
		menuExtra = new JMenu(gl.lbMenuExtra);
		buttonNoDots = new JToggleButton(gl.lbButtonNoDots);
		buttonStop = new JButton(gl.lbButtonStop);
		buttonStart.addActionListener(new buttonStartActionListener());
		buttonStop.addActionListener(new buttonStopActionListener());
		buttonAuto.addActionListener(new buttonAutoActionListener());
		buttonNoDots.addActionListener(new buttonNoDotsActionListener());
		//
		textfInputJournalTitle = new JTextField(30);
		buttonFind = new JButton(gl.lbButtonFind);
		buttonFind.addActionListener(new buttonFindActionListener());
		// local variable checkboxFromBeginning is accessed from within inner class;
		// needs to be declared final; see the list of class variables list above
		checkboxFromBeginning = new JCheckBox(gl.lbSearchFromBeginning, true);
		checkboxFromBeginning.addActionListener(new ActionListener() {
			/*
			* Use anonymous inner classes to handle button events.
			* Note how addActionListener is introduced:
			* ===
			* One advantage to using an anonymous inner class is that the component
			* that invokes the class'es methods is already known. There is no need
			* to call getActionCommand( ), for example, to determine what button
			* generated the event because each implementation of actionPerformed( )
			* is associated with only one button, the one that generated the event.
			* [Herb Schildt's Java Programming Cookbook. p. 390.]
			*/
			public void actionPerformed(ActionEvent ae) {
				if (checkboxFromBeginning.isEnabled()) {
						// depending on the status of JCheckBox after click:
					jdd.matchBeginning = checkboxFromBeginning.isSelected();
				}
			}
		});
		//
		buttonAccept = new JButton(gl.lbButtonAccept);
		buttonNext = new JButton(gl.lbButtonNext);
		buttonAccept.addActionListener(new buttonAcceptActionListener());
		buttonNext.addActionListener(new buttonNextActionListener());
		mlistTitles = new MutableList();
		mlistDetails = new MutableList();
		mlistTitles.addListSelectionListener(new mlistTitlesListSelectionListener());
		mlistTitles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mlistTitles.setLayoutOrientation(JList.VERTICAL);
		mlistTitles.setVisibleRowCount(8);
		// edit action:
		edit = new EditListAction();
		listAction = new ListAction(mlistTitles, edit);
		//
		mlistDetails.setVisibleRowCount(8);
		mlistDetails.setLayoutOrientation(JList.VERTICAL);
			//titlesList.setFixedCellHeight(cellHeight);
		titlesScroller = new JScrollPane(mlistTitles);
		titlesScroller.setPreferredSize(new Dimension(200, 100));
		titlesScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		titlesScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// There is no sense to select anything in the list of details:
		detailsScroller = new JScrollPane(mlistDetails);
		detailsScroller.setPreferredSize(new Dimension(200, 150));
			//detailsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			//detailsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		detailsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//
		comboboxDataSource = new JComboBox<>(jdd.arrayOfDataSources);
		comboboxDataSource.addActionListener(new comboboxDataSourceActionListener());
		////comboboxDataSource.setSelectedIndex(0);
		//
		// Variables related to the algorithm of search and change in the text:
		// (currentView comes from jEdit)
		currentBuffer = currentView.getTextArea().getBuffer();
		lookupStart = 0;
		selectionCurrentJrnlTitle = null;
		jrnlTitlesFoundAll = new ArrayList<int[]>();
		jrnlTitlesMarked = new ArrayList<int[]>();
		//
		started = false;
		allowedButtonAccept = false;
		allowedButtonFind = true;
		goOnAuto = false;
		goOnAutoPlus = false;
		previousInputTitle = "";
		//
		panelStartStop = createPanelStartStop();
		panelSearch = createPanelSearch();
		panelResults = createPanelResults();
		panelDataSource = createPanelDataSource();
		// "Start-Stop" component (Panel) is added:
		add(panelStartStop);
		// "Search" component (Panel) is added:
		add(panelSearch);
		// "Results" component (Panel) is added:
		add(panelResults);
		// "DataSource" component (Panel) is added:
		add(panelDataSource);
	}

	/* ===
	* Functions that are parts of the constructor method:
	* ===
	*/
	// === Panel Top: top ===
	private JPanel createPanelStartStop() {
		JPanel panelTop = new JPanel();
		GridLayout panelTopGridLayout = new GridLayout(1,5);
		// START, AUTO, EXTRA, NoDots, STOP
		panelTop.setLayout(panelTopGridLayout);
		// panelTop.setLayout(new BorderLayout());
		//JPanel panelStartAuto = new JPanel();
		//panelStartAuto.setLayout(new BoxLayout(panelStartAuto, BoxLayout.X_AXIS));
		//panelStartAuto.add(buttonStart);
		panelTop.add(buttonStart);
		//panelStartAuto.add(buttonAuto);
		panelTop.add(buttonAuto);
		JPanel panelExtraMenu = createExtraMenuPanel();
		//panelStartAuto.add(panelExtraMenu);
		panelTop.add(panelExtraMenu);
		//panelStartAuto.add(buttonNoDots);
		panelTop.add(buttonNoDots);
		//
		//panelTop.add(panelStartAuto, BorderLayout.WEST);
		//panelTop.add(buttonStop, BorderLayout.EAST);
		panelTop.add(buttonStop);
		// Border:
		Border raisedetchedStartStop =
			BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.green, Color.black);
		TitledBorder raisedetchedStartStopT =
			BorderFactory.createTitledBorder(raisedetchedStartStop, "");
		//
		panelTop.setBorder(paneEdge);
		panelTop.setBorder(raisedetchedStartStopT);
		return panelTop;
	}

	// === Extra Menu Panel
	private JPanel createExtraMenuPanel() {// implements ActionListener {
		// ME = Menu Extra
		JPanel panelME = new JPanel();
		panelME.setLayout(new BorderLayout());
			//panelME.setLayout(new FlowLayout());
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuExtra);
		JMenuItem menuitemMark = new JMenuItem(gl.lbMenuItemMark);
		JMenuItem menuitemDistinguishMarked = new JMenuItem(gl.lbMenuItemDistinguishMarked);
		JMenuItem menuitemDistinguishAll = new JMenuItem(gl.lbMenuItemDistinguishAll);
		JMenuItem menuitemDataGathered = new JMenuItem(gl.lbMenuItemDataGathered);
		JMenuItem menuitemEditTitles = new JMenuItem(gl.lbMenuItemEditTitles);
		menuitemAutoPlus.setBackground(Color.GRAY);
		menuExtra.add(menuitemMark);
		//menuExtra.add(menuitemDistinguishMarked);
		menuExtra.add(menuitemDistinguishAll);
		menuExtra.addSeparator();
		menuExtra.add(menuitemDataGathered);
		menuExtra.addSeparator();
		menuExtra.add(menuitemEditTitles);
		menuExtra.addSeparator();
		menuExtra.add(menuitemAutoPlus);
		menuitemMark.addActionListener(new menuitemMarkListener());
		menuitemDistinguishAll.addActionListener(new menuitemDistinguishAllListener());
		menuitemDataGathered.addActionListener(new menuitemDataGatheredListener());
		menuitemEditTitles.addActionListener(new menuitemEditTitlesListener());
		menuitemAutoPlus.addActionListener(new menuitemAutoPlusListener());
		//
		panelME.add(menuBar, BorderLayout.CENTER);
		return panelME;
	}

	// === Panel A: "Search" ===
	private JPanel createPanelSearch() {
		JPanel panelA = new JPanel();
		panelA.setLayout(new BoxLayout(panelA, BoxLayout.Y_AXIS));
		// Inner panels:
		panelA.add(createPanelAA());
		panelA.add(createPanelAB());
		// Border:
		Border raisedetchedSearch =
			BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.orange, Color.black);
		TitledBorder raisedetchedTitledborderSearch =
			BorderFactory.createTitledBorder(raisedetchedSearch, gl.lbBorderSearch);
		//
		panelA.setBorder(paneEdge);
		panelA.setBorder(raisedetchedTitledborderSearch);
		return panelA;
	}
	//
	private JPanel createPanelAA() {
		// Makes JPanel where "Input journal title" lives,
		// with the input journal title area.
		JPanel panelAA = new JPanel();
		//panelAA.setLayout(new BoxLayout(panelAA, BoxLayout.Y_AXIS));
		panelAA.setLayout(new BorderLayout());
		// In this way, the label text is placed more accurately:
		panelAA.add(new JLabel(gl.lbLabelInputJournalTitle), BorderLayout.WEST);
		// If =static=, then "New Floating Instance" works badly:
					// textfInputJournalTitle = new JTextField(30);
		panelAA.add(textfInputJournalTitle, BorderLayout.SOUTH);
		return panelAA;
	}
	//
	private JPanel createPanelAB() {
		// Makes JPanel where the JButton "Find" and
		// the JCheckbox "Search from beginning" live.
		JPanel panelAB = new JPanel();
		//panelAB.setLayout(new BoxLayout(panelAB, BoxLayout.X_AXIS));
		panelAB.setLayout(new BorderLayout());
		panelAB.add(checkboxFromBeginning, BorderLayout.WEST);
		panelAB.add(buttonFind, BorderLayout.EAST);
		return panelAB;
	}

	// === Panel B: Results ===
	private JPanel createPanelResults() {
		/*
		JPanel "Results". Comprises of four smaller panels:
		BA: for JLabel "Zurnalu pavadinimai" and JButton "Accept";
		BB: for 'matching' journal titles (JList);
		BC: for JLabel "Detales";
		BD: for the details of the chosen journal title (JList).
		*/
		JPanel panelB = new JPanel();
		panelB.setLayout(new BoxLayout(panelB, BoxLayout.Y_AXIS));
		// Border:
		Border raisedetchedResults = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.yellow, Color.black);
		TitledBorder raisedetchedTitledborderResults =
			BorderFactory.createTitledBorder(raisedetchedResults, gl.lbBorderResults);
		panelB.setBorder(paneEdge);
		panelB.setBorder(raisedetchedTitledborderResults);
		// Inner panels:
		panelB.add(createPanelBA());
		panelB.add(createPanelBB());
		panelB.add(createPanelBC());
		panelB.add(createPanelBD());
		return panelB;
	}
	//
	private JPanel createPanelBA() {
		// JLabel "Journal titles" and JButton "Accept"
		JPanel panelBA = new JPanel();
		panelBA.setLayout(new BorderLayout());
		Box boxSS = Box.createHorizontalBox();
		JLabel labelJournalTitlesList = new JLabel(gl.lbLabelJournalTitles);
		labelJournalTitlesList.setToolTipText(gl.lbLabelJournalTitlesList);
		panelBA.add(labelJournalTitlesList, BorderLayout.WEST);
		boxSS.add(buttonAccept);
		boxSS.add(Box.createHorizontalStrut(5));
		boxSS.add(buttonNext);
		panelBA.add(boxSS, BorderLayout.EAST);
		return panelBA;
	}
	//
	private JPanel createPanelBB() {
		// JPanel where 'matching' journal titles are listed.
		JPanel panelBB = new JPanel();
		panelBB.setLayout(new BoxLayout(panelBB, BoxLayout.Y_AXIS));
		// Set the item width
		int cellWidth = 300;
		// Set the item height
		int cellHeight = 18;
		panelBB.add(titlesScroller);
		return panelBB;
	}
	//
	private JPanel createPanelBC() {
		// JLabel "Details"
		JPanel panelBC = new JPanel();
		panelBC.setLayout(new BorderLayout());
		panelBC.add(new JLabel(gl.lbLabelDetails), BorderLayout.WEST);
		return panelBC;
	}
	//
	private JPanel createPanelBD() {
		// JPanel where the details of the chosen journal title are shown.
		JPanel panelBD = new JPanel();
		panelBD.setLayout(new BoxLayout(panelBD, BoxLayout.Y_AXIS));
		// Set the item width
		int cellWidth = 300;
		// Set the item height
		int cellHeight = 18;
		panelBD.add(detailsScroller);
		return panelBD;
	}

	// === Panel C: data source ===
	private JPanel createPanelDataSource() {
		// Panel shows the data source -- the collection 
		// where journal titles are searched.
		JPanel panelC = new JPanel();
		panelC.setLayout(new BoxLayout(panelC, BoxLayout.Y_AXIS));
		//Border:
		Border raisedetchedDataSource = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.green, Color.black);
		TitledBorder raisedetchedTitledborderDataSource =
			BorderFactory.createTitledBorder(raisedetchedDataSource, gl.lbBorderDataSource);
		panelC.setBorder(paneEdge);
		panelC.setBorder(raisedetchedTitledborderDataSource);
		panelC.add(comboboxDataSource);
		return panelC;
	}
	
	// === Utilities
	
	public boolean readJournalTitleNext(String str, int start) {
		// Finds \bseries... \bissue... \btitle.
		boolean ifFound = false;
		qms = qms.tccGetBissueBtitleArg(str, start);
		if (qms.qstr.length() > 1) {
			ifFound = true;
			jrnlTitle = qms.qstr.substring(1, qms.qstr.length() - 1);
			jrnlTitleStart = qms.qstart + 1;
			// Actually, jrnlTitleEnd = qms.qend - 1, but for selections 
			// this value works (otherwise, we should take jrnlTitleEnd + 1):
			jrnlTitleEnd = qms.qend;
			// Actions when bseries-bissue-btitle is found:
			currentView.getTextArea().scrollTo(jrnlTitleStart, true);
			selectionCurrentJrnlTitle = new Selection.Range(jrnlTitleStart, jrnlTitleEnd);
			currentView.getTextArea().addToSelection(selectionCurrentJrnlTitle);
			textfInputJournalTitle.setText(jrnlTitle);	
			// the original journal title is saved:
			jdd.inputTitleOrig = jrnlTitle;
			lookupStart = jrnlTitleStart;
			jrnlTitlesFoundAll.add(new int[]{jrnlTitleStart, jrnlTitleEnd});
		}
		return ifFound;
	}

	// =================
	// === Menu Item Listeners ===
	
	public class menuitemMarkListener
		implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem)(ae.getSource());
				//if (started) {
					if (source.getText().equals(gl.lbMenuItemMark)) {
						// the text selection is arranged
						currentView.getTextArea().addToSelection(new Selection.Range(jrnlTitleStart, jrnlTitleEnd));
						// the marker is added in the actual position
						currentView.getTextArea().scrollTo(jrnlTitleEnd, true);
						currentView.getBuffer().addMarker('c', jrnlTitleEnd);
					}
				//}
			}
		}
	
	public class menuitemDistinguishAllListener
		implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem)(ae.getSource());
				//if (started) {
				//Macros.message(currentView, "source.getText(): " + source.getText() + "\n");
						// getBuffer() [of view] - Returns the current edit pane's buffer.
						// getTextArea() [of view] - Returns the current edit pane's text area.
					if (source.getText().equals(gl.lbMenuItemDistinguishAll)) {
						int sels = jrnlTitlesFoundAll.size();
						//Macros.message(currentView, "selections: " + selections + "\n");
						for (int s = 0; s < sels; s++) {
							int start = jrnlTitlesFoundAll.get(s)[0];
							int end = jrnlTitlesFoundAll.get(s)[1];
							//Macros.message(currentView, "sel end: " + end + "\n");
							currentView.getTextArea().scrollTo(end, true);
							currentView.getBuffer().addMarker('b', end);
							currentView.getTextArea().addToSelection(new Selection.Range(start, end));
						}
					}
				//}
			}
		}
	
	public class menuitemDataGatheredListener
		implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem)(ae.getSource());
				if (started) {
					if (source.getText().equals(gl.lbMenuItemDataGathered)) {
						jdd.loadUserChoiceData();
					}
				}
			}
		}
		
	public class menuitemEditTitlesListener
		implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem)(ae.getSource());
				if (started) {
					// main activities are stopped:
					actionOnStop();
				}
				// opens user-choice file (of journal titles) for editing:
				jEdit.openFile(currentView, jdd.jd.jdUserChoicePath);
			}
		}	
		
	public class menuitemAutoPlusListener
		implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem)(ae.getSource());
				if (!goOnAutoPlus) {
					goOnAutoPlus = true;
					source.setBackground(Color.GREEN); 
				} else {
					goOnAutoPlus = false;
					source.setBackground(Color.GRAY); 
				}
			}
		}	
	
	public class buttonStartActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			JButton button = (JButton) aevt.getSource();
			if (button.isEnabled()) {
				actionOnStart();
			}
		}
	}

	// === START ===
	public void actionOnStart() {
		started = true;
		allowedButtonFind = true;
		jrnlTitlesFoundAll.clear();
		jrnlTitlesMarked.clear();
		// currentBuffer is the jEdit object
		currentBuffer = currentView.getTextArea().getBuffer();
		int bufferLength = currentBuffer.getLength();
		String strBuffer = currentBuffer.getText(0, bufferLength);
		
		/*
		When the plugin is "floating", the cursor moves [Caret]
		make the editor window active, and the plugin window hides.
		Therefore we will refuse of the following actions (they make the editor window active):
			currentView.getTextArea().moveCaretPosition(txB.txFirst);
			currentView.getTextArea().scrollAndCenterCaret();
		*/
		
		mlistDetails.getContents().removeAllElements();
		isJrnlTitle = readJournalTitleNext(strBuffer, lookupStart);
		if (isJrnlTitle) {
		}
		else {
			mlistDetails.getContents().addElement(gl.lbTextNotFound);
		}
		// The string is initiated, where "user-choice" will be recorded:
		jdd.userChoiceDataAccumulator = "";
		jdd.rollUserChoiceFile();
		// TRACING:
		//jdd.tracingAccumulator = "\n=== START TRACING ===\n\n";
		//jdd.rollTracingFile();
	}
	
	// === "Find"
	public class buttonFindActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			String acR = aevt.getActionCommand();
			// See which button was pressed.
			if(acR.equals(gl.lbButtonFind)) {
				if(buttonFind.isEnabled()) {
					//jdd.inputTitle = textfInputJournalTitle.getText();
					actionOnFind();
				}
			}
		}
	}
	//
	public void actionOnFind() {
		jdd.inputTitle = textfInputJournalTitle.getText();
		// if textfInputJournalTitle.getText() is changed, then one should
		// be allowed to do another search:
		if (!allowedButtonFind &&
			!previousInputTitle.equals(jdd.inputTitle)) {
			allowedButtonFind = true;
		}
		if(allowedButtonFind) {
			// to prevent the repeated use of buttonFind;
			// the button buttonFind can be used again 
			// only if buttonNext has been used, or after the change of the "input title"
			if (!jdd.inputTitle.equals("")) {
				jddRollActionsForDataMatched();
				// With the new data, mlistTitles and mlistDetails 
				// are rearranged.
				if (mlistTitles.getContents().getSize() != 0) {
					mlistTitles.removeSelectionInterval(jdd.indexTitleSelected, jdd.indexTitleSelected);
					mlistTitles.getContents().removeAllElements();
				}
				mlistDetails.getContents().removeAllElements();
				if (jdd.jnTitlesMatched.size() > 0) {
					for (int t = 0; t < jdd.jnTitlesMatched.size(); t++) {
						mlistTitles.getContents().addElement(jdd.jnTitlesMatched.get(t));
					}
					// if there is only one 'match', then the additional dummy list entry is added
					// to enable the editing of this one match
					if (jdd.jnTitlesMatched.size() == 1) {
						mlistTitles.getContents().addElement(gl.lbDummyElement);
					}
				}
				else {
					mlistTitles.getContents().removeAllElements();
					// if there are no matches, then two additional dummy list entries are added
					// to enable the editing/insertion of the output journal title
					mlistTitles.getContents().addElement(gl.lbDummyElementA);
					mlistTitles.getContents().addElement(gl.lbDummyElementB);
					mlistDetails.getContents().addElement(gl.lbErrorMessageA);
				}
			}
			else {
				mlistTitles.getContents().removeAllElements();
				mlistDetails.getContents().removeAllElements();
				mlistTitles.getContents().addElement(gl.lbDummyElementA); // to enable editing/insertion of the output journal title
				mlistTitles.getContents().addElement(gl.lbDummyElementB);
				mlistDetails.getContents().addElement(gl.lbErrorMessageB);
			}
		}
		previousInputTitle = jdd.inputTitle;
		allowedButtonFind = false;
		//allowedButtonAccept = false;
	}

	private void jddRollActionsForDataMatched() {
		// Actions that are usually done when 
		// button "Find" is pressed:
		jdd.rolljnDataMatched();
		/**
		* This point renders to jdd.jd.jdMatchingJournalsData,
		* jd.jdData, jd.jdDataASCII are involved into calculations;
		* the result is of type ArrayList<ArrayList<String>>, consisting of:
		* position [0]: canonic abbrev. version of the journal title,
		* position [1]: one of =REGEX=, =FOUND=, =EXACT=
		* position [2]: DB user
		* position [3]: ISSN
		* position [4] and further ones - other versions of the journal title
		* jdd.jnUserData is appended with pressing the button buttonAccept,
		* when the version of the journal title and related details are chosen.
		* Now one can calculate jdd.UserDataMatched:
		*/

		//Macros.message(currentView, jdd.jd.tracingJdData);
		//jdd.tracingAccumulator = jdd.jd.tracingJdData;
		//jdd.writeAndCloseTracingFile();

		jdd.rolljnUserDataMatched();
		jdd.rolljnAllDataMatched();
		jdd.rolljnTitlesMatched();
	}

	// === "Accept"
	public class buttonAcceptActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			//JButton button = (JButton) aevt.getSource();
			String acP = aevt.getActionCommand();
			// See which button was pressed.
			if(acP.equals(gl.lbButtonAccept)) {
				if (buttonAccept.isEnabled()) {
					actionOnAccept();
				}
			}
		}
	}
	/**
	public void setSelectedText(Selection s, java.lang.String selectedText)
	Replaces the selection with the specified text.
	Parameters: s - The selection, selectedText - The new text
	===
	public void setSelectedText(java.lang.String selectedText)
	Replaces the selection at the caret with the specified text.
	If there is no selection at the caret, the text is inserted
	at the caret position.
	*/
	//
	public void actionOnAccept() {
		// @selectedTitle should come from the chosen item in the list mlistTitles
		// mlistTitlesListSelectionListener
			//Macros.message(currentView, "jdd.selectedTitle: " + jdd.selectedTitle + "\n");
		//String userChoiceControl = "";
		if (started && allowedButtonAccept) {
			if (!jdd.selectedTitle.equals("")) {
				jdd.makeSelectedTitleOutput(); // with this, the Vancouver mode is realized
				currentView.getTextArea().setSelectedText(selectionCurrentJrnlTitle, jdd.selectedTitleOutput);
				/**
				* Each change is put down into the array JournalData (in memory).
				* At the same time, the record is made ready to write into the separate "user-choise" data file.
				* In this way, the possibilities of choice of journal titles should be growing during the work session.
				* Data are written into memory in the same format as that for records made from the data file.
				* That is, records are put into userChoiceData in the same format as in ...MatchingJournalsData:
				*/
				jdd.appendUserData();
				//
				//jdd.appendTracingAccumulator("Accept");
				// Control:
				//Macros.message(currentView, "jdd.jnUserData.size() = " + jdd.jnUserData.size() + "\n");
				//Macros.message(currentView, jdd.userChoiceTrace);
				jdd.appendUserDataForFile();
				allowedButtonAccept = false;
			}
			// to prevent the repeated pressing of button (hand tremor):
			jdd.selectedTitle = "";
		}
	}

	// === "Next"
	public class buttonNextActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			JButton button = (JButton) aevt.getSource();
			if (button.isEnabled()) {
				if (started) {
					if (!goOnAuto) {
						actionOnNext();
					} else {
						actionOnNextAuto();
					}
				}
			}
		}
	}

	/**
	* ===
	* Remarks on the algorithm for actionOnNext.
	* When the plugin is "floating", the cursor moves [Caret]
	* make the editor window active, and the plugin window hides.
	* Therefore we will refuse of the following actions (they make the editor window active):
		currentView.getTextArea().moveCaretPosition(txB.txFirst);
		currentView.getTextArea().scrollAndCenterCaret();
	* If the pressing of "Next" is not preceeded by pressing "Accept",
	* it means that nathing was changed in the preceeding step.
	* Therefore, the journal title found in the preceeding step 
	* could be removed from the "text area" @Selection:
	* 		currentView.getTextArea().removeFromSelection(selectionCurrentJrnlTitle);
	* But in case when the option "NoDots" is used,
	* formally, (almost) all journal titles are changed.
	* Therefore, in a simple case this episode could be omitted.
	* But in case of the option goOnAuto this episode must be omitted.   ???
	* ===
	*/
	//
	public void actionOnNext() {
		// After an eventual change in the text the buffer length may change:
		int bufferLength = currentBuffer.getLength();
		String strBuffer = currentBuffer.getText(0, bufferLength);
		isJrnlTitle = readJournalTitleNext(strBuffer, lookupStart);
		if (isJrnlTitle) {
			mlistTitles.getContents().removeAllElements();
			mlistDetails.getContents().removeAllElements();
			jdd.selectedTitle = "";
		}
		// when \bissue...\bititle is (no more) found:
		else {
			// finalizing actions:
			mlistTitles.getContents().removeAllElements(); // is it good?
			mlistDetails.getContents().removeAllElements();
			mlistDetails.getContents().addElement(gl.lbTextNotFound);
			// the button "Find" is 'stopped':
			textfInputJournalTitle.setText("");
		}
		allowedButtonAccept = false;
		// When all activities bounded to buttonNext are done,
		// buttonFind is allowed:
		allowedButtonFind = true;
	}
	
	public void actionOnNextAuto() {
		// Action on 'Next', when 'AUTO' is activated.
		/**
		* With the AUTO enabled, mlistTitles and mlistDetails are not appended.
		* Conditions are checked on what can be done with findings made by
		* actionOnFind(), and if the AUTO loop can be continued.
		* While the continuation condition is satisfied (localGoOnAuto @true),
		* the function of the button "Find" is simulated, and the button "Accept" is not pressed:
		* "Accept" is either not needed or it is simulated also.
		* ===
		* localGoOnAuto (continuation) conditions.
		* Three levels of 'matching' are possible:
		* [A] When (only) =EXACT= matching(s) is/are available.
		* If any matching(s) is/are found, and for any of them 
		* the matching level is not =EXACT=, then
		* the continuation condition [A] is not satisfied.
		* There can be 2 candidates of level =EXACT=, one of them, say, with DB user: =user choice=,
		* and another with a real DB user. Then it is not important which candidate is accepted.
		
		* Due to possible editing of journal titles being accepted (editing the list of titles)
		* a situation can emerge when several titles match, 
		* but one of them is of level =EXACT=.
		* Then this title is just accepted, hence nothing is to be changed.
		* Such title coincides with that in the field "Input journal title".
		* [B]
		* There can be a (one) candidate of level =ACCEPTED=.
		* There can be a unique candidate of level =FOUND=.
		* There can be a unique candidate of level =REGEX=.
		*
		* Level =REGEX= : if there are more than one match of this level, the user should decide
		* which candidate is to be accepted, hence AUTO is stopped.
		*/
		String screenMessage = "";
		// local variable to monitor "goOnAuto":
		boolean localGoOnAuto;
		//
		localGoOnAuto = false; // initial state
		do {
			allowedButtonAccept = true;
			// After an eventual change in the text the buffer length may change:
			int bufferLength = currentBuffer.getLength();
			String strBuffer = currentBuffer.getText(0, bufferLength);
			isJrnlTitle = readJournalTitleNext(strBuffer, lookupStart);
			if (isJrnlTitle) {
				// lists are cleaned:
				mlistTitles.getContents().removeAllElements();
				mlistDetails.getContents().removeAllElements();
				jdd.selectedTitle = "";
				// textfInputJournalTitle gets the journal title in readJournalTitleNext(...)
				jdd.inputTitle = textfInputJournalTitle.getText();
				jddRollActionsForDataMatched();
				if (jdd.jnTitlesMatched.size() > 0) {
					if (jdd.jd.jdSource.equals("SerialsDB:SPR")) {
						if (goOnAutoPlus) {
							screenMessage = "# matched titles: ";
							screenMessage = screenMessage + jdd.jnAllDataMatched.size() + "\n";
							for (int x = 0; x < jdd.jnAllDataMatched.size(); x++) {
								screenMessage = screenMessage + "\n" + "Title: " + jdd.jnAllDataMatched.get(x).get(0);
								screenMessage = screenMessage + "\n" + "Level: " + jdd.jnAllDataMatched.get(x).get(1);
							}
						}
						// The continuation condition [A] here:
						localGoOnAuto = true;
						for (int x = 0; x < jdd.jnAllDataMatched.size(); x++) {
							if (!jdd.jnAllDataMatched.get(x).get(1).equals("=EXACT=")) {
								localGoOnAuto = false;
							}
						}
						if (localGoOnAuto) {
							jdd.selectedTitle = jdd.jnTitlesMatched.get(0);
							if (goOnAutoPlus) {
								screenMessage = screenMessage + "\n\n" + "Selected: " + jdd.selectedTitle;
								Macros.message(currentView, screenMessage);
							}
							mlistDetails.getContents().removeAllElements();
							mlistDetails.getContents().addElement("Selected: " + jdd.selectedTitle);
							actionOnAccept();
						}
						// [B] If there were no success with =EXACT=,
						// one tries with the levels =FOUND=, =ACCEPTED=, =REGEX=.
						// If localGoOnAuto == true due to coincidence of level =EXACT= (condition [A]),
						// the following epizode is omitted.
						// If localGoOnAuto == false, there may be new possibilities:
						if (!localGoOnAuto) {
							if ((jdd.jnTitlesMatched.size() == 1) &&
								(jdd.jnAllDataMatched.get(0).get(1).equals("=FOUND=") ||
								 jdd.jnAllDataMatched.get(0).get(1).equals("=ACCEPTED=") ||
								 jdd.jnAllDataMatched.get(0).get(1).equals("=REGEX="))) {
								// The unique suggestion is accepted, and localGoOnAuto = true;
								// One has to produce selection of this unique element of the list,
								// its acceptance, and moving to the next \bissue\btitle
								jdd.selectedTitle = jdd.jnTitlesMatched.get(0);
								if (goOnAutoPlus) {
									screenMessage = screenMessage + "\n\n" + "Selected: " + jdd.selectedTitle;
									Macros.message(currentView, screenMessage);
								}
								mlistDetails.getContents().removeAllElements();
								mlistDetails.getContents().addElement("Selected: " + jdd.selectedTitle);
								actionOnAccept();
								localGoOnAuto = true;
							}
							else {
								if (goOnAutoPlus) {
									screenMessage = screenMessage + "\n\n" + "Selected title: " + "NONE";
									Macros.message(currentView, screenMessage);
								}
								mlistDetails.getContents().removeAllElements();
								mlistDetails.getContents().addElement("Selected: " + "NONE");
								localGoOnAuto = false;
							}
						}
						// If we leave this block with localGoOnAuto == true,
						// then we continue with the new run through the while loop from its start,
						// where a next \bissue\btitle is searched.
					}
					else if (jdd.jd.jdSource.equals("AMS")) {
						// In the case of "AMS" a simple condition is sufficient: there should be only one
						// coincidence. This condition could be elaborated.
						if (jdd.jnTitlesMatched.size() == 1) {
							localGoOnAuto = true;
						}
						else {
							localGoOnAuto = false;
						}
					}
				}   // the end of jdd.jnTitlesMatched.size() > 0 branch
				else {
					// If jdd.jnTitlesMatched.size() == 0:
					localGoOnAuto = false;
				}
			}
			// if \bissue\btitle is no more found:
			else {
				localGoOnAuto = false;
				// Without this we fall into endless loop ;)
				// Actions when bissue-btitle is no more found:
				mlistTitles.getContents().removeAllElements(); // is this good?
				mlistDetails.getContents().removeAllElements();
				mlistDetails.getContents().addElement(gl.lbTextNotFound);
				allowedButtonAccept = false;
				// the button "Find" is 'stopped':
				textfInputJournalTitle.setText("");
			}
		}
		// The enf of the do-while loop; one should see the loop will not become endless;
		// See above!
		while(localGoOnAuto);
		// If AUTO is not continued, then the choice should be done "by hand", and
		// buttonAccept should be pressed.
		//
		// When all activities bounded to buttonNext are done,
		// buttonFind is allowed:
		allowedButtonFind = true;
	}


	// === STOP
	public class buttonStopActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			JButton button = (JButton) aevt.getSource();
			if (button.isEnabled()) {
				actionOnStop();
			}
		}
	}
	//
	public void actionOnStop() {
		qms.setParams("", 0, 0, "");
		textfInputJournalTitle.setText("");
		mlistTitles.getContents().removeAllElements();
		mlistDetails.getContents().removeAllElements();
		lookupStart = 0;
		started = false;
		goOnAutoPlus = false;
		menuitemAutoPlus.setBackground(Color.GRAY);
		// user-choice is written down
		jdd.writeAndCloseUserChoiceFile();
		// tracing is written down
		//jdd.writeAndCloseTracingFile();
		/*
		The variables bounded to buttons "AUTO" and "NoDots"
		can be changed by this function, but the state of the buttons 
		cannot be changed. Therefore it is better to monitor these variables
		by buttons "AUTO" and "NoDots".
		*/
		mlistDetails.getContents().addElement(gl.lbMessageStopped);
	}

	// === AUTO
	// http://www.java2s.com/Tutorial/Java/0240__Swing/ListeningtoJToggleButtonEventswithanActionListener.htm
	public class buttonAutoActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			AbstractButton buttonA = (AbstractButton) aevt.getSource();
			// boolean buttonAutoSelected = buttonA.getModel().isSelected();
			// there is no sense to introduce a local variable, since its value should be 
			// assigned to another variable
			goOnAuto = buttonA.getModel().isSelected();
			// Control:
			// Macros.message(currentView, "Auto = " + goOnAuto + "\n");
		}
	}

	// === NoDots
	public class buttonNoDotsActionListener
		implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			AbstractButton buttonB = (AbstractButton) aevt.getSource();
			// boolean buttonNoDotsSelected = buttonB.getModel().isSelected();
			// there is no sense to introduce a local variable, since its value should be 
			// assigned to another variable
			jdd.isVancouverMode = buttonB.getModel().isSelected();
		}
	}

	// === "DataSource"
	@SuppressWarnings("unchecked")
	public class comboboxDataSourceActionListener implements ActionListener {
		public void actionPerformed(ActionEvent aevt) {
			//warning: [rawtypes] found raw type: JComboBox
			//JComboBox comboBox = (JComboBox) aevt.getSource();
			JComboBox<String> comboBox = (JComboBox<String>) aevt.getSource();
			jdd.indexOfDataSource = comboBox.getSelectedIndex();
			jdd.rollSelectedSource();
			actionOnDataSourceSelection();
		}
	}
	//
	public void actionOnDataSourceSelection() {
		// For any sake make "clear":
		jdd.jd.clearJournalData();

		//Macros.message(currentView, "1 jdd.jd.jdDataPath = " + jdd.jd.jdDataPath);

		jdd.jd.setDataSource(jdd.selectedSource);

		//Macros.message(currentView, "2 jdd.jd.jdSource = " + jdd.jd.jdSource);

		jdd.jd.rollJournalData();

			// testing:
		//Macros.message(currentView, "after rollJdData: jdd.jd.jdData.size() = " + jdd.jd.jdData.size());
		//jdd.jd.rollJdDataASCII();
		//if (jdd.jd.isDataSuppl) {
		//	jdd.jd.rollJdDataSuppl();
		//	jdd.jd.rollJdDataASCIISuppl();
		//}

		//Macros.message(currentView, "3 jdd.jd.jdDataPath = " + jdd.jd.jdDataPath);

			// testing:
		//Macros.message(currentView, "rollout ASCII:      jdd.jd.jdDataASCII.size()      = " + jdd.jd.jdDataASCII.size());
		//Macros.message(currentView, "rollout ASCIISuppl: jdd.jd.jdDataASCIISuppl.size() = " + jdd.jd.jdDataASCIISuppl.size());

		jdd.jnTitlesMatched.clear();
		mlistTitles.getContents().removeAllElements();
		jdd.jnTitleDetails.clear();
		mlistDetails.getContents().removeAllElements();
	}

	// === Titles [List]
	public class mlistTitlesListSelectionListener implements ListSelectionListener {
		/**
		* [...]ListSelectionListener, but not [...][TitlesList][SelectionListener],
		* (although the latter could be possible ...)
		*
		* Inner class for mlistTitlesListSelectionListener based on the model of
		* http://www.leepoint.net/JavaBasics/gui/gui-31-dogyears-guimodel.html
		===
		* This is one of most common ways to create a listener - define an
		* inner class that "implements [Foo]Listener", and in that class define
		* the [FooBarChangedOrPerformed]_method() method.
		*/
		@SuppressWarnings("unchecked")
		public void valueChanged(ListSelectionEvent levt) {
			// http://www.java2s.com/Code/JavaAPI/javax.swing.text/ListSelectionEventgetSource.htm
			boolean adjust = levt.getValueIsAdjusting();
			if (!adjust) {
				/**
				* if this line is commented out, i.e., 
				* //JList list = (JList) levt.getSource();
				* we receive:
				* warning: [rawtypes] found raw type: JList
				* ^JList list = (JList) levt.getSource();
				*/
				JList<String> list = (JList<String>) levt.getSource();
				/**
				* warning: [unchecked] unchecked cast
				* JList<String> list = (JList<String>) levt.getSource()^;
				*/
				int selections[] = list.getSelectedIndices();
				/**
				//Object selectedValues[] = list.getSelectedValues();
				* Until JDK 1.6 (deprecated in 1.7):
				* public Object[] getSelectedValues()
				* New since JDK 1.7:
				* public List<E> getSelectedValuesList()
				* Returns a list of all the selected items, in increasing order based on
				* their indices in the list.
				* Other points related to List<E>:
				* 		reference to List is ambiguous, both class java.awt.List in
				* 		java.awt and interface java.util.List in java.util match
				*
				* 		error: type List does not take parameters
				* 		java.awt.List<String>
				*/
				java.util.List<String> selectedTitleValues = list.getSelectedValuesList();
				if (selections.length > 0) {
					// this condition works well when selection is removed before
					// the change of the journal title;
					// it is needed then that this 'listener' do nothing
					int idx = selections[0];
					jdd.indexTitleSelected = selections[0];
						//Macros.message(currentView, "idx, selections[0] = " + idx + "\n");
					/**
					* // selectedTitle = (String) selectedTitleValues[0];
					* error: array required, but List<String> found
					* selectedTitle = (String) selectedTitleValues[0]^;
					* Therefore:
					* selectedTitle = (String) selectedTitleValues.get(0);
					* and
					* warning: [cast] redundant cast to String
					* selectedTitle = (String) selectedTitleValues.get(0);
					*/
					// the journal title chosen:
					jdd.selectedTitle = selectedTitleValues.get(0);
					// the list of "details" [mlist] is cleaned:
					mlistDetails.getContents().removeAllElements();
					// new array of "details [ArrayList] is formed:
					jdd.rolljnTitleDetais(idx);
					int dsize = jdd.jnTitleDetails.size();
						//Macros.message(currentView, "jnTitleDetails.size() = " + dsize + "\n");
					// the list of "details" [mlist] is filled in with new elements:
					for (int d = 0; d < dsize; d++) {
						/**
						* warning: [unchecked] unchecked call to addElement(E) as
						* a member of the raw type DefaultListModel
						* mlistDetailsJList.getContents().addElement(jdetails.get(d));
																		 ^
						* where E is a type-variable:
						* E extends Object declared in class DefaultListModel
						*/
						mlistDetails.getContents().addElement(jdd.jnTitleDetails.get(d));
					}
				}
				allowedButtonAccept = true;
			}
		}
	}

	// === Mutable List
	@SuppressWarnings("serial")
	class MutableList extends JList<String> {
		/**
		http://java.sun.com/products/jfc/tsc/tech_topics/jlist_1/jlist.html
		The preferred way to use a dynamic list in an application is to bind
		the code that's updating the list to the ListModel, not to the JList itself.
		The JList encourages this practice because the mutable DefaultListModel
		API isn't exposed by JList. The advantage of keeping the JList model
		and the JList (view) separate is that one can easily replace
		the view without disturbing the rest of the application.
		Occasionally it is more convenient to wire the model and view together.
		One simple way to do this is to create a JList as shown in this example:
		*/
		MutableList() {
			super(new DefaultListModel<String>());
		}
		DefaultListModel<String> getContents() {
			return (DefaultListModel<String>)getModel();
		}
	}

	/**
	* To write an Action Listener, follow the steps given below:
	* 1. Declare an event handler class and specify that the class either
	* implements an ActionListener interface or extends a class
	* that implements an ActionListener interface. For example:
	*     public class MyClass implements ActionListener {...
	* 2. Register an instance of the event handler class as a listener
	* on one or more components. For example:
	*     someComponent.addActionListener(instanceOfMyClass);
	* 3. Include code that implements the methods in listener interface.
	* For example:
	*     public void actionPerformed(ActionEvent e) {
	 *    ...//code that reacts to the action... }
	*/
}
