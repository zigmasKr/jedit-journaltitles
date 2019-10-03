/*
 * EditListAction.java
 * 
 * Part of the JournalTitles plugin for the jEdit text editor
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
* Source:
* http://tips4java.wordpress.com/2008/10/19/list-editor/
* Posted by Rob Camick on October 19, 2008
*/
 
 
package journaltitles;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/*
*  A simple popup editor for a JList that allows you to change
*  the value in the selected row.
*
*  The default implementation has a few limitations:
*
*  a) the JList must be using the DefaultListModel
*  b) the data in the model is replaced with a String object
*
*  If you which to use a different model or different data then you must
*  extend this class and:
*
*  a) invoke the setModelClass(...) method to specify the ListModel you need
*  b) override the applyValueToModel(...) method to update the model
*/

/**
@SuppressWarnings
must go before the public keyword.
*/

/*
The serializable class <...> does
not declare a static final serialVersionUID field
of type long warning suppressed by
@SuppressWarnings("serial")
See more at:
http://www.hubberspot.com/2012/09/what-is-suppresswarnings-annotation.html#sthash.1hTNtccB.dpuf
*/

@SuppressWarnings("serial")
public class EditListAction extends AbstractAction {
	// Instance attributes:
	private JList<String> list;

	private JPopupMenu editPopup;
	private JTextField editTextField;
	private Class<?> modelClass;

	// Constructor:
	public EditListAction() {
		setModelClass(DefaultListModel.class);
		/** DefaultListModel.class "prints" kaip
		* class javax.swing.DefaultListModel
		* System.out.println("show: " + DefaultListModel.class);
		*
		* Constructor "switches on" a method which eventually 
		* (or imaginary) assigns the value DefaultListModel
		* to the "instance attribute" modelClass. */
	}

	// Methods:
	protected void setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
		/** It also "prints" as:
		* showtwo: class javax.swing.DefaultListModel
		* and it happens earlier than that with "show" (clear)
		* System.out.println("show: " + this.modelClass);  */
	}

	protected void applyValueToModel(String value, ListModel<String> model, int row) {
		DefaultListModel<String> dlm = (DefaultListModel<String>)model;
		dlm.set(row, value);
	}

	/*
	* Display the popup editor when requested
	*/
	/*
	http://stackoverflow.com/questions/2592642/type-safety-unchecked-cast-from-object
	*/
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		list = (JList<String>)e.getSource();
		ListModel<String> model = list.getModel();

		if (!modelClass.isAssignableFrom(model.getClass()))
			return;

		//  Do a lazy creation of the popup editor
		if (editPopup == null)
			createEditPopup();

		//  Position the popup editor over top of the selected row
		int row = list.getSelectedIndex();
		Rectangle r = list.getCellBounds(row, row);

		editPopup.setPreferredSize(new Dimension(r.width, r.height));
		editPopup.show(list, r.x, r.y);

		//  Prepare the text field for editing
		editTextField.setText(list.getSelectedValue().toString());
		editTextField.selectAll();
		editTextField.requestFocusInWindow();
	}

	/*
	*  Create the popup editor
	*/
	private void createEditPopup() {
		//  Use a text field as the editor
		editTextField = new JTextField();
		Border border = UIManager.getBorder("List.focusCellHighlightBorder");
		editTextField.setBorder(border);

		//  Add an Action to the text field to save the new value to the model
		editTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = editTextField.getText();
				ListModel<String> model = list.getModel();
				int row = list.getSelectedIndex();
				applyValueToModel(value, model, row);
				editPopup.setVisible(false);
			}
		});

		//  Add the editor to the popup
		editPopup = new JPopupMenu();
		editPopup.setBorder(new EmptyBorder(0, 0, 0, 0));
		editPopup.add(editTextField);
	}
}
