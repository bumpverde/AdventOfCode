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

/**
 * --- Day 8: Two-Factor Authentication ---
 * You come across a door implementing what you can only assume is an implementation of two-factor authentication after a long game of requirements telephone.
 * 
 * To get past the door, you first swipe a keycard (no problem; there was one on a nearby desk). 
 * Then, it displays a code on a little screen, and you type that code on a keypad. Then, presumably, the door unlocks.
 * 
 * Unfortunately, the screen has been smashed. After a few minutes, you've taken everything apart and figured out how it works. 
 * Now you just have to work out what the screen would have displayed.
 * 
 * The magnetic strip on the card you swiped encodes a series of instructions for the screen; these instructions are your puzzle input. 
 * The screen is 50 pixels wide and 6 pixels tall, all of which start off, and is capable of three somewhat peculiar operations:
 * 
 * - rect AxB turns on all of the pixels in a rectangle at the top-left of the screen which is A wide and B tall.
 * - rotate row y=A by B shifts all of the pixels in row A (0 is the top row) right by B pixels. Pixels that would fall off the right end appear at the left end of the row.
 * - rotate column x=A by B shifts all of the pixels in column A (0 is the left column) down by B pixels. Pixels that would fall off the bottom appear at the top of the column.
 * 
 * For example, here is a simple sequence on a smaller screen:
 * 
 * rect 3x2 creates a small rectangle in the top-left corner:
 * ###....
 * ###....
 * .......
 * 
 * rotate column x=1 by 1 rotates the second column down by one pixel:
 * #.#....
 * ###....
 * .#.....
 *
 * rotate row y=0 by 4 rotates the top row right by four pixels:
 * ....#.#
 * ###....
 * .#.....
 * 
 * rotate column x=1 by 1 again rotates the second column down by one pixel, causing the bottom pixel to wrap back to the top:
 * .#..#.#
 * #.#....
 * .#.....
 * 
 * As you can see, this display technology is extremely powerful, and will soon dominate the tiny-code-displaying-screen market. 
 * That's what the advertisement on the back of the display tries to convince you, anyway.
 * 
 * There seems to be an intermediate check of the voltage used by the display: after you swipe your card, if the screen did work, how many pixels should be lit?
 * 
 * Your puzzle answer was 106.
 * 
 * --- Part Two ---
 * You notice that the screen is only capable of displaying capital letters; in the font it uses, each letter is 5 pixels wide and 6 tall.
 * 
 * After you swipe your card, what code is the screen trying to display?
 * 
 * Your puzzle answer was CFLELOYFCS.
 *  
 * @author bumpverde
 */
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
