/*
 * TxCode.java
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

import java.util.*; 

public class TxCode {
	
	private class CommChunk {
		String cont;
		int start;
		int end;
		int length;
		
		private CommChunk(String cc, int cs, int ce, int cl) {
			cont = cc;
			start = cs;
			end = ce;
			length = cl;
		}
	}
	
	private class CodeChunk {
		String cont;
		int start;
		int end;
		int length;
		int diff;
		
		private CodeChunk(String dc, int ds, int de, int dl, int dd) {
			cont = dc;
			start = ds;
			end = de;
			length = dl;
			diff = dd;
		}
	}
	
	StringBuilder base; // Bazinis stringas, vienas ir tas pats tam tikram uzdaviniui.
	StringBuilder code; // Stringo reiksmine dalis, apvalyta nuo komentaru.
	TxState state;
	ArrayList<CommChunk> commChunks;
	ArrayList<CodeChunk> codeChunks;
	
	public TxCode() {
		base = new StringBuilder();
		code = new StringBuilder();
		this.state = new TxState();
		this.commChunks = new ArrayList<CommChunk>();
		this.codeChunks = new ArrayList<CodeChunk>();
	}
	
	
	void txCodeDo() {
		StringBuilder tstr = base;
		boolean isComment = false;
		int start_comm = 0;
		int start_code = 0;
		int temp_diff = 0;
		String code_cont; 
		String comm_cont; 
		int k;
		for (k = 0; k < tstr.length(); k++) {
			// incoming ('next') char:
			state.getChar(tstr.charAt(k));
			// state is 'code':
			if (!isComment) {
				if (state.isOutput) {
					// if incoming char is 'output', i.e. 'code':
					// 'code' chunk is growing;
					/// at the end:
					if (k == tstr.length() - 1) {
						code_cont = base.substring(start_code, k + 1);
						codeChunks.add( 
							new CodeChunk(code_cont, start_code, k + 1, 
								k + 1 - start_code, temp_diff));
						code.append(code_cont);
					}
				} else {
					// case: 'code' chunk ends; 
					// 'comment' chunk starts;
					// code chunk is added to ArrayList	
					code_cont = base.substring(start_code, k);	
					codeChunks.add(
						new CodeChunk(code_cont, start_code, k,
							k - start_code, temp_diff));
					code.append(code_cont);
					start_comm = k;
					isComment = true;
				}
			}
			// state is 'comment':
			else {
				if (state.isOutput) {
					// case: 'comment' chunk ends;
					// 'code' chunk starts;
					// comment chunk is added to ArrayList
					comm_cont = base.substring(start_comm, k + 1);
					commChunks.add( 
						new CommChunk(comm_cont, start_comm, k + 1,
							k + 1 - start_comm));
						start_code = k + 1;
						temp_diff = temp_diff + k + 1 - start_comm;
						isComment = false;
				}
				// 'comment' chunk is growing
				else {
					/// at the end:
					if (k == tstr.length() - 1) {
						comm_cont = base.substring(start_comm, k + 1);
						commChunks.add( 
							new CommChunk(comm_cont, start_comm, k + 1,
							k + 1 - start_comm));
					}
				}
			}
		}
	}

	void setBase(StringBuilder bstr) {
		base = bstr;
	}
	
	void init(StringBuilder initstr) {
		setBase(initstr);
		txCodeDo();
	}
	
	int mapCode(int pc) {
		int m;
		int out = 0;
		int tpc;
		CodeChunk node;
		for (m = 0; m < codeChunks.size(); m++) {
			node = codeChunks.get(m);
			tpc = pc + node.diff;
			if ((tpc >= node.start) &&
				(tpc <= node.end)) {
				out = tpc;
				break;
			}
		}
		return out;
	}
	
	String displayToString() {
		int m;
		CodeChunk cdnode;
		CommChunk cmnode;
		StringBuilder out = new StringBuilder();
		out.append(code);
		out.append("\n");
		out.append("\n\n===*CODE CHUNKS===\n");
		for (m = 0; m < codeChunks.size(); m++) {
			cdnode = codeChunks.get(m);
			out.append("\n\n*CODE CHUNK:\n|");
			out.append(cdnode.cont);
			out.append("|");
			out.append("\n*CODE CHUNK START:\n");
			out.append(cdnode.start);
			out.append("\n*CODE CHUNK END:\n");
			out.append(cdnode.end);
			out.append("\n*CODE CHUNK LENGTH:\n");
			out.append(cdnode.length);
			out.append("\n*CODE CHUNK SDIFF:\n");
			out.append(cdnode.diff);
		}
		out.append("\n\n===*COMMENT CHUNKS===\n");
		for (m = 0; m < commChunks.size(); m++) {
			cmnode = commChunks.get(m);
			out.append("\n\n*COMMENT CHUNK:\n|");
			out.append(cmnode.cont);
			out.append("|");
			out.append("\n*START:\n");
			out.append(cmnode.start);
			out.append("\n*END:\n");
			out.append(cmnode.end);
			out.append("\n*LENGTH:\n");
			out.append(cmnode.length);
		}
		return out.toString();
	}
}

