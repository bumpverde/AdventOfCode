package com.verde.advent.y2016;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.verde.advent.AdventUtils;

/**
 * --- Day 4: Security Through Obscurity ---
 * Finally, you come across an information kiosk with a list of rooms. Of course, the list is encrypted and full of decoy data, 
 * but the instructions to decode the list are barely hidden nearby. Better remove the decoy data first.
 * 
 * Each room consists of an encrypted name (lowercase letters separated by dashes) followed by a dash, a sector ID, and a checksum in square brackets.
 * 
 * A room is real (not a decoy) if the checksum is the five most common letters in the encrypted name, in order, with ties broken by alphabetization. For example:
 * 
 * - aaaaa-bbb-z-y-x-123[abxyz] is a real room because the most common letters are a (5), b (3), and then a tie between x, y, and z, which are listed alphabetically.
 * - a-b-c-d-e-f-g-h-987[abcde] is a real room because although the letters are all tied (1 of each), the first five are listed alphabetically.
 * - not-a-real-room-404[oarel] is a real room.
 * - totally-real-room-200[decoy] is not.
 * 
 * Of the real rooms from the list above, the sum of their sector IDs is 1514.
 * 
 * What is the sum of the sector IDs of the real rooms?
 * 
 * Your puzzle answer was 173787.
 * 
 * --- Part Two ---
 * With all the decoy data out of the way, it's time to decrypt this list and get moving.
 * 
 * The room names are encrypted by a state-of-the-art shift cipher, which is nearly unbreakable without the right software. However, the information kiosk designers at Easter Bunny HQ were not expecting to deal with a master cryptographer like yourself.
 * 
 * To decrypt a room name, rotate each letter forward through the alphabet a number of times equal to the room's sector ID. A becomes B, B becomes C, Z becomes A, and so on. Dashes become spaces.
 * 
 * For example, the real name for qzmt-zixmtkozy-ivhz-343 is very encrypted name.
 * 
 * What is the sector ID of the room where North Pole objects are stored?
 * 
 * Your puzzle answer was 548. * 
 * @author bumpverde
 */
public class Y2016D04 {
    /** Match specs like "aaaaa-bbb-z-y-x-123[abxyz]" */
    private static final String SPEC_REGEX = "(.*)-(\\d*)\\[([a-z]*)\\]";
    private static final Pattern SPEC_PATTERN = Pattern.compile(SPEC_REGEX);
    
    public static String[] parseRealParts(String spec) {
        Matcher m = SPEC_PATTERN.matcher(spec);
        if (! m.matches()) {
            return null;
        }

        Predicate<Integer> isValidChar = (ch) -> (ch >= 'a') && (ch <= 'z');
        
        // Validate the first part
        String chars = m.group(1);
        if (chars.isEmpty() 
                || !isValidChar.test((int) chars.charAt(0))
                || !chars.chars().allMatch(ch -> (ch == '-') || isValidChar.test(ch))) {
            return null;
        }
        
        // Compute the frequencies
        Map<String,Long> frequencyByChar = 
                chars
                .chars()
                .filter(c -> c != '-')
                .mapToObj(c -> AdventUtils.toString((char) c))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        Comparator<String> sorter = (s1, s2) -> {
            Long freq1 = frequencyByChar.get(s1);
            Long freq2 = frequencyByChar.get(s2);
            
            return (freq1 == freq2) ? s1.compareTo(s2) : (int) (freq2 - freq1);
        };

        // Get the sorted frequencies and put them together into a String
        String expectedCheckSum =
                StringUtils.join(frequencyByChar.keySet().stream().sorted(sorter).limit(5).collect(Collectors.toList()), "");
        
        // Extract the sector ID
        int sectorId = Integer.parseInt(m.group(2).trim());
        
        // Validate the checksum
        String checkSum = m.group(3).trim();
        if (! expectedCheckSum.equals(checkSum)) {
            return null;
        }
        
        return new String[] { chars, String.valueOf(sectorId), checkSum };
    }
    
    public static int parseSectorId(String spec) {
        String[] parts = parseRealParts(spec);
        return (parts == null) ? 0 : Integer.parseInt(parts[1]);
    }
        
    public static int sumRealSectorIds(String[] specs) {
        return Arrays.stream(specs).mapToInt(Y2016D04::parseSectorId).sum();
    }

    public static int rotate(int c, int count) {
        // Dash goes to space
        if (c == '-') {
            return ' ';
        }
        
        // Space stays a space
        if (c == ' ') {
            return c;
        }
        
        // Chars rotate back to 'a' from 'z'
        int range = 26;
        count %= range;
        int newC = (((c - 'a') + count) % range) + 'a';
        return newC;
    }
    
    public static String getDecryptedName(String spec) {
        String[] parts = parseRealParts(spec);
        int sectorId = parseSectorId(spec);
        
        if ((parts == null) || (parts[0] == null) || (sectorId == 0)) {
            return null;
        }
        
        return AdventUtils.toString(parts[0].chars().map(c -> rotate(c, sectorId)));
    }
    
    public static int getNorthPoleSectorId(String[] specs) {
        return 
                Arrays
                .stream(specs)
                .filter(spec -> (getDecryptedName(spec) != null) && getDecryptedName(spec).contains("northpole")) 
                .map(spec -> parseSectorId(spec))
                .findFirst()
                .orElse(0);
    }
    
    @Test
    public void testParseSectorId() {
        String[] inputs = {
                "aaaaa-bbb-z-y-x-123[abxyz]",
                "a-b-c-d-e-f-g-h-987[abcde]",
                "not-a-real-room-404[oarel]", 
                "totally-real-room-200[decoy]"
        };
        
        int[] expected = { 123, 987, 404, 0 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("parseSectorId() incorrect", expected[i], parseSectorId(inputs[i]));
        }
    }

    @Test
    public void testSumSectorIds() {
        String[][] inputs = {
                {
                    "aaaaa-bbb-z-y-x-123[abxyz]",
                    "a-b-c-d-e-f-g-h-987[abcde]",
                    "not-a-real-room-404[oarel]", 
                    "totally-real-room-200[decoy]"
                },
                STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]),
        };
        
        int[] expected = { 1514, 173787 };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("sumRealSectorIds() incorrect", expected[i], sumRealSectorIds(inputs[i]));
        }
    }

    @Test
    public void testGetDecryptedName() {
        String[] inputs = {
                "qzmt-zixmtkozy-ivhz-343[zimth]"
        };
        
        String[] expected = { "very encrypted name" };
        
        for (int i=0; i<inputs.length; ++i) {
            assertEquals("getDecryptedName() incorrect", expected[i], getDecryptedName(inputs[i]));
        }
    }

    @Test
    public void testGetNorthPoleSectorId() {
        String[] inputs = STAR_INPUTS.toArray(new String[STAR_INPUTS.size()]);
        int expected = 548;
        
        assertEquals("getNorthPoleSectorId() incorrect", expected, getNorthPoleSectorId(inputs));
    }


    public static final List<String> STAR_INPUTS = AdventUtils.loadResourceStrings("/2016/day4-inputs.txt");
}
