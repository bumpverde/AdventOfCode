package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.verde.advent.AdventUtils;

public class Y2016D10 {
    static final Map<String, Bot> botsByName = new HashMap<String, Bot>();
    static final Map<String, Integer> outputsByName = new HashMap<String, Integer>();

    public static Bot getBot(String name) {
        Bot bot = botsByName.get(name);
        
        if (bot == null) {
            bot = new Bot(name);
            botsByName.put(name,  bot);
        }
        
        return bot;
    }
    
    public static int getOutput(String name) {
        Integer value = outputsByName.get(name);
        
        if (value == null) {
            value = new Integer(0);
            outputsByName.put(name,  value);
        }
        
        return value;
    }
    
    public static class Bot {
        String name;
        List<Integer> values = new LinkedList<Integer>();
        String xferLowTo = null;
        String xferHighTo = null;

        public Bot(String name) {
            this.name = name;
        }

        public void setTransferInstructions(String low, String high) {
            this.xferLowTo = low;
            this.xferHighTo = high;
            checkTransfer();
        }

        public void add(int value) {
            System.out.printf("adding value %d to %s\n", value, name);
            values.add(value);
            checkTransfer();
        }
        
        public int getLowValue() {
            return values.stream().mapToInt(x -> x).min().orElse(0);
        }
        
        public int getHighValue() {
            return values.stream().mapToInt(x -> x).max().orElse(0);
        }
        
        public void checkTransfer() {
            // If we have two values, then execute transfer instructions
            if (values.size() >= 2) {
                transfer(xferLowTo, getLowValue());
                transfer(xferHighTo, getHighValue());
//                values.clear();   // don't clear, or else we can't validate the states
            }
        }
        
        private void transfer(String to, int value) {
            if (to == null) {
                return;
            }
            
            System.out.printf("transferring %d from %s to %s\n", value, name, to);
            if (to.contains("bot")) {
                getBot(to).add(value);
            } else {
                outputsByName.put(to,  getOutput(to) + value);
            }
        }
    }
    
    public static class Action {
        
        Pattern pattern;
        Consumer<Matcher> execute;
        Consumer<Matcher> unexecute;
        
        public Action(String regex, Consumer<Matcher> execute) {
            this(regex, execute, execute);
        }

        public Action(String regex, Consumer<Matcher> execute, Consumer<Matcher> unexecute) {
            this.pattern = Pattern.compile(regex);
            this.execute = execute;
            this.unexecute = unexecute;
        }

        public boolean execute(String instruction) {
            Matcher m = pattern.matcher(instruction);
            if (m.matches()) {
                execute.accept(m);
            }
            
            return m.matches();
        }

        public boolean unexecute(String instruction) {
            Matcher m = pattern.matcher(instruction);
            if (m.matches()) {
                unexecute.accept(m);
            }

            return m.matches();
        }
    }
    
    public static Collection<Action> getActions() {
        Collection<Action> actions = new LinkedList<Action>();
        
        // Match specs like "value 5 goes to bot 2".
        actions.add(new Action("value (\\d*) goes to bot (\\d*)", (m) -> {
            getBot("bot" + m.group(2)).add(AdventUtils.toInt(m.group(1)));
        }));
        
        // Match specs like "bot 2 gives low to bot 1 and high to output 3"
        actions.add(new Action("bot (\\d*) gives low to (bot|output) (\\d*) and high to (bot|output) (\\d*)", (m) -> {
            getBot("bot" + m.group(1)).setTransferInstructions(m.group(2) + m.group(3), m.group(4) + m.group(5));
        }));
        
        return actions;
    }
    
    public static Bot findBot(String[] instructions, Predicate<Bot> filter) {
        Collection<Action> actions = getActions();
        
        // For each instruction, apply all the actions, returning the first non-null result
        for (String instruction : instructions) {
            boolean matchedAction = false; 
            for (Action action : actions) {
                if (action.execute(instruction)) {
                    matchedAction = true;
                    break;
                }
            }
            
            // If there are no matches, fire an alert
            if (! matchedAction) {
                System.out.printf("ERROR: no Actions matched to work on instruction '%s'\n", instruction);
            }
        }

        // Now that the instructions have been processed, return the first bot that passes the filter
        return botsByName.values().stream().filter(filter).findFirst().orElse(null);
    }
    
    @Test
    public void testFindBot() {
        String[] instructions = {
                "value 5 goes to bot 2",
                "bot 2 gives low to bot 1 and high to bot 0",
                "value 3 goes to bot 1",
                "bot 1 gives low to output 1 and high to bot 0",
                "bot 0 gives low to output 2 and high to output 0",
                "value 2 goes to bot 2",
        };
        Predicate<Bot> predicate = (bot) -> (bot.getLowValue() == 2) && (bot.getHighValue() == 5);
        String expected = "bot2";
        assertEquals("findBot() incorrect", expected, findBot(instructions, predicate).name);
    }
    
    @Test
    public void testStarOne() {
        String[] instructions = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        Predicate<Bot> predicate = (bot) -> (bot.getLowValue() == 17) && (bot.getHighValue() == 61);
        assertEquals("star one findBot() is incorrect", "bot73", findBot(instructions, predicate).name);
    }

    @Test
    public void testStarTwo() {
        String[] instructions = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        Predicate<Bot> predicate = (bot) -> (bot.getLowValue() == 17) && (bot.getHighValue() == 61);
        System.out.printf("star one findBot() is %s\n", findBot(instructions, predicate).name);
        assertEquals("star two findBot() is incorrect", 3965, outputsByName.get("output0") * outputsByName.get("output1") * outputsByName.get("output2"));
//        outputsByName.entrySet().forEach(e -> System.out.printf("output %s=%d\n", e.getKey(), e.getValue()));
    }

    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day10-inputs.txt");
}
