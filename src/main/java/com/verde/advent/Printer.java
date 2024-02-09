package com.verde.advent;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

public interface Printer extends Closeable {
    /**
     * Print a String, probably formatted with String.format().
     *
     * @param s the String to print
     */
    void print(String s);

    default void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    default void printf(Supplier<String> supplier) {
        String s = get(supplier);
        if (s != null) {
            print(s);
        }
    }

    default void println() {
        print("\n");
    }

    default void println(String s) {
        print(s);
        print("\n");
    }

    default void println(Supplier<String> supplier) {
        String s = get(supplier);
        if (s != null) {
            println(s);
        }
    }

    default String get(Supplier<String> supplier) {
        return supplier.get();
    }

    @Override
    void close();
    public static Printer fromDevNull() {
       return new Printer() {
            public void print(String s) {}
            public void close() {}
           public String get(Supplier<String> supplier) { return null; }
        };
    }

    public static Printer fromStdOut() {
        return new Printer() {
            public void print(String s) { System.out.print(s); }
            public void close() {}
        };
    }

    public static Printer fromFile(String fileName) throws IOException {
        final FileWriter fw = new FileWriter(fileName);

        return new Printer() {
            public void print(String s) {
                try {
                    fw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void close() {
                try {
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Printer fromFileSilent(String fileName) {
        try {
            return fromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
