package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D08 {
    public static class Action {
        String regex;
        Pattern pattern;
        BiConsumer<Screen,Matcher> doAction;
        BiConsumer<Screen,Matcher> undoAction;
        
        public Action(String regex, BiConsumer<Screen,Matcher> doAction) {
            this(regex, doAction, doAction);
        }

        public Action(String regex, BiConsumer<Screen,Matcher> doAction, BiConsumer<Screen,Matcher> undoAction) {
            this.regex = regex;
            this.pattern = Pattern.compile(regex);
            this.doAction = doAction;
            this.undoAction = undoAction;
        }

        public boolean execute(Screen s, String instruction) {
            Matcher m = pattern.matcher(instruction);
            if (m.matches()) {
                doAction.accept(s,  m);
            }
            
            return m.matches();
        }

        public boolean rollback(Screen s, String instruction) {
            Matcher m = pattern.matcher(instruction);
            if (m.matches()) {
                undoAction.accept(s,  m);
            }
            
            return m.matches();
        }
    }
    
    public static class Screen {
        int numRows;
        int numCols;
        boolean[][] pixels;
        
        public Screen(int nCols, int nRows) {
            numCols = nCols;
            numRows = nRows;
            pixels = new boolean [nCols][nRows];
            
            // Everything is off initially
            clear();
        }

        private char asChar(boolean on) {
            return on ? '1' : '0';
        }

        private boolean asBoolean(char on) {
            return on == '1';
        }
        
        public int getNumCols() {
            return numCols;
        }
        
        public int getNumRows() {
            return numRows;
        }

        public boolean isOn(int col, int row) {
            return pixels[col][row];
        }

        public boolean setOn(int col, int row, boolean on) {
            boolean prev = isOn(col, row);
            
            pixels[col][row] = on;
            return prev;
        }
        
        public int getNumOn() {
            int numOn = 0;
            for (int col=0; col<numCols; ++col) {
                for (int row=0; row<numRows; ++row) {
                    if (isOn(col, row)) {
                        ++numOn;
                    }
                }
            }
            
            return numOn;
        }

        public void clear() {
            for (int col=0; col<numCols; ++col) {
                for (int row=0; row<numRows; ++row) {
                    setOn(col, row, false);
                }
            }
        }

        public char[] readRow(int row) {
            char[] data = new char[numCols];
            
            for (int col=0; col<numCols; ++col) {
                data[col] = asChar(isOn(col, row));
            }
            
            return data;
        }

        public char[] readCol(int col) {
            char[] data = new char[numRows];
            
            for (int row=0; row<numRows; ++row) {
                data[row] = asChar(isOn(col, row));
            }
            
            return data;
        }
        
        public void writeRow(int row, char[] data) {
            for (int col=0; col<numCols; ++col) {
                setOn(col, row, asBoolean(data[col]));
            }
        }

        public void writeCol(int col, char[] data) {
            for (int row=0; row<numRows; ++row) {
                setOn(col, row, asBoolean(data[row]));
            }
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            
            for (int row=0; row<numRows; ++row) {
                for (int col=0; col<numCols; ++col) {
                    sb.append(isOn(col, row) ? "#" : " ");
                }
                sb.append("\n");
            }
            
            return sb.toString();
        }
    }

    public static Collection<Action> getActions() {
        Collection<Action> actions = new LinkedList<Action>();
        
        // Match specs like "rect 3x2"
        actions.add(new Action("rect (\\d*)x(\\d*)", (screen, m) -> {
            int nCols = AdventUtils.toInt(m.group(1));
            int nRows = AdventUtils.toInt(m.group(2));
            
            for (int col=0; col<nCols; ++col) {
                for (int row=0; row<nRows; ++ row) {
                    screen.setOn(col,  row,  true);
                }
            }
        }));
        
        // rotate column x=1 by 1
        actions.add(new Action("rotate column x=(\\d*) by (\\d*)", (screen, m) -> {
            int col = AdventUtils.toInt(m.group(1));
            int count = AdventUtils.toInt(m.group(2));
            char[] data = screen.readCol(col);
            
            AdventUtils.rotateRight(data, count);
            screen.writeCol(col, data);
        }));
        
        // rotate row y=0 by 4
        actions.add(new Action("rotate row y=(\\d*) by (\\d*)", (screen, m) -> {
            int row = AdventUtils.toInt(m.group(1));
            int count = AdventUtils.toInt(m.group(2));
            char[] data = screen.readRow(row);
            
            AdventUtils.rotateRight(data, count);
            screen.writeRow(row, data);
        }));
        
        return actions;
    }
    
    public static int render(Screen screen, String[] instructions) {
        Collection<Action> actions = getActions();
        
        // Process each instruction
        for (String instruction : instructions) {
            // Act on the instruction
            boolean processed = actions.stream().anyMatch(a -> a.execute(screen, instruction));
            
            // If there are no matches, fire an alert
            if (! processed) {
                System.out.printf("ERROR: no Actions matched to work on instruction '%s'\n", instruction);
            }
        }
        
        System.out.printf("Thanks for watching the show!\n\n%s\n\n", screen);
        
        int numOn = screen.getNumOn();
        return numOn;
    }

    @Test
    public void testRender() {
        Screen screen = new Screen(7, 3);
        String[][] inputs = {
                { 
                    "rect 3x2",
                    "rotate column x=1 by 1",
                    "rotate row y=0 by 4",
                    "rotate column x=1 by 1",
                }
        };
        int[] expected = { 6, };
    
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("render() incorrect", expected[i], render(screen, inputs[i]));
        }
    }

    @Test
    public void testStarOne() {
        Screen screen = new Screen(NUM_COLS, NUM_ROWS);
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        int expected = 106;
    
        assertEquals("testStarOne() incorrect", expected, render(screen, inputs));
    }
    
    private static final int NUM_ROWS=6, NUM_COLS=50;
    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day8-inputs.txt");
}
