import java.util.Map;
import java.util.TreeMap;

public class AoC_01 {

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc01.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        // Part 1
        var sum = 0;
        for (var line : lines) {
            var number = fromCodeToNumber(line);
            sum += number;
        }
        System.out.println("Part 1 Sum: " + sum);

        // Part 2
        var sum2 = 0;
        for (var line : lines) {
            var number = fromCodeToNumberPart2(line);
            System.out.println(line + " (word) <---> (number) " + number);
            sum2 += number;
        }
        System.out.println("Part 2 Sum: " + sum2);

        // get content of the file
        // then filter out the characeters
        // then find the first and last digit
        // combine themn as a number
        // sum all of them up
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

    public static int fromCodeToNumberPart2(String inputCode) {
        for (int i = 0; i < 2; i++) {
            for (String key : map.keySet()) {
                var value = map.get(key);
                inputCode = inputCode.replaceAll(key, value);
            }
        }

        System.out.println(inputCode);

        return fromCodeToNumber(inputCode);
    }

}
