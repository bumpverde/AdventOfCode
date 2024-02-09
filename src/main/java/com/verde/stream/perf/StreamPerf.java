package com.verde.stream.perf;

import java.util.function.Supplier;
import java.util.stream.LongStream;

public class StreamPerf {
	static final long ONE_MILLION = 1000000L;
	static final long NANOS_PER_SECOND = 1000000000L;
	
	public static long MAX_ITERS = 500 * ONE_MILLION;
	public static LongStream VALUES = LongStream.range(0, MAX_ITERS);
	public static long[] ARRAY_VALUES = new long[(int) MAX_ITERS];
	static {
		for (int i=0; i<MAX_ITERS; ++i) {
			ARRAY_VALUES[i] = (long) i;
		}
	}
	
	public static long filterMapReduce() {
		return VALUES.filter(x -> (x % 2) == 0).map(x -> x + 1).sum();
	}

	public static long filterMapReduceArray(long[] vals) {
		long sum = 0;
		
		for (int i=0; i<MAX_ITERS; ++i) {
			long x = vals[i];
			if ((x % 2) == 0) {
				sum += x + 1;
			}
		}
		
		return sum;
	}

	public static void time(Supplier<Long> supplier, String msg) {
		long start = System.nanoTime();
		long result = supplier.get();
		long stop = System.nanoTime();
		long duration = stop - start;
		double elapsedSecs = (double) duration / (double) NANOS_PER_SECOND;
		double opsPerSec = (double) MAX_ITERS / elapsedSecs;
		
		System.out.printf("%s: result=%d, nSecs=%f, ops/sec=%,f\n", msg, result, elapsedSecs, opsPerSec);
	}
	
	public static void main(String[] args) {
		time(() -> filterMapReduce(), "Filter, Map, Reduce");
		time(() -> filterMapReduceArray(ARRAY_VALUES), "Filter, Map, Reduce Array");
	}
}
