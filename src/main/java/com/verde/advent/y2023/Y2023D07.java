package com.verde.advent.y2023;

import com.verde.advent.AdventUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Y2023D07 {
    public static final String BASIC_TEST_INPUTS = """
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483""";

    public static final String JOKER_REPLACEMENTS = "23456789TQKA";

    public static final Map<String,Integer> CARD_RANKS_BY_LABEL = new HashMap<>();
    static {
        setUseJokers(false);
    }

    public static boolean JACKS_ARE_JOKERS = false;

    public static void setUseJokers(boolean useJokers) {
        // Reset the way Jacks are treated
        JACKS_ARE_JOKERS = useJokers;

        // Set the card ranks accordingly
        CARD_RANKS_BY_LABEL.clear();
        if (!useJokers) {
            CARD_RANKS_BY_LABEL.put("A", 13);
            CARD_RANKS_BY_LABEL.put("K", 12);
            CARD_RANKS_BY_LABEL.put("Q", 11);
            CARD_RANKS_BY_LABEL.put("J", 10);
            CARD_RANKS_BY_LABEL.put("T", 9);
            CARD_RANKS_BY_LABEL.put("9", 8);
            CARD_RANKS_BY_LABEL.put("8", 7);
            CARD_RANKS_BY_LABEL.put("7", 6);
            CARD_RANKS_BY_LABEL.put("6", 5);
            CARD_RANKS_BY_LABEL.put("5", 4);
            CARD_RANKS_BY_LABEL.put("4", 3);
            CARD_RANKS_BY_LABEL.put("3", 2);
            CARD_RANKS_BY_LABEL.put("2", 1);
        } else {
            CARD_RANKS_BY_LABEL.put("A", 13);
            CARD_RANKS_BY_LABEL.put("K", 12);
            CARD_RANKS_BY_LABEL.put("Q", 11);
            CARD_RANKS_BY_LABEL.put("T", 10);
            CARD_RANKS_BY_LABEL.put("9", 9);
            CARD_RANKS_BY_LABEL.put("8", 8);
            CARD_RANKS_BY_LABEL.put("7", 7);
            CARD_RANKS_BY_LABEL.put("6", 6);
            CARD_RANKS_BY_LABEL.put("5", 5);
            CARD_RANKS_BY_LABEL.put("4", 4);
            CARD_RANKS_BY_LABEL.put("3", 3);
            CARD_RANKS_BY_LABEL.put("2", 2);
            CARD_RANKS_BY_LABEL.put("J", 1);
        }
    };

    public static enum HandType {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        private int rank;

        HandType(int rank) {
            this.rank = rank;
        }
    }

    public static int compareCards(String c1, String c2) {
        return Integer.compare(CARD_RANKS_BY_LABEL.get(c1), CARD_RANKS_BY_LABEL.get(c2));
    }

    private static String sortString(String input) {
        String sortedString = input.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .sorted((c1, c2) -> compareCards(c2, c1))  // descending order
                .map(String::valueOf)
                .collect(Collectors.joining());

        return sortedString;
    }

    public static class Hand implements Comparable {
        public HandType handType;
        public String cards;
        public int bid;

        public static Hand parseHand(String input) {
            String[] parts = StringUtils.split(input, " ");
            String cards = parts[0];
            int bid = Integer.parseInt(parts[1]);
            return new Hand(cards, bid);
        }

        public Hand(String cards, int bid) {
            this(cards, bid, JACKS_ARE_JOKERS);
        }

        public Hand(String cards, int bid, boolean jacksAreJokers) {
            this.cards = cards;
            this.bid = bid;

            this.handType = jacksAreJokers ? getHandTypeWithJokers(cards) : getHandType(cards);
        }

        public int getNumCards(char card) {
            return (int) cards.chars().filter(ch -> ch == card).count();
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", cards, handType);
        }

        public static HandType getHandType(String cards) {
            Map<Character, Long> charCountMap = cards.chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Sorting the map by count in descending order
            Map<Character, Long> sortedCharCountMap = charCountMap.entrySet().stream()
                    .sorted(Map.Entry.<Character, Long>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            // Determine the counts of the cards
            int index = 0;
            int[] cardCounts = new int[sortedCharCountMap.size()];
            for (long count : sortedCharCountMap.values()) {
                cardCounts[index++] = (int) count;
            }

            int nDistinctCards = cardCounts.length;

            if (nDistinctCards == 1) {
                return HandType.FIVE_OF_A_KIND;
            }

            if (cardCounts[0] == 4) {
                return HandType.FOUR_OF_A_KIND;
            }

            if ((nDistinctCards == 2) &&
                    (((cardCounts[0] == 3) && (cardCounts[1] == 2)) || ((cardCounts[0] == 2) && (cardCounts[1] == 3)))) {
                return HandType.FULL_HOUSE;
            }

            if (cardCounts[0] == 3) {
                return HandType.THREE_OF_A_KIND;
            }

            if ((nDistinctCards == 3) && (cardCounts[0] == 2) && (cardCounts[1] == 2)) {
                return HandType.TWO_PAIR;
            }

            if ((nDistinctCards > 1) && (cardCounts[0] == 2)) {
                return HandType.ONE_PAIR;
            }

            return HandType.HIGH_CARD;
        }

        public static HandType getHandTypeWithJokers(String cards) {
            // Compute all the wild hands
            Collection<String> possibilities =  calculateWildHands(cards);

            // Sort them and choose the highest hand type, passing false for jacksAreJokers to prevent infinite loops
            Hand highestHand = possibilities.stream().map(s -> new Hand(s, 0, false)).sorted((h1, h2) -> h2.compareTo(h1)).findFirst().orElse(null);
            return (highestHand != null) ? highestHand.handType : null;
        }

        public static Collection<String> calculateWildHands(String cards) {
            Collection<String> possibilities =  new LinkedList<>();

            if (cards.isEmpty()) {
                return possibilities;
            }

            // If no jokers, treat as a normal hand
            if (cards.indexOf("J") < 0) {
                possibilities.add(cards);
            } else {
                // Replace all the jokers with all the variants
                int startIndex = 0, indexJoker = -1;
                while ((startIndex < cards.length()) && ((indexJoker = cards.indexOf("J", startIndex)) >= 0)) {
                    String prefix = (indexJoker > 0) ? cards.substring(0, indexJoker) : "";
                    String suffix = (indexJoker+1 < cards.length()) ? cards.substring(indexJoker+1) : "";

                    // Compute all the wild variants of the suffix.
                    // Make sure if there are no suffixes we at least tack on all the replacements.
                    Collection<String> wildSuffixes = calculateWildHands(suffix);
                    if (wildSuffixes.isEmpty()) {
                        wildSuffixes.add("");
                    }

                    for (int i=0; i<JOKER_REPLACEMENTS.length(); ++i) {
                        for (String wildSuffix : wildSuffixes) {
                            String wildHand = String.format("%s%c%s", prefix, JOKER_REPLACEMENTS.charAt(i), wildSuffix);
                            possibilities.add(wildHand);
                        }
                    }

                    startIndex = indexJoker + 1;
                }
            }

            return possibilities;
        }

        @Override
        public int compareTo(Object o) {
            if (! (o instanceof Hand)) {
                return 0;
            }

            Hand rhs = (Hand) o;

            // If they are not the same hand type, then rank based on the type
            if (handType != rhs.handType) {
                return Integer.compare(handType.rank, rhs.handType.rank);
            }

            // Else, they are the same rank, so go card by card, returning the first difference
            for (int i=0; i<cards.length(); ++i) {
                int comparison =  compareCards(String.valueOf(cards.charAt(i)), String.valueOf(rhs.cards.charAt(i)));
                if (comparison != 0) {
                    return comparison;
                }
            }

            return 0;
        }
    }

    public static List<Hand> parseHands(Collection<String> inputs) {
        List<Hand> hands =
                inputs
                        .stream()
                        .filter(s -> !s.trim().isEmpty())
                        .map(s -> Hand.parseHand(s))
                        .sorted()
                        .collect(Collectors.toList());

        return hands;
    }

    public static long computeWinnings(Collection<Hand> hands) {
        AtomicInteger rank = new AtomicInteger(1);
        long winnings = hands.stream().mapToLong(h -> h.bid * rank.getAndIncrement()).sum();

        return winnings;
    }

    @Test
    public void testSort() {
        setUseJokers(false);
        String[] inputs = {
                "23456789TJQKA"
        };
        String[] expected = {
                "AKQJT98765432"
        };

        for (int i=0; i<inputs.length; ++i) {
            String actual = sortString(inputs[i]);
            assertEquals("Rank sorting incorrect", expected[i], actual);
        }
    }

    @Test
    public void testHandTypes() {
        setUseJokers(false);
        String[] inputs = {
                "23456",
                "23426",
                "23423",
                "23433",
                "ATAAA",
                "A2222",
                "2A222",
                "AAAAA",
                "AKAKA",
                "TAAAT"
        };
        HandType[] expected = {
                HandType.HIGH_CARD,
                HandType.ONE_PAIR,
                HandType.TWO_PAIR,
                HandType.THREE_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FIVE_OF_A_KIND,
                HandType.FULL_HOUSE,
                HandType.FULL_HOUSE
        };

        for (int i=0; i<inputs.length; ++i) {
            HandType actual = Hand.getHandType(inputs[i]);
            assertEquals("HandType incorrect", expected[i], actual);
        }
    }

    @Test
    public void testHandStrengths() {
        setUseJokers(false);
        String[][] inputs = {
                { "5432A", "432A5" },
                { "77888", "77788" },
                { "33332", "2AAAA" }
        };

        for (int i=0; i<inputs.length; ++i) {
            Hand stronger = new Hand(inputs[i][0], 0);
            Hand weaker = new Hand(inputs[i][1], 0);
            int strongerComparison = stronger.compareTo(weaker);
            int weakerComparison = weaker.compareTo(stronger);

            assertTrue(String.format("HandType strength incorrect against self: %s", stronger.cards), stronger.compareTo(stronger) == 0);
            assertTrue(String.format("HandType strength incorrect against self: %s", weaker.cards), weaker.compareTo(weaker) == 0);
            assertTrue(String.format("HandType strength incorrect: %s and %s is %d", stronger.cards, weaker.cards, strongerComparison), strongerComparison > 0);
            assertTrue(String.format("HandType strength incorrect: %s and %s is %d", weaker.cards, stronger.cards, weakerComparison), weakerComparison < 0);
        }
    }

    @Test
    public void testBasicInputsPart1() {
        setUseJokers(false);
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS, "\n")).collect(Collectors.toList());
        List<Hand> hands = parseHands(inputs);
        long expected = 6440L;
        long actual = computeWinnings(hands);

        assertEquals("Basic inputs incorrect", expected, actual);
    }

    @Test
    public void testPart1() {
        setUseJokers(false);
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day7-p1.txt");
        List<Hand> hands = parseHands(inputs);
        long expected = 249638405;
        long actual = computeWinnings(hands);


        System.out.printf("Day7 Part1: %d\n", actual);
        assertEquals("Part1 winnings calculation incorrect", expected, actual);
    }

    @Test
    public void testJokers() {
        setUseJokers(true);
        String[] inputs = {
                "K898J",
                "JJJJJ",
                "JJJJ8",
                "QJJQ2",
                "T55J5",
                "KTJJT",
                "QQQJA",
                "77373",
        };
        HandType[] expected = {
                HandType.THREE_OF_A_KIND,
                HandType.FIVE_OF_A_KIND,
                HandType.FIVE_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FOUR_OF_A_KIND,
                HandType.FULL_HOUSE
        };

        for (int i=0; i<inputs.length; ++i) {
            HandType actual = Hand.getHandTypeWithJokers(inputs[i]);
            assertEquals("HandType with Jokers incorrect", expected[i], actual);
        }
    }

    @Test
    public void testBasicInputsPart2() {
        setUseJokers(true);
        Collection<String> inputs = Stream.of(StringUtils.split(BASIC_TEST_INPUTS, "\n")).collect(Collectors.toList());
        List<Hand> hands = parseHands(inputs);
        long expected = 5905L;
        long actual = computeWinnings(hands);

        assertEquals("Basic inputs Part2 incorrect", expected, actual);
    }

    @Test
    public void testPart2() {
        setUseJokers(true);
        Collection<String> inputs = AdventUtils.loadResourceStrings("/2023/day7-p1.txt");
        List<Hand> hands = parseHands(inputs);
        long expected = 249776650;
        long actual = computeWinnings(hands);

        System.out.printf("Day7 Part2: %d\n", actual);
        assertEquals("Part2 winnings calculation incorrect", expected, actual);
    }
}
