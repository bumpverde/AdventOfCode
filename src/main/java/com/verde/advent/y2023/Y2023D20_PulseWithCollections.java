package com.verde.advent.y2023;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Y2023D20_PulseWithCollections {
    public static final String BROADCASTER = "broadcaster";
    public static final Collection<Module> NO_DESTINATIONS = new LinkedList<>();

    public static String getVoltage(boolean state) {
        return state ? "high" : "low";
    }

    public static class Pulse {
        public Module source;
        public boolean signal;
        public Module destination;
        public Collection<Module> destinations;

        public Pulse(Module source, boolean signal) {
            this(source, signal, NO_DESTINATIONS);
        }


        public Pulse(Module source, boolean signal, Collection<Module> destinations) {
            this.source = source;
            this.signal = signal;
            this.destinations = destinations;
        }

        public Pulse(Module source, boolean signal, Module destination) {
            this(source, signal, new LinkedList<>());
            destinations.add(destination);
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
         * Receive an input pulse signal and return a Pulse for any necessary outgoing signals, which may be null.
         *
         * @param input input Pulse, whose destinations are ignored
         * @return a Pulse for any outgoing signals from this module in response to the input, or null
         */
        public abstract Pulse pulse(Pulse input);

        public void logPulse(Pulse pulse, boolean skipped) {
            logPulse(pulse, skipped, null);
        }

        public void logPulse(Pulse pulse, boolean skipped, String msg) {
            if (pulse.source != null) {
                String prefix = skipped ? "\t" : "";
                String srcId = (pulse.source == null) ? "NULL" : pulse.source.id;
                String additionalMsg = (msg == null) ? "" : "(" + msg + ")";
                System.out.printf("%s%s: %s -%s-> %s %s\n", prefix, id, srcId, getVoltage(pulse.signal), id, additionalMsg);
            }
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
        public Pulse pulse(Pulse pulse) {
            Pulse output = null;

            // If signal is LOW calculate output and then toggle state
            if (! pulse.signal) {
                state = !state;
                output = new Pulse(this, state, destinations);
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
        public Map<String,Boolean> rememberedStates = new HashMap<>();

        public ConjunctionModule(String id) {
            super(id);
        }

        @Override
        public void addInput(Module m) {
            super.addInput(m);
            rememberedStates.put(m.id, false);  // init incoming input states to false
        }

        @Override
        public Pulse pulse(Pulse pulse) {
            // Remember the last input from the source
            rememberedStates.put(pulse.source.id, pulse.signal);

            // Determine signal to send based on all the inputs being remembered high
            AtomicBoolean allHigh = new AtomicBoolean(!inputs.isEmpty());
            inputs.forEach(c -> allHigh.set(allHigh.get() && rememberedStates.get(c.id)));

            // Return a request to signal all the destinations
            logPulse(pulse, false, String.format("allHigh=%b, sending %s", allHigh.get(), getVoltage(!allHigh.get())));
            return new Pulse(this, !allHigh.get(), destinations);
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
        public Pulse pulse(Pulse pulse) {
            logPulse(pulse, false);
            return new Pulse(this, pulse.signal, destinations);
        }
    }

    public static Map<String,Module> parseModules(Collection<String> input) {
        Map<String,Module> modules = new HashMap<>();

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
                    System.out.printf("ERROR: unable to resolve destination module '%s': %s\n", destId, line);
                }
                destModule.addInput(srcModule);
                srcModule.addDestination(destModule);
            });
        });

        return modules;
    }

    public static long computeAnswer(Map<String,Module> modules, boolean initialSignal, int nButtonPushes) {
        // Locate the broadcaster
        Module broadcaster = modules.get(BROADCASTER);
        if (broadcaster == null) {
            System.out.printf("ERROR: unable to resolve '%s' module\n", BROADCASTER);
            return -1l;
        }

        // Hook the button up as a trigger to the broadcaster
        BroadcastModule button = new BroadcastModule("button");
        button.addDestination(broadcaster);
        //Pulse buttonPush = button.pulse(new Pulse(null, initialSignal));  // no source module since elf is pushing the button
        //Pulse buttonPush = button.pulse(new Pulse(button, initialSignal));
        Pulse buttonPush = new Pulse(button, initialSignal, broadcaster);

        // Use a FIFO queue to process all the signals
        //BlockingQueue<Pulse> pulsesToProcess = new ArrayBlockingQueue<>(1000);
        Queue<Pulse> pulsesToProcess = new LinkedList<>();
        AtomicLong nLowSignals = new AtomicLong(0L);
        AtomicLong nHighSignals = new AtomicLong(0L);
        try {
            for (int i = 0; i < nButtonPushes; ++i) {
                //Pulse trigger = broadcaster.pulse(buttonPush);
                //pulsesToProcess.put(trigger);
                //pulsesToProcess.put(buttonPush);
                pulsesToProcess.add(buttonPush);

                //Output output = button.pulse(null, initialSignal);  // no source module since elf is pushing the button
                //Pulse output = broadcaster.pulse(button, initialSignal);
                //pulsesToProcess.put(output);

                while (pulsesToProcess.peek() != null) {
                    // Get the next output to process, which is really the next input signal to pulse
                    //Pulse nextPulse = pulsesToProcess.take();
                    Pulse pulse = pulsesToProcess.remove();

                    // Keep track of number of high/low signals
                    if (pulse.source != null) {
                        // Deliver the pulse signal and queue up whatever needs to be signaled next
                        for (Module destModule : pulse.destinations) {
                            Pulse futurePulse = destModule.pulse(pulse);
                            if (futurePulse != null) {
                                AtomicLong whichToInc = futurePulse.signal ? nHighSignals : nLowSignals;
                                whichToInc.incrementAndGet();
                                System.out.printf("\tHigh: %,d, Low: %,d\n", nHighSignals.get(), nLowSignals.get());

                                pulsesToProcess.add(futurePulse);
                            }
                        }

                    }


                    /**
                     for (Module destModule : pulse.destinations) {
                     Pulse futurePulse = destModule.pulse(pulse);
                     if (futurePulse != null) {
                     pulsesToProcess.add(futurePulse);
                     }
                     }
                     */
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
    public void testExamplePart1() {
        String[] inputs = {
                BROADCASTER + " -> a, b, c",
                "%a -> b",
                "%b -> c",
                "%c -> inv",
                "&inv -> a"
        };
        String[] expectedIds = {BROADCASTER, "a", "b", "c", "inv"};
        Class[] expectedTypes = {BroadcastModule.class, FlipFlopModule.class, FlipFlopModule.class, FlipFlopModule.class, ConjunctionModule.class};
        Map<String,Module> modules = parseModules(Arrays.asList(inputs));

        assertEquals("number of parsed modules incorrect", expectedIds.length, modules.size());
        for (int i=0; i<expectedIds.length; ++i) {
            String id = expectedIds[i];
            Module m = modules.get(id);
            assertNotNull("unable to resolve module " + id, m);
            assertEquals("incorrect module type for " + id, expectedTypes[i], m.getClass());
        }

        assertEquals("incorrect answer", 32L, computeAnswer(modules, false, 1));
    }

}
