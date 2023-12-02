import java.util.HashMap;
import java.util.Map;

public class AoC_01_v2 {

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc01.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        // Part 1
        System.out.println("Part 1 Sum: " + lines.stream().mapToInt(AoC_01_v2::fromCodeToNumber).sum());

        // Part 2
        System.out.println("Part 2 Sum: " + lines.stream().mapToInt(AoC_01_v2::fromCodeToNumberPart2).sum());
    }

    public static int fromCodeToNumber(String code) {
        int lindex = code.length();
        int rindex = -1;
        for (int i = 1; i < 10; i++) {
            var n = Integer.toString(i);
            int first_index = code.indexOf(n);
            int last_index = code.lastIndexOf(n);
            if (first_index > -1)
                lindex = Math.min(first_index, lindex);
            if (last_index > -1)
                rindex = Math.max(last_index, rindex);
        }

        String firstAndLastOnly = "" + code.charAt(lindex) + code.charAt(rindex);

        return Integer.parseInt(firstAndLastOnly);
    }

    static Map<String, String> map = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }

        map.put("one", "1");
        map.put("two", "2");
        map.put("three", "3");
        map.put("four", "4");
        map.put("five", "5");
        map.put("six", "6");
        map.put("seven", "7");
        map.put("eight", "8");
        map.put("nine", "9");
    }
    // To be honest, I got stuck with Part 2 here because requirements were not
    // clear (as it often happens in AoC!)
    // and ended up checking on Reddit for tips.
    // But it is hard to see tips without seeing tricks used by others, and this was
    // the best trick IMO.

    public static int fromCodeToNumberPart2(String code) {
        int lindex = code.length();
        int rindex = -1;
        int llength = 0;
        int rlength = 0;
        for (String value : map.keySet()) {
            int first_index = code.indexOf(value);
            int last_index = code.lastIndexOf(value);
            if (first_index > -1 && first_index < lindex) {
                llength = value.length();
                lindex = first_index;
            }
            if (last_index > -1 && last_index > rindex) {
                rlength = value.length();
                rindex = last_index;
            }
        }

        String firstAndLastOnly = map.get(code.substring(lindex, lindex + llength))
                + map.get(code.substring(rindex, rindex + rlength));

        return Integer.parseInt(firstAndLastOnly);
    }

}
