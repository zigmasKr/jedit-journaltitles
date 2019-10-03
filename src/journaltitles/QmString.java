/*
 * QmString.java
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

import java.util.regex.*;

public class QmString {
	/* class QmString - Quantum String */
	
	String qstr;
	int qstart;
	int qend;
	String qmsg;
		
	public QmString() {
		qstr = "";
		qstart = 0;
		qend = 0;
		qmsg = "";
	}
	
	public QmString(String astr, int s, int e) {
		qstr = astr;
		qstart = s;
		qend = e;
		qmsg = "";
	}
	
	public QmString setParams(String ss, int a, int b, String mm) {
		this.qstr = ss;
		this.qstart = a;
		this.qend = b;
		this.qmsg = mm;
		return this;
	}
	
	public String toString() {
		String bb;
		if (qmsg.equals("")) {
			bb = "\n@@qstr:\n" + qstr + "\n@@qstart: " + qstart 
				+ "\n@@qend:   " + qend + "\n";
		} else {
			bb = "\n@@qmsg:   " + qmsg + "\n";
		}
		return bb;
	}
	
	public String toStringc() {
		String bb;
		bb = "\n@@qstr:\n" + qstr + "\n@@qstart: " + qstart 
			+ "\n@@qend:   " + qend + "\n@@qmsg:   " + qmsg + "\n";
		return bb;
	}
	
	public QmString readBracesGroup(String str, int start) {
		/*
		From position @start, tries to read cs mainarg -
		the group {...}, with possibly preceeding spaces.
		*/
		int cb = 0;
		int tempc = 0;
		QmString bgroup = new QmString();
		String _str = str.substring(start);  // inclusive
		StringBuilder fencedGroup = new StringBuilder();
		//
		Pattern p_space = Pattern.compile("^(\\s*)(\\{)(.*)$", Pattern.DOTALL);
		Matcher m_space = p_space.matcher(_str);
		if (m_space.find()) {
			// System.out.println("10 DEBUG group(0): |" + m_space.group(0) + "|");
			// System.out.println("11 DEBUG group(1): |" + m_space.group(1) + "|");
			// System.out.println("12 DEBUG group(2): |" + m_space.group(2) + "|");
			fencedGroup.append("{");
			cb = 1;
			tempc = m_space.start(2);
			_str = m_space.group(3);
			// System.out.println("13 DEBUG _str: |" + _str + "|");
			bgroup.qstart = tempc + start;
		}
		else {
			bgroup.qmsg = "readBracesGroup: ERROR 1: String does not start with  space(s)+brace.\n"; 
			return bgroup;
		}
		// (?<!a)b matches a "b" that is not preceded by an "a", using negative lookbehind;
		Pattern p_brace = Pattern.compile("^((.*?)((?<!\\\\)(\\{|\\})))(.*)$", Pattern.DOTALL);
		// groups:
		// group(0) - all the match
		// group(1) - up to (.*) -- comprises groups 2,3,4
		// group(2) - (.*?) -- up to '{' either '}'
		// group(3) - ((?<!\\\\)(\\{|\\}))
		// group(4) - (\\{|\\}) -- the contents of group(4) coincides with that of group(3)
		// group(5) - (.*)
		Matcher m_brace = p_brace.matcher(_str);
		do {
			if (m_brace.find()) {
				// System.out.println("20 DEBUG group(0): " + m_brace.group(0));
				// System.out.println("21 DEBUG group(1): " + m_brace.group(1));
				// System.out.println("22 DEBUG group(2): " + m_brace.group(2));
				// System.out.println("23 DEBUG group(3): " + m_brace.group(3));
				// System.out.println("24 DEBUG group(4): " + m_brace.group(4));
				// System.out.println("25 DEBUG group(5): " + m_brace.group(5));
				if (m_brace.group(4).equals("{")) {
					fencedGroup.append(m_brace.group(1));
					cb = cb + 1;
					tempc = tempc + m_brace.end(1);
					_str = m_brace.group(5);
				}
				else if (m_brace.group(4).equals("}")) {
					fencedGroup.append(m_brace.group(1));
					cb = cb - 1;
					tempc = tempc + m_brace.end(1);
					// System.out.println("2511 DEBUG tempc: " + tempc);	
					_str = m_brace.group(5);
				}
				else {
					bgroup.qmsg = "readBracesGroup: It's impossible.\n"; 
					cb = cb + 1;
				}
				// System.out.println("26 DEBUG counter = " +  cb);
				// System.out.println("\n\n");
				// System.out.println("26 DEBUG _str: |" + _str + "|");
				m_brace = p_brace.matcher(_str);
			} else {
				bgroup.qmsg = "readBracesGroup: ERROR 2: Brace not found.\n";
				cb = cb + 1;
			}
		}  
		while ((cb > 0) && (cb < 20));  // for fun, to avoid dead lock, etc.
		if (cb > 0) {
			bgroup.qmsg = "readBracesGroup: ERROR 3: Counter is not zero: cb = " + cb  + "\n";
		}
		bgroup.qend = tempc + start;
		bgroup.qstr = fencedGroup.toString();
		return bgroup;
	}
	
	public QmString readBracketsGroup(String str, int start) {
		/*
		From position @start, tries to read cs optarg -
		the group [...], with possibly preceeding spaces.
		*/
		int cb = 0;
		int tempc = 0;
		QmString bgroup = new QmString();
		String _str = str.substring(start);  // inclusive
		StringBuilder fencedGroup = new StringBuilder();
		//
		Pattern p_space = Pattern.compile("^(\\s*)(\\[)(.*)$", Pattern.DOTALL);
		Matcher m_space = p_space.matcher(_str);
		if (m_space.find()) {
			fencedGroup.append("[");  // m_space.group(2)
			cb = 1;
			tempc = m_space.start(2);
			_str = m_space.group(3);
			bgroup.qstart = tempc + start;
		}
		else {
			bgroup.qmsg = "readBracketsGroup: ERROR 1: String does not start with  space(s)+bracket.\n";
			return bgroup;
		}
		// (?<!a)b matches a "b" that is not preceded by an "a", using negative lookbehind
		Pattern p_bracket = Pattern.compile("^((.*?)((?<!\\\\)(\\[|\\])))(.*)$", Pattern.DOTALL);
		Matcher m_bracket = p_bracket.matcher(_str);
		do {
			if (m_bracket.find()) {
				if (m_bracket.group(4).equals("[")) {
					fencedGroup.append(m_bracket.group(1));
					cb = cb + 1;
					tempc = tempc + m_bracket.end(1);
					_str = m_bracket.group(5);
				}
				else if (m_bracket.group(4).equals("]")) {
					fencedGroup.append(m_bracket.group(1));
					cb = cb - 1;
					tempc = tempc + m_bracket.end(1);
					_str = m_bracket.group(5);
				}
				else {
					bgroup.qmsg = "readBracketsGroup: It's impossible.\n";
					cb = cb + 1;
				}
				m_bracket = p_bracket.matcher(_str);
			} else {
				bgroup.qmsg = "readBracketsGroup: ERROR 2:  Bracket not found.\n";
				cb = cb + 1;
			}
		} while ((cb > 0) && (cb < 20));  // for fun, to avoid dead lock, etc.
		if (cb > 0) {
			bgroup.qmsg = "readBracketsGroup: ERROR 3: Counter is not zero: cb = " + cb  + "\n";
		}
		bgroup.qend = tempc + start;
		bgroup.qstr = fencedGroup.toString();
		return bgroup;
	}
	
	public QmString findSubString(String strBase, String strSearch, int start) {
		// search is in a (pure) code
		int index;
		QmString output = new QmString();
		index = strBase.indexOf(strSearch, start);
		if (index > -1) {
			output.qstr = strSearch;
			output.qstart = index;
			output.qend = index + strSearch.length();
		} else {
			output.qmsg = "String   " + strSearch + "   not found\n";
		}
		return output;
	}
	
	public QmString findSubString(String strBase, String strSearch) {
		return findSubString(strBase, strSearch, 0);
	}
	
	public QmString findRegex(String strBase, String strRegex, int start) {
		// search is in a (pure) code;
		// @strRegex is assumed to be of the form "(?s)(regex-a)(.*)$"
		String input = strBase.substring(start); 
		Pattern p_strRegex = Pattern.compile(strRegex);
		Matcher m_strRegex = p_strRegex.matcher(input);
		QmString output = new QmString();
		if (m_strRegex.find()) {
			output.qstr = m_strRegex.group(1);
			output.qstart = m_strRegex.start(1);
			output.qend = m_strRegex.end(1);
		} else {
			output.qmsg = "Pattern   " + strRegex + "   not found\n";
		}
		return output;
	}
	
	public QmString findRegex(String strBase, String strRegex) {
		return findRegex(strBase, strRegex, 0);
	}
	
	// Mnemonic tcc - for "TeX code & comments"
	
	public QmString tccFindSubString(String strBase, String strSearch, int start) {
		// search is in a TeX code+comments
		String input = strBase.substring(start);
		// -- one assumes that @start position belongs to pure TeX code
		TxCode tca = new TxCode();
		tca.init(new StringBuilder(input));
		// pure TeX code, starting from prePosition:
		String inputCode = tca.code.toString();
		QmString ss = findSubString(inputCode, strSearch);
		// map back to original text (code+comments):
		int qstart_ss = tca.mapCode(ss.qstart);
		int qend_ss = tca.mapCode(ss.qend);
		// add @start:
		ss.qstart = qstart_ss + start;
		ss.qend = qend_ss + start;
		return ss;
	}
	
	public QmString tccFindSubString(String strBase, String strSearch) {
		return tccFindSubString(strBase, strSearch, 0);
	}
	
	public QmString tccFindRegex(String strBase, String strRegex, int start) {
		// search is in a TeX code+comments;
		// @strRegex is assumed to be of the form "(?s)(regex-a)(.*)$"
		String input = strBase.substring(start);
		// -- one assumes that @start position belongs to pure TeX code
		TxCode tcb = new TxCode();
		tcb.init(new StringBuilder(input));
		// pure TeX code, starting from prePosition:
		String inputCode = tcb.code.toString();
		QmString sr = findRegex(inputCode, strRegex);
		// map back to original text (code+comments):
		int qstart_sr = tcb.mapCode(sr.qstart);
		int qend_sr = tcb.mapCode(sr.qend);
		// add @start:
		sr.qstart = qstart_sr + start;
		sr.qend = qend_sr + start;
		return sr;
	}
	
	public QmString tccFindRegex(String strBase, String strRegex) {
		return tccFindRegex(strBase, strRegex, 0);
	}
	
	public String regexBissueBtitle = "(?s)(\\\\begin\\s*\\{bissue\\}\\s*\\\\bseries\\s*\\{\\s*\\\\btitle)(.*)$";
	
	
	public QmString tccGetBissueBtitleArg(String str, int start) {
		// \begin{bissue}
		// \bseries{\btitle{Discrete Event Dyn. Syst.} \bvolumeno{22}}
		
		/* Step A: take substring at @start of the initial string.
		It is assumed that @start position belongs to pure TeX code. */
		String input = str.substring(start);
		QmString bissueBtitle = new QmString();
		QmString outp = new QmString();
		int qstart_aa; 
		int qend_aa; 
		
		/* Step B: take pure TeX code of the substring @input. */
		TxCode tcd = new TxCode();
		tcd.init(new StringBuilder(input));
		// pure TeX code, starting from @start:
		String inputCode = tcd.code.toString();
		
		/* Step C: do any search, calculations, etc. on the pure TeX
		code string @inputCode. 
		Methods findRegex, findSubString, or any other code can be used. */
		bissueBtitle = findRegex(inputCode, regexBissueBtitle);
		if (bissueBtitle.qmsg.equals("")) {
			outp = readBracesGroup(inputCode, bissueBtitle.qend);
			
			/* Step D: if that calculation is successful, get back 
			via mapCode to the original TeX code+comments */
			qstart_aa = tcd.mapCode(outp.qstart);
			qend_aa = tcd.mapCode(outp.qend);
			// add @start:
			outp.qstart = qstart_aa + start;
			outp.qend = qend_aa + start;
		} else {
			outp.qmsg = bissueBtitle.qmsg;
		}
		return outp;
	}
	
	public QmString tccGetBissueBtitleArg(String str) {
		// \begin{bissue}
		// \bseries{\btitle{Discrete Event Dyn. Syst.} \bvolumeno{22}}
		return tccGetBissueBtitleArg(str, 0);
	}
	
}

