/*
 * TxState.java
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
/*
 * Class for the TeX code reading that naively 
 * implements the reading by TeX.
*/

public class TxState {
	
	boolean isEscape;
	boolean isOutput;
	boolean isEOL;
	char    Cchar;

	public TxState() {
		/* 
		Usually a TxState object is initialized
		as being in the neutral state.
		*/
		this.isEscape = false; // next char will belong to control sequence
		this.isOutput = false;
		this.Cchar = '\0';     // current char on which the state lives
	}
	
	public void getChar(char ch) {
		/* 
		Method which changes the state of 
		a TxState object depending on what 
		the gobbled (next to where TxState lives) char is.
		*/
		this.Cchar = ch;
		if (this.isEscape) {
			// this is enough for skipping commented characters
			this.isEscape = false;
		}
		else if (ch == '\\') {
			this.isEscape = true;
		}
		else if ((!this.isEscape) && (ch == '%')) {
			this.isOutput = false;
		}
		if ((!this.isOutput) && (ch != '\n')) {
			this.isOutput = false;
		} 
		else if (!this.isOutput) {
			this.isOutput = true;
			this.isEOL = false;
			this.Cchar = '\0';
		}
	}
}
