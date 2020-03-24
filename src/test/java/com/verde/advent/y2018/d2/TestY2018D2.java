package com.verde.advent.y2018.d2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestY2018D2 {
    public static String[] STAR_INPUTS = {
            "umdryebvlapkozostecnihjexg", "amdryebalapkozfstwcnrhjqxg", "umdcyebvlapaozfstwcnihjqgg", "ymdryrbvlapkozfstwcuihjqxg", "umdrsebvlapkozxstwcnihjqig", "umdryibvlapkohfstwcnfhjqxg", "umdryebvqapkozfatwcnihjqxs", "umzrpebvlapkozfshwcnihjqxg", "fmhryebvlapkozfstwckihjqxg", "umdryebvlahkozfstwcnizjrxg", "qmdryebvlapkozfslwcnihgqxg", "umdiyebjlapknzfstwcnihjqxg", "umdryebvlapkoqfstwcaihvqxg", "cmdryebvlapkpzfstwcnihjvxg", "umdryebvlakkozfstwcgihjixg", "umdryebvlasjozfstwcnihqqxg", "umdryebvladkozfsvwcnifjqxg", "umdrlebvlapaozfstwcniwjqxg", "umdryebvlhpkozrstwsnihjqxg", "umdryebvcapkozfqtwcnihjrxg", "ubdrykbvlapkowfstwcnihjqxg", "umdryebvldpkozfstwcnihtqsg", "umdryebvlapaozyutwcnihjqxg", "umdryibvlapkozfstdfnihjqxg", "umdryebvlapgozkstwznihjqxg", "umdrxebvlapkozfstwcngxjqxg", "umdryekvlapkozfstwclchjqxg", "nmdryebvlapkozjsewcnihjqxg", "umdryebvyapkozfstfcniheqxg", "umdfyebvlapkozfstwcnhhjpxg", "umdryelylupkozfstwcnihjqxg", "smdryebvlqpkozfstwcnihjdxg", "umdryebvlapaozfsuwcnihjqxc", "umdryebvlrzkozrstwcnihjqxg", "umdbycbvlapkojfstwcnihjqxg", "umdryebvlapkonfstwpnirjqxg", "uecryebvlapkozfstwcnihpqxg", "uqdryebvltpkozfstwcnihrqxg", "umdryebvlqsknzfstwcnihjqxg", "cmdryebvlapkocfstwcvihjqxg", "umdrkebvlapkozqsfwcnihjqxg", "umdryabveapkoifstwcnihjqxg", "ummrnehvlapkozfstwcnihjqxg", "umdryebvlxpkozfstwqnihjtxg", "umdryebvlagkozastwcnihjqxh", "umdryebvlatkozzhtwcnihjqxg", "umdryebvlcpkozfstwrnihjqvg", "umdryebvlapkozfsnwcnrhjcxg", "umdzyebvlypkozfstwcnibjqxg", "nmdryebvlvpkozbstwcnihjqxg", "uwdryebvlipkozfstwcnihvqxg", "umdraebvlavkozfstwcnihjqwg", "umdeyebvlspbozfstwcnihjqxg", "umdryxlvlapkozfstwcnihjqxu", "umdryegvlapkqzfstwcnirjqxg", "umdrupbvlapkozfstwcnihjqog", "imxryebvlapkxzfstwcnihjqxg", "umdrfebvlapkozowtwcnihjqxg", "umdreebvlapkozmstwczihjqxg", "undryebdlapkozbstwcnihjqxg", "umdryebvlapkpzfetwcnihjqxb", "ymdnyebvlapkozfstwinihjqxg", "umdryebvaapkozfstwcnihyqqg", "umdryebvlapkzzwsrwcnihjqxg", "umdrkebvlapkmzfskwcnihjqxg", "umdrmebvlapkozfsvwcnidjqxg", "umdlyehvlapkozfstwcnihjqkg", "umnryebvlrpkozfstwjnihjqxg", "uqdryebvlapxozfsawcnihjqxg", "vmdruebvlapkozfstwcnihjqqg", "umdryabviapkozistwcnihjqxg", "umdryebvlapkzzfstwfnihkqxg", "uvdryebvlapkozfsxwcuihjqxg", "umdlhebvlapkozfstwcnvhjqxg", "umdreebvlapkopfstjcnihjqxg", "umdryebvlazkomfstwynihjqxg", "kmdryebulapkoznstwcnihjqxg", "umdryebvxakkozfstwinihjqxg", "ukdryobvlapkozistwcnihjqxg", "umdryebveapkozfstwcnthjqgg", "mmdrtebvlapcozfstwcnihjqxg", "umdryebvlapkolistwnnihjqxg", "umdryebxlapkozfatwcnihjqxx", "uxdryebvlapkozfstwhniheqxg", "ufdryebvzapkozfstwcnbhjqxg", "amdryhbvlapkozfstwcnifjqxg", "umqryebvlaphozfstwcnihjqxn", "umdryebvlapkosfstfcnihjqxe", "gmkryebvlapkozfstwcnihjmxg", "umdrnebvlkpkozfstwcnihjnxg", "umdryebvrapkozfstmcndhjqxg", "umdryebvmapkozfstichihjqxg", "umdryesvnapkozestwcnihjqxg", "umeryhbvlapkozfstfcnihjqxg", "umdryedvbapkozfstwcnihqqxg", "umdryebllapzozfstwcnihjvxg", "umdcyebvlzdkozfstwcnihjqxg", "umdrybbvlapkbvfstwcnihjqxg", "umdrytbglapkozfsthcnihjqxg", "umdryebvlkpkozfsteclihjqxg", "umdntebvlapkmzfstwcnihjqxg", "lkdryebveapkozfstwcnihjqxg", "ymdryubvlapkozfstwbnihjqxg", "tmrryebvlapkozfstwcnqhjqxg", "umdryeovlaekonfstwcnihjqxg", "umiryeuvlapkozfstwcnihjwxg", "umdryebvlspvozwstwcnihjqxg", "umdrtebvlapkoznxtwcnihjqxg", "umvryebvlaphozfstwcnahjqxg", "umdryebvlapkozfstinniajqxg", "umdryebqlapkozfctwcnihjqxx", "umdryebvlapkbzfptwcnihjqvg", "umdryabviapkozistwcnihjqxd", "umdryrbvlapkezfstscnihjqxg", "umhryebvlapkozfstacnihxqxg", "umdxyelvlapkozfitwcnihjqxg", "umdryevvuapkozfstwcnihtqxg", "uydrypbvxapkozfstwcnihjqxg", "umdryebvlapkopfstwcnihzqxo", "uedryebvlapkozistwceihjqxg", "umdiyebvlapkozfgtwcnihjqxv", "ymdryebvlapkozfsticniqjqxg", "umbrkebvlapkozfslwcnihjqxg", "umdryebliapkozbstwcnihjqxg", "umvryebolapkozfstwcnihjqig", "umdryeavbackozfstwcnihjqxg", "umdryfbvlapsozfstwcnihaqxg", "umdqyebvlapkozfjtgcnihjqxg", "umdrjebvlaqkozfstwcyihjqxg", "umdryebklaqkozrstwcnihjqxg", "umdryebvpapkozfstwcpihjqjg", "uydryebhlawkozfstwcnihjqxg", "umdyyebvlapkozfstwcykhjqxg", "umdryebvlapkozfstwcnitjnxh", "umdzyebvlapkozfstwcnehyqxg", "mmcryebvlapkozfstwinihjqxg", "umdryebvlapuozfstwmvihjqxg", "umdryfbvlapkozqstwcnihjmxg", "umdryebslapsozfhtwcnihjqxg", "umdtyemvlapmozfstwcnihjqxg", "umdrxevvlapkozfytwcnihjqxg", "umdahebvlapjozfstwcnihjqxg", "umdryebvlapkozfstacnivjqxb", "umdryebvlzpkozfjtwcnihjyxg", "umdryebvlaqkozfstwcnisjqxu", "umdrydbvlapkozfsuwcnihjlxg", "umdryebvlapkomrstwcnihjqkg", "umdryebvlapcozfstmcnwhjqxg", "umdryebvlahkozfstwcibhjqxg", "gmdrzebvlapkozlstwcnihjqxg", "umdryebvlapkezfsswcnrhjqxg", "umdryebvlapkoqfitwcgihjqxg", "umdrnebvlapkozfsiwcninjqxg", "umdryebvlapkozfsrwckohjqxg", "umdryebtlapkomfstwcnihjexg", "umdryxbvlapjozfstwcnihoqxg", "umdpyebvlapkosustwcnihjqxg", "umdryebvlapkvzfawwcnihjqxg", "umhnyebvlaikozfstwcnihjqxg", "umdryebvlagkozfstvknihjqxg", "uodryebjlapkoxfstwcnihjqxg", "umdryefdlapkozfstwcnyhjqxg", "umprmebvtapkozfstwcnihjqxg", "umdhyebvlapoozfstwcnihjqgg", "uddryebvidpkozfstwcnihjqxg", "umdryebtlapkozfetwfnihjqxg", "umdbyebolapkozfstwcoihjqxg", "umdryebvlapkonfstwcnihjpxo", "umdryebvlapkohfstwcnihjqwk", "umdryebolalkkzfstwcnihjqxg", "updryebvxapkozfstwcnshjqxg", "umdryebvlapkovfktwcnuhjqxg", "umdrqrbvlppkozfstwcnihjqxg", "umdrylgvlapkozfstwrnihjqxg", "umdryebvlapkozfstxcnihbqig", "uvdryeevlappozfstwcnihjqxg", "zmdryebvlapkozfstwcnihqqxt", "umdryebvlapvozfstwenihiqxg", "umdryebvlbpkozfsgwcnihjlxg", "umdryhbvlapkozfstwcnihtqxw", "umdreecvlapkozwstwcnihjqxg", "umwryebvlapkoztsmwcnihjqxg", "ukdryebvfapkozrstwcnihjqxg", "umdrylbdlamkozfstwcnihjqxg", "umdryebvlapoozwsmwcnihjqxg", "umdryebvlapkozfqtwcnnzjqxg", "umdryekvlapktzfstwcnohjqxg", "umdryebvlapkozfstwcnihjwqo", "umdrrebflapkogfstwcnihjqxg", "umdryevvlapkozfztwctihjqxg", "umdrybbvlapkozfstwcnihxaxg", "umdryebvlapkozfsowcnphjqag", "smdryebvlapbozfitwcnihjqxg", "umdryebvtapiozfstwcnihjqxe", "umdryebjlakkozfstwccihjqxg", "umdryebvlapdozfshwckihjqxg", "umnryebvlapiozfstwcnihlqxg", "umdrycbvlapkjzfsnwcnihjqxg", "umdryebvyaprozjstwcnihjqxg", "ucdryebvlapkozfstwomihjqxg", "umdryebvlagklzfstwcnihjqyg", "umdryebvladkozfstwcnihjqjh", "umdrwebvlapkozfstwdnicjqxg", "umdryebvlapkmzfstwcniheqxr", "umdryebvlapkjzfstwcviheqxg", "umdrvebvlapkozfstwcbihjqmg", "umdrfebvlapkoffstwcnihsqxg", "umdryebvtarkazfstwcnihjqxg", "umdryebvlapkozfstwcfihjcng", "umdryebvlapkktostwcnihjqxg", "uedryeevlapkozfstwcniijqxg", "bmdryebylapkozfstwcnihjqog", "umdryebvlmpkoztstwcnihjqeg", "umdryepvlarkohfstwcnihjqxg", "uwdryebvlapklzfstzcnihjqxg", "umdryebklapkozfsswcbihjqxg", "umdtyeavlapkozfstwsnihjqxg", "umdryebvaapkozfhtfcnihjqxg", "umdrpebvlapuozfstwvnihjqxg", "umdryebvlapkozffmwcniijqxg", "uqdpyebvlapkozfstwfnihjqxg", "umdryebvlapuozdstwcnihjhxg", "tmdryhbvlapkozfptwcnihjqxg", "umdryevvmapkozfstwcnihjgxg", "umdryeuvlapmozfstwcnihjwxg", "umdryebqlzpkozfbtwcnihjqxg", "umdryebvsapkozystwcniqjqxg", "imdryebvlapkozfscwinihjqxg", "umdryebvlzpkozustwcnmhjqxg", "umdrypbvlapbozfsnwcnihjqxg", "bmdryebvlapqoznstwcnihjqxg", "umdrfebvlapaozfstwcnihxqxg", "umdiyebvxapkozfstwcnchjqxg", "umdrygbvlapkozfstwcnizjqxz", "amdryedvlapkozfstwcnihfqxg", "umdryebvvapzozfstwcnihjgxg", "undryebvlapkzzfstjcnihjqxg", "umdryvbvlapgozfrtwcnihjqxg", "umdrkebvlapkozfstwcnihihxg", "umdryebvrppkozfsowcnihjqxg", "umdryebvlapktzfsdwclihjqxg", "otdrdebvlapkozfstwcnihjqxg", "mmdryebvlazkozfxtwcnihjqxg", "umdryebvlapkozfsbwtnihjqxa", "imqryebvrapkozfstwcnihjqxg", "umdryebvlrpkozfscwcnihjqlg", "uedryebvlapkoznsvwcnihjqxg", "umdryebvlqpkozfstscnihjqxj", "umerycbvlapkozfstwcnihjqxh", "umdkykbvlapjozfstwcnihjqxg" 
    };
    
    @Test
    public void testNumCharsWithFrequency() {
        
        String[] inputs = {
                "abcdef",
                "bababc",
                "abbcde",
                "abcccd",
                "aabcdd",
                "abcdee",
                "ababab"
        };
        int expected = 12;
        
        assertEquals("frequency checksum is incorrect", expected, (int) Y2018D02.checkSum(inputs, 2, 3));
    }

    @Test
    public void testStarOneInput() {
        long sum = Y2018D02.checkSum(STAR_INPUTS, 2, 3);    // 5704
        System.out.printf("CheckSum: %d\n", sum);
    }

    @Test
    public void testIsPrototype() {
        String[] s1 = {
                "abcde",
                "fghij"
        };
        String[] s2 = {
                "axcye",
                "fguij"
        };
        boolean[] expected = {
                false,
                true
        };
        
        for (int i=0; i<s1.length; ++i) {
            assertEquals("isPrototype is incorrect", expected[i], Y2018D02.isPrototype(s1[i], s2[i]));
        }
    }

    @Test
    public void testGetPrototypicalChars() {
        String[] s1 = {
                "abcde",
                "fghij"
        };
        String[] s2 = {
                "axcye",
                "fguij"
        };
        String[] expected = {
                null,
                "fgij"
        };
        
        for (int i=0; i<s1.length; ++i) {
            assertEquals("getPrototypicalChars is incorrect", expected[i], Y2018D02.getPrototypicalChars(s1[i], s2[i]));
        }
    }

    @Test
    public void testStarTwoInput() {
        String proto = Y2018D02.getPrototypicalChars(STAR_INPUTS);    // "umdryabviapkozistwcnihjqx"
        System.out.printf("Prototypical chars: %s\n", proto);
    }
}
