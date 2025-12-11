package com.verde.advent;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.function.BiFunction;

public class StreamUtils {
    public static <A, B, R> Stream<? extends R> zip(
            Stream<A> stream1,
            Stream<B> stream2,
            BiFunction<? super A, ? super B, ? extends R> combiner) {
        Iterator<A> iterator1 = stream1.iterator();
        Iterator<B> iterator2 = stream2.iterator();

        // Stream.generate creates an infinite stream, limit ensures it doesn't go on forever.
        return Stream.generate(() -> combiner.apply(iterator1.next(), iterator2.next()))
                .takeWhile(x -> iterator1.hasNext() && iterator2.hasNext());
    }
}

