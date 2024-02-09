package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Y2023D20 {
    public static final String BROADCASTER = "broadcaster";
    public static final String OUTPUT = "output";
    public static final String RX = "rx";

    // Config settings
    public static boolean LOG_PULSES = false;

    public static String getVoltage(boolean state) {
        return state ? "high" : "low";
    }

    public static class PassResults {
        int passNum;
        long numLowPulses;
        long numHighPulses;

        public PassResults(int passNum, long numLowPulses, long numHighPulses) {
            this.passNum = passNum;
            this.numLowPulses = numLowPulses;
            this.numHighPulses = numHighPulses;
        }
    }

    public static class Pulse {
        public Module source;
        public boolean signal;
        public Module destination;
        public int pass; // the pass number, defaults to -1 but is set by the outer algorithm

        public Pulse(Module source, boolean signal, Module destination) {
            this.source = source;
            this.signal = signal;
            this.destination = destination;
            this.pass = -1;  // will be reset by the outer algorithm
        }
    }

    public static abstract class Module {
        public String id;
        public Collection<Module> inputs;
        public Collection<Module> destinations;

        public Module(String id) {
            this.id = id;
            this.inputs = new LinkedHashSet<>();
            this.destinations = new LinkedHashSet<>();
        }

        public void addInput(Module m) {
            inputs.add(m);
        }

        public void addDestination(Module m) {
            destinations.add(m);
        }

        /**
         * Receive an input pulse signal and return Pulses for any necessary outgoing signals, which may be null.
         *
         * @param input input Pulse, whose destinations are ignored
         * @return a Collection<Pulse></Pulse> for any outgoing signals from this module in response to the input, or null
         */
        public abstract Collection<Pulse> pulse(Pulse input);

        public void logPulse(Pulse pulse, boolean skipped) {
            logPulse(pulse, skipped, null);
        }

        public void logPulse(Pulse pulse, boolean skipped, String msg) {
            if (LOG_PULSES && (pulse.source != null)) {
                String prefix = skipped ? "\t" : "";
                String srcId = (pulse.source == null) ? "NULL" : pulse.source.id;
                String additionalMsg = (msg == null) ? "" : "(" + msg + ")";
                System.out.printf("%s%s: %s -%s-> %s %s\n", prefix, id, srcId, getVoltage(pulse.signal), id, additionalMsg);
            }
        }

        public Collection<Pulse> createPulses(boolean signal) {
            return destinations.stream().map(m -> new Pulse(this, signal, m)).collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Module module = (Module) o;
            return id.equals(module.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * Flip-flop modules (prefix %) are either on or off; they are initially off.
     * If a flip-flop module receives a high pulse, it is ignored and nothing happens.
     * However, if a flip-flop module receives a low pulse, it flips between on and off.
     * If it was off, it turns on and sends a high pulse.
     * If it was on, it turns off and sends a low pulse.
     */
    public static class FlipFlopModule extends Module {
        public boolean state = false;

        public FlipFlopModule(String id) {
            super(id);
        }

        @Override
        public Collection<Pulse> pulse(Pulse pulse) {
            Collection<Pulse> output = null;

            // If signal is LOW calculate output and then toggle state
            if (!pulse.signal) {
                state = !state;
                output = createPulses(state);
                logPulse(pulse, false, "now " + getVoltage(state));
            } else {
                logPulse(pulse, true, "skipping HIGH signal from " + pulse.source.id);
            }

            return output;
        }
    }

    /**
     * Conjunction modules (prefix &) remember the type of the most recent pulse received from each of
     * their connected input modules; they initially default to remembering a low pulse for each input.
     * When a pulse is received, the conjunction module first updates its memory for that input.
     * Then, if it remembers high pulses for all inputs, it sends a low pulse; otherwise, it sends a high pulse.
     */
    public static class ConjunctionModule extends Module {
        public Map<String, Boolean> rememberedStates = new HashMap<>();

        // Maintain a Map whose keys are the input Module IDs and values are a Collection of pass counts
        // where the input from that module was high. We'll use that to determine the frequencies of each.
        public Map<String,Integer> highPulsePassesById = new HashMap<>();

        public ConjunctionModule(String id) {
            super(id);
        }

        @Override
        public void addInput(Module m) {
            super.addInput(m);
            rememberedStates.put(m.id, false);  // init incoming input states to false
        }

        @Override
        public Collection<Pulse> pulse(Pulse pulse) {
            // Remember the last input from the source
            rememberedStates.put(pulse.source.id, pulse.signal);

            // Determine signal to send based on all the inputs being remembered high
            AtomicBoolean allHigh = new AtomicBoolean(!inputs.isEmpty());
            inputs.forEach(c -> allHigh.set(allHigh.get() && rememberedStates.get(c.id)));

            // Return a request to signal all the destinations
            logPulse(pulse, false, String.format("allHigh=%b, sending %s", allHigh.get(), getVoltage(!allHigh.get())));

            // Maintain the first pass count for inputs that came in high
            if (pulse.signal) {
                if (!highPulsePassesById.containsKey(pulse.source.id)) {
                    highPulsePassesById.put(pulse.source.id, pulse.pass);
                    //System.out.printf("\t%s: module %s came in HIGH on pass %,d\n", id, pulse.source.id, pulse.pass);
                }
            }

            return createPulses(!allHigh.get());
        }
    }

    /**
     * There is a single broadcast module (named broadcaster).
     * When it receives a pulse, it sends the same pulse to all of its destination modules.
     */
    public static class BroadcastModule extends Module {
        public BroadcastModule(String id) {
            super(id);
        }

        @Override
        public Collection<Pulse> pulse(Pulse pulse) {
            logPulse(pulse, false);
            return createPulses(pulse.signal);
        }
    }

    /**
     * An OutputModule is not well-defined, but it seems to be a dead-end log to the console.
     */
    public static class OutputModule extends Module {
        public OutputModule(String id) {
            super(id);
        }

        @Override
        public Collection<Pulse> pulse(Pulse pulse) {
            logPulse(pulse, true, "OUTPUT");
            return null;
        }
    }

    /**
     * An RxModule is the final machine to turn on, which it does when it receives a single low pulse.
     */
    public static class RxModule extends Module {
        public boolean state = false;

        public RxModule(String id) {
            super(id);
        }

        @Override
        public Collection<Pulse> pulse(Pulse pulse) {
            if (!pulse.signal) {
                state = true;
                System.out.printf("RX is turning ON!!");
            }

            logPulse(pulse, false, "state is " + getVoltage(state));
            return null;
        }
    }

    public static Map<String, Module> parseModules(Collection<String> input) {
        Map<String, Module> modules = new HashMap<>();

        // Make a first pass thru and parse all the Modules.
        // Most of them make forward references to modules that are not created yet (2nd pass work).
        input.stream().map(String::trim).filter(s -> !s.isEmpty()).forEach(line -> {
            String[] parts = StringUtils.splitByWholeSeparator(line, " -> ");
            String id = parts[0];
            Module m = null;

            if (id.equals(BROADCASTER)) {
                m = new BroadcastModule(id);
            } else if (id.startsWith("%")) {
                m = new FlipFlopModule(id.substring(1));
            } else if (id.startsWith("&")) {
                m = new ConjunctionModule(id.substring(1));
            } else {
                System.out.printf("ERROR: %s\n", line);
            }

            if (m != null) {
                modules.put(m.id, m);
            }

            // The "output" module is odd because it only appears as a destination.
            // If it is in the destination list, make sure to create one now.
            if (parts[1].contains(OUTPUT)) {
                modules.computeIfAbsent(OUTPUT, k -> new OutputModule(OUTPUT));
            }

            // Part2: The "rx" module is also odd because it only appears as a destination.
            // If it is in the destination list, make sure to create one now.
            if (parts[1].contains(RX)) {
                modules.computeIfAbsent(RX, k -> new RxModule(RX));
            }
        });

        // Make a second pass thru and set all the source/destination modules
        input.stream().map(String::trim).filter(s -> !s.isEmpty()).forEach(line -> {
            String[] parts = StringUtils.splitByWholeSeparator(line, " -> ");
            String srcId = parts[0];

            if (srcId.startsWith("%") || srcId.startsWith("&")) {
                srcId = srcId.substring(1);
            }

            Module srcModule = modules.get(srcId);
            if (srcModule == null) {
                System.out.printf("ERROR: unable to resolve source module '%s': %s\n", srcId, line);
            }

            // Add destinations and inputs
            String[] destIds = StringUtils.split(parts[1].trim(), ",");
            Stream.of(destIds).map(String::trim).forEach(destId -> {
                Module destModule = modules.get(destId);
                if (destModule == null) {
                    System.out.printf("ERROR: unable to resolve destination module '%s': %s  --- using an OutputModule\n", destId, line);
                    destModule = new OutputModule(destId);
                    modules.put(destId, destModule);
                }

                destModule.addInput(srcModule);
                srcModule.addDestination(destModule);
            });
        });

        return modules;
    }

    public static long computeAnswer(Map<String, Module> modules, boolean initialSignal, int nButtonPushes) {
        return computeAnswer(modules, initialSignal, nButtonPushes, null);
    }

    public static long computeAnswer(Map<String, Module> modules, boolean initialSignal, int nButtonPushes, Predicate<PassResults> passPredicate) {
        // Locate the broadcaster
        Module broadcaster = modules.get(BROADCASTER);
        if (broadcaster == null) {
            System.out.printf("ERROR: unable to resolve '%s' module\n", BROADCASTER);
            return -1l;
        }

        // Hook the button up as a trigger to the broadcaster
        BroadcastModule button = new BroadcastModule("button");
        button.addDestination(broadcaster);
        Pulse buttonPush = new Pulse(button, initialSignal, broadcaster);

        // Use a FIFO queue to process all the signals
        Queue<Pulse> pulsesToProcess = new LinkedList<>();
        AtomicLong nLowSignals = new AtomicLong(0L);
        AtomicLong nHighSignals = new AtomicLong(0L);
        try {
            for (int i = 0; i < nButtonPushes; ++i) {
                pulsesToProcess.add(buttonPush);

                while (pulsesToProcess.peek() != null) {
                    // Get the next output to process, which is really the next input signal to pulse
                    Pulse pulse = pulsesToProcess.remove();

                    // Keep track of number of high/low signals
                    if (pulse.source != null) {
                        AtomicLong whichToInc = pulse.signal ? nHighSignals : nLowSignals;
                        whichToInc.incrementAndGet();
                        //System.out.printf("\tHigh: %,d, Low: %,d\n", nHighSignals.get(), nLowSignals.get());

                        // Set the pass on the pulse and deliver the pulse signal and queue up whatever needs to be signaled next.
                        // Setting the pass allows modules to record interesting data based on the pass, necessary for Part2.
                        pulse.pass = i+1;  // i=0 is button push #1
                        Collection<Pulse> futurePulses = pulse.destination.pulse(pulse);
                        if (futurePulses != null) {
                            pulsesToProcess.addAll(futurePulses);
                        }
                    }
                }

                // That wraps up this pass. Invoke the PassResults predicate, if there is one, to see if we should stop
                if (passPredicate != null) {
                    if (!passPredicate.test(new PassResults(i, nLowSignals.get(), nHighSignals.get()))) {
                        break;
                    }
                }

                if ((i % 500_000) == 0) {
                    System.out.printf("Pass %,d\n", i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long answer = nLowSignals.get() * nHighSignals.get();
        System.out.printf("\nHigh: %,d, Low: %,d, Answer: %,d\n", nHighSignals.get(), nLowSignals.get(), answer);
        return answer;
    }

    @Test
    public void testExample1Part1() {
        String[] inputs = {
                BROADCASTER + " -> a, b, c",
                "%a -> b",
                "%b -> c",
                "%c -> inv",
                "&inv -> a"
        };
        String[] expectedIds = {BROADCASTER, "a", "b", "c", "inv"};
        Class[] expectedTypes = {BroadcastModule.class, FlipFlopModule.class, FlipFlopModule.class, FlipFlopModule.class, ConjunctionModule.class};
        Map<String, Module> modules = parseModules(Arrays.asList(inputs));

        assertEquals("number of parsed modules incorrect", expectedIds.length, modules.size());
        for (int i = 0; i < expectedIds.length; ++i) {
            String id = expectedIds[i];
            Module m = modules.get(id);
            assertNotNull("unable to resolve module " + id, m);
            assertEquals("incorrect module type for " + id, expectedTypes[i], m.getClass());
        }

        assertEquals("incorrect answer for 1 button push", 32L, computeAnswer(modules, false, 1));

        modules = parseModules(Arrays.asList(inputs));
        assertEquals("incorrect answer for 1000 button pushes", 32000000L, computeAnswer(modules, false, 1000));
    }

    @Test
    public void testExample2Part1() {
        String[] inputs = {
                BROADCASTER + " -> a",
                "%a -> inv, con",
                "&inv -> b",
                "%b -> con",
                "&con -> output",
        };
        String[] expectedIds = {BROADCASTER, "a", "b", "inv", "con", "output"};
        Class[] expectedTypes = {BroadcastModule.class, FlipFlopModule.class, FlipFlopModule.class, ConjunctionModule.class, ConjunctionModule.class, OutputModule.class};
        Map<String, Module> modules = parseModules(Arrays.asList(inputs));

        assertEquals("number of parsed modules incorrect", expectedIds.length, modules.size());
        for (int i = 0; i < expectedIds.length; ++i) {
            String id = expectedIds[i];
            Module m = modules.get(id);
            assertNotNull("unable to resolve module " + id, m);
            assertEquals("incorrect module type for " + id, expectedTypes[i], m.getClass());
        }

        assertEquals("incorrect answer for 1 button push", 16L, computeAnswer(modules, false, 1));

        modules = parseModules(Arrays.asList(inputs));
        assertEquals("incorrect answer for 1000 button pushes", 11687500L, computeAnswer(modules, false, 1000));
    }

    @Test
    public void testPart1() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day20-p1.txt");  // "rx" is not a defined module!
        Map<String, Module> modules = parseModules(inputs);

        assertEquals("incorrect answer Part1", 712_543_680L, computeAnswer(modules, false, 1000));
    }

    /**
     * Create and return a Predicate<PassResults> that will report out the first time the RxModule turns on.
     * This Predicate will return false when that happens, which will halt continued pass evaluation.
     *
     * @param rx the RxModule
     * @return the Predicate<PassResults>
     */
    public Predicate<PassResults> createRxObserver(RxModule rx) {
        return (pr) -> {
            if (rx.state) {
                System.out.printf("RxModule is turning on!! Pass=%d, High: %,d, Low: %,d\n", pr.passNum, pr.numHighPulses, pr.numLowPulses);
            }

            return !rx.state;
        };
    }

    /**
     * Create and return a Predicate<PassResults> that will report out the first time all the inputs
     * for the vr module have signalled high (not necessarily together, but independently).
     * If we have all the initial HIGH pass counts for the inputs, then we have the answer for Part2.
     *
     * This Predicate will return false when that happens, which will halt continued pass evaluation.
     *
     * @param modules the modules
     * @return the Predicate<PassResults>
     */
    public Predicate<PassResults> createVrObserver(Map<String,Module> modules) {
        ConjunctionModule vr = (ConjunctionModule) modules.get("vr");

        return (prPred) -> {
            // If we have all the initial HIGH pass counts for the inputs, then we have the answer for Part2
            if (vr.highPulsePassesById.size() == vr.inputs.size()) {
                BigInteger part2Answer = new BigInteger("1", 10);

                for (int pass : vr.highPulsePassesById.values()) {
                    part2Answer = part2Answer.multiply(new BigInteger(String.valueOf(pass), 10));
                }

                System.out.println();
                System.out.println("========================================");
                System.out.printf("Part2 answer: %d\n", part2Answer);
                System.out.println("========================================");

                return false;  // halt processing
            }

            return true; // continue processing
        };
    }

    @Test
    public void testPart2() {
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day20-p1.txt");
        Map<String, Module> modules = parseModules(inputs);
        Predicate<PassResults> vrObserver = createVrObserver(modules);

        // The answer is the rx module turns on at button push 238920142622879,
        // which is the product of the frequencies of each of the four inputs to vr coming in high:
        //   vr: module dk came in HIGH on pass 3,793
        //   vr: module fg came in HIGH on pass 3,929
        //   vr: module pq came in HIGH on pass 4,001
        //   vr: module fm came in HIGH on pass 4,007
        // 3793 * 3929 * 4001 * 4007 = 238920142622879
        //
        // The inspiration for this approach came from https://simontoth.substack.com/p/daily-bite-of-c-advent-of-code-day-290
        computeAnswer(modules, false, 5_000, vrObserver);
    }
}
