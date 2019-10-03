/*
 * ExtString.java
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

public class ExtString {
	
	public ExtString() {}
	
	public String plainAscii(String str) {
		// Naive conversion to plain ascii: removes diacritics,
		// removes superfluous braces, sets lowercase.
		String _str = str; 
		// fonts' command:
		String ptrn_textit = "\\\\textit";
		String ptrn_textbf = "\\\\textbf";
		String ptrn_textrm = "\\\\textrm";
		// "ordinary special" letters:
		String ptrn_csi = "\\\\[iI]\\s*";
		String ptrn_csl = "\\\\[iL]\\s*";
		String ptrn_cso = "\\\\[oO]\\s*";
		// more "special" letters:
		String ptrn_csoe = "\\\\oe\\s*";
		String ptrn_csae = "\\\\ae\\s*";
		String ptrn_csss = "\\\\ss\\s*";
		// more "special" letters:
		String ptrn_csaa = "\\\\aa\\s*";
		String ptrn_TeX_diaa = "\\\\[\\`\\'\\^\\\"\\~\\.\\-\\=]\\s*";
		String ptrn_TeX_diab = "\\\\[cbdkrtuvH]\\s+";
		String ptrn_TeX_diac = "\\\\[cbdkrtuvH]\\{";
		// any grouping fences:
		String ptrn_braces = "\\[|\\{|\\}|\\]";
		// quotation marks:
		String ptrn_quotem = "[\\`\\']+";
		// French quotaion marks:
		String ptrn_og = "\\\\og\\s*";
		String ptrn_fg = "\\\\fg\\\\s+";
		// mathematicsl:
		String ptrn_mathm = "\\$";
		// Compiled patterns:
		Pattern p_textit = Pattern.compile(ptrn_textit);
		Pattern p_textbf = Pattern.compile(ptrn_textbf);
		Pattern p_textrm = Pattern.compile(ptrn_textrm);
		Pattern p_csi = Pattern.compile(ptrn_csi, Pattern.CASE_INSENSITIVE);
		Pattern p_csl = Pattern.compile(ptrn_csl, Pattern.CASE_INSENSITIVE);
		Pattern p_cso = Pattern.compile(ptrn_cso, Pattern.CASE_INSENSITIVE);
		Pattern p_csoe = Pattern.compile(ptrn_csoe, Pattern.CASE_INSENSITIVE);
		Pattern p_csae = Pattern.compile(ptrn_csae, Pattern.CASE_INSENSITIVE);
		Pattern p_csss = Pattern.compile(ptrn_csss, Pattern.CASE_INSENSITIVE);
		Pattern p_csaa = Pattern.compile(ptrn_csaa, Pattern.CASE_INSENSITIVE);
		Pattern p_TeX_diaa = Pattern.compile(ptrn_TeX_diaa);
		Pattern p_TeX_diab = Pattern.compile(ptrn_TeX_diab);
		Pattern p_TeX_diac = Pattern.compile(ptrn_TeX_diac);
		Pattern p_braces = Pattern.compile(ptrn_braces);
		Pattern p_quotem = Pattern.compile(ptrn_quotem);
		Pattern p_og = Pattern.compile(ptrn_og);
		Pattern p_fg = Pattern.compile(ptrn_fg);
		Pattern p_mathm = Pattern.compile(ptrn_mathm);
		//
		Matcher m_textit = p_textit.matcher(_str);
		_str = m_textit.replaceAll("");
		Matcher m_textbf = p_textbf.matcher(_str);
		_str = m_textbf.replaceAll("");
		Matcher m_textrm = p_textrm.matcher(_str);
		_str = m_textrm.replaceAll("");
		//
		Matcher m_csi = p_csi.matcher(_str);
		_str = m_csi.replaceAll("i");
		Matcher m_csl = p_csl.matcher(_str);
		_str = m_csl.replaceAll("l");
		Matcher m_cso = p_cso.matcher(_str);
		_str = m_cso.replaceAll("o");
		//
		Matcher m_csoe = p_csoe.matcher(_str);
		_str = m_csoe.replaceAll("oe");
		Matcher m_csae = p_csae.matcher(_str);
		_str = m_csae.replaceAll("ae");
		Matcher m_csss = p_csss.matcher(_str);
		_str = m_csss.replaceAll("ss");
		Matcher m_csaa = p_csaa.matcher(_str);
		_str = m_csaa.replaceAll("a");
		//
		Matcher m_TeX_diaa = p_TeX_diaa.matcher(_str);
		_str = m_TeX_diaa.replaceAll("");
		Matcher m_TeX_diab = p_TeX_diab.matcher(_str);
		_str = m_TeX_diab.replaceAll("");
		Matcher m_TeX_diac = p_TeX_diac.matcher(_str);
		_str = m_TeX_diac.replaceAll("{");
		//
		Matcher m_braces = p_braces.matcher(_str);
		_str = m_braces.replaceAll("");
		Matcher m_quotem = p_quotem.matcher(_str);
		_str = m_quotem.replaceAll("");
		//
		Matcher m_og = p_og.matcher(_str);
		_str = m_og.replaceAll("");
		Matcher m_fg = p_fg.matcher(_str);
		_str = m_fg.replaceAll(" ");
		//
		Matcher m_mathm = p_mathm.matcher(_str);
		_str = m_mathm.replaceAll("");
		//
		_str = _str.toLowerCase();
		return _str;
	}

	public static String removeFullStops(String str) { 
		// Removes all dots '.', which are not proceeded with \\.
		String ptrn_fullstop = "(?<!\\\\)\\.";
		Pattern p_fullstop = Pattern.compile(ptrn_fullstop);
		Matcher m_fullstop = p_fullstop.matcher(str);
		return m_fullstop.replaceAll("");
	}
	
}

