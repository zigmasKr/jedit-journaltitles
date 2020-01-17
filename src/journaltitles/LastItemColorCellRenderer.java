
/*
 * LastItemColorCellRenderer.java
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
Idea based on:
https://coderanch.com/t/335943/java/Changing-background-color-JList
*/

package journaltitles;

import java.awt.Color;
import java.awt.Component;
	  
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
	  
class LastItemColorCellRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list, Object value, int index, 
		boolean isSelected, boolean cellHasFocus) {
		int lastIndex = list.getModel().getSize() - 1;
		// some greyish
		// 0xcccccc selected by ColorChooser
		Color myGreyish = new Color(204,204,204,255); 
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (index == lastIndex) {
			// index f the last element
			c.setBackground(myGreyish);
		}
		else {
			c.setBackground(Color.white);
		}
		return c;
	}
}
