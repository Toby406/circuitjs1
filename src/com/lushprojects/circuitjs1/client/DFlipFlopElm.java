/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client;

    class DFlipFlopElm extends ChipElm {
	final int FLAG_RESET = 2;
	final int FLAG_SET = 4;
	final int FLAG_INVERT_SET_RESET = 8;
	boolean hasReset() { return (flags & FLAG_RESET) != 0 || hasSet(); }
	boolean hasSet() { return (flags & FLAG_SET) != 0; }
	boolean invertSetReset() { return (flags & FLAG_INVERT_SET_RESET) != 0; }
	
	public DFlipFlopElm(int xx, int yy) {
            super(xx, yy);
	    pins[2].value = !pins[1].value;
        }
	public DFlipFlopElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    pins[2].value = !pins[1].value;
	    justLoaded = true;
	}
	String getChipName() { return "D flip-flop"; }
	void setupPins() {
	    sizeX = 2;
	    sizeY = 3;
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(0, SIDE_W, "D");
	    pins[1] = new Pin(0, SIDE_E, "Q");
	    pins[1].output = pins[1].state = true;
	    pins[2] = new Pin(hasSet()?1:2, SIDE_E, "Q");
	    pins[2].output = true;
	    pins[2].lineOver = true;
	    pins[3] = new Pin(1, SIDE_W, "");
	    pins[3].clock = true;
           if (!hasSet()) {
            if (hasReset()) {
               pins[4] = new Pin(2, SIDE_W, "R");
               pins[4].bubble = invertSetReset();
            }
           } else {
               pins[5] = new Pin(2, SIDE_W, "S");
               pins[4] = new Pin(2, SIDE_E, "R");
               pins[4].bubble = pins[5].bubble = invertSetReset();
            }
	}
	int getPostCount() {
	    return 4 + (hasReset() ? 1 : 0) + (hasSet() ? 1 : 0);
	}
	int getVoltageSourceCount() { return 2; }
        void reset() {
            super.reset();
	    nodes[2].volts = 5;
	    pins[2].value = true;
        }
	void execute() {
	    if (nodes[3].high && !lastClock)
		writeOutput(1, nodes[0].high);
	    if(hasSet() && nodes[5].high != invertSetReset())
		writeOutput(1, true);
	    if(hasReset() && nodes[4].high != invertSetReset())
		writeOutput(1, false);
	    writeOutput(2, !nodes[1].high);
	    lastClock = nodes[3].high;
	}
	int getDumpType() { return 155; }
	public EditInfo getEditInfo(int n) {
	    if (n == 2) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Reset Pin", hasReset());
		return ei;
	    }
	    if (n == 3) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Set Pin", hasSet());
		return ei;
	    }
	    if (n == 4) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Invert Set/Reset", invertSetReset());
		return ei;
	    }
	    return super.getEditInfo(n);
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 2) {
		if (ei.checkbox.getState())
		    flags |= FLAG_RESET;
		else
		    flags &= ~FLAG_RESET|FLAG_SET;
		setupPins();
		allocNodes();
		setPoints();
	    }
	    if (n == 3) {
		if (ei.checkbox.getState())
		    flags |= FLAG_SET;
		else
		    flags &= ~FLAG_SET;
		setupPins();
		allocNodes();
		setPoints();
	    }
	    if (n == 4) {
		flags = ei.changeFlag(flags, FLAG_INVERT_SET_RESET);
		setupPins();
		setPoints();
	    }
	    super.setEditValue(n, ei);
	}
    }
