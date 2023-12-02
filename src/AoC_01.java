import java.util.Map;
import java.util.TreeMap;

public class AoC_01 {

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
        int number = 0;

        var clean = code.replaceAll("[^0-9]", "");
        if (clean.length() == 0) {
            return 0;
        }
        var firstAndLastOnly = clean.substring(0, 1) + clean.substring(clean.length() - 1);
        number = Integer.parseInt(firstAndLastOnly);
        return number;
    }

    static Map<String, String> map = new TreeMap<>();

    static {
        map.put("one", "o1e");
        map.put("two", "t2o");
        map.put("three", "thr3e");
        map.put("four", "f4ur");
        map.put("five", "f5ve");
        map.put("six", "s6x");
        map.put("seven", "se7en");
        map.put("eight", "e8ght");
        map.put("nine", "n9ne");
    }
    // To be honest, I got stuck with Part 2 here because requirements were not
    // clear (as it often happens in AoC!)
    // and ended up checking on Reddit for tips.
    // But it is hard to see tips without seeing tricks used by others, and this was
    // the best trick IMO.

    public static int fromCodeToNumberPart2(String inputCode) {
        // Run twice to ensure that all words are replaced
        for (int i = 0; i < 2; i++) {
            for (String key : map.keySet()) {
                var value = map.get(key);
                inputCode = inputCode.replaceAll(key, value);
            }
        }


        return fromCodeToNumber(inputCode);
    }

}
