package com.verde.mcsims;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link PiSimulator} approximates PI using a Monte Carlo simulation
 * that drops rain over a 2x2 square that has a circle inscribed in it.
 * See https://youtu.be/VJTFfIqO4TU.
 *
 * Area of circle = pi * r^2
 * Area of circle = 4 * r^2
 * Probability(water in circle) = (pi * r^2) / ( 4 * r^2)
 * pi = 4 * probability
 */
public class PiSimulator {
    static final long ONE_MILLION = 1000000L;
    static final long NANOS_PER_SECOND = 1000000000L;
    public static long MAX_ITERS = 100 * ONE_MILLION;

    private static AtomicLong numInCirlce = new AtomicLong(0);
    private static AtomicLong numInSquare = new AtomicLong(0);
    private static AtomicLong numEvals = new AtomicLong(0);
    private static AtomicBoolean done = new AtomicBoolean(false);

    public static SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance("NativePRNG");
        //return new SecureRandom();
    }

    public static void update(double x, double y) {
        boolean isInCircle = (x * x) + (y * y) <= (0.5 * 0.5);
        boolean isInSquare = (Math.abs(x) <= 0.5) && (Math.abs(y) <= 0.5);

        numEvals.getAndIncrement();

        if (isInCircle) {
            numInCirlce.getAndIncrement();
        }

        if (isInSquare) {
            numInSquare.getAndIncrement();
        }
    }

    public static class PointGenerator implements Runnable {
        private SecureRandom random = null;

        public PointGenerator() throws NoSuchAlgorithmException {
            random = getSecureRandom();
        }

        @Override
        public void run() {
            while (! done.get()) {
                double x = random.nextDouble() - 0.5;
                double y = random.nextDouble() - 0.5;
                update(x, y);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();

        // Start all the workers
        for (int i=0; i<4; ++i) {
            new Thread(new PointGenerator()).start();
        }

        // Wait until its done
        while (! done.get()) {
            Thread.sleep(1000);
            if (numEvals.get() > MAX_ITERS) {
                done.set(true);
            }
        }

        long stop = System.nanoTime();
        long duration = stop - start;
        double elapsedSecs = (double) duration / (double) NANOS_PER_SECOND;
        double opsPerSec = (double) MAX_ITERS / elapsedSecs;

        double pi = (double) (4 * numInCirlce.get()) / (double) numInSquare.get();
        System.out.printf("pi=%f, nSecs=%f, ops/sec=%,f\n", pi, elapsedSecs, opsPerSec);
    }
}
