package com.verde.advent.y2018.d4;

import java.util.regex.Pattern;

public class Y2018D04 {
    
    
    public static class Event {
        /** Match specs like "#3 @ 5,5: 2x2" */
        private static final String SPEC_REGEX = "#(\\d*)\\s@\\s(\\d*),(\\d*):\\s(\\d*)x(\\d*)";
        private static final Pattern SPEC_PATTERN = Pattern.compile(SPEC_REGEX);

        enum Type {
            ARRIVE, ASLEEP, AWAKE
        };
        
        private int year;
        private int month;
        private int day;
        private int min;
        private Type type;
        
        public static Event parseFromSpec(String spec) {
        
            return null;
        }
        
        public Event(Type type, int year, int month, int day, int min) {
            this.type = type;
            this.year = year;
            this.month = month;
            this.day = day;
            this.min = min;
        }
        
        public int getMonth() {
            return month;
        }
        public int getDay() {
            return day;
        }
        public int getMin() {
            return min;
        }
        public Type getType() {
            return type;
        }
        
    }
}
