/*
 * ListAction.java
 * *
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

import java.awt.event.*;
import javax.swing.*;

/*
 *	Add an Action to a JList that can be invoked either by using
 *  the keyboard or a mouse.
 *
 *  By default the Enter will will be used to invoke the Action
 *  from the keyboard although you can specify and KeyStroke you wish.
 *
 *  A double click with the mouse will invoke the same Action.
 *
 *  The Action can be reset at any time.
 */

public class ListAction implements MouseListener {
	private static final KeyStroke ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

	private JList<String> list;
	private KeyStroke keyStroke;

	/*
	*	Add an Action to the JList bound by the default KeyStroke
	*/
	public ListAction(JList<String> list, Action action) {
		this(list, action, ENTER);
	}

	/*
	*	Add an Action to the JList bound by the specified KeyStroke
	*/
	public ListAction(JList<String> list, Action action, KeyStroke keyStroke) {
		this.list = list;
		this.keyStroke = keyStroke;

		//  Add the KeyStroke to the InputMap
		InputMap im = list.getInputMap();
		im.put(keyStroke, keyStroke);

		//  Add the Action to the ActionMap
		setAction(action);

		//  Handle mouse double click
		list.addMouseListener(this);
	}

	/*
	*  Add the Action to the ActionMap
	*/
	public void setAction(Action action) {
		list.getActionMap().put(keyStroke, action);
	}

	//  Implement MouseListener interface
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Action action = list.getActionMap().get(keyStroke);
			if (action != null) {
				ActionEvent event = new ActionEvent(
					list,
					ActionEvent.ACTION_PERFORMED,
					"");
				action.actionPerformed(event);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
