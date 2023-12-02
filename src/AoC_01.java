import java.util.Map;
import java.util.TreeMap;

public class AoC_01 {

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc01.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        var sum = 0;
        for (var line : lines) {
            var number = fromCodeToNumber(line);
            sum += number;
        }

        System.out.println("Part 1 Sum: " + sum);

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

    public static int fromCodeToNumberPart2(String inputCode) {
        int number = 0;
        String code = inputCode;
        int pos = 0;
        int verifiedLength = 3;

        StringBuilder temp = new StringBuilder();
        while (pos < code.length()) {
            if (Character.isDigit(code.charAt(pos))) {
                temp.append(code.charAt(pos));
                pos++;
                continue;
            }

            // else NOT digit, check if it is a word
            while (verifiedLength <= 5) {
                if (pos + verifiedLength > code.length()) {
                    break;
                }
                
                var sub = code.substring(pos, pos + verifiedLength);
                if (map.containsKey(sub)) {
                    temp.append(map.get(sub));
                    pos = pos + verifiedLength;
                    verifiedLength = 3;
                    break;
                } else {
                    verifiedLength++;
                }
            }

            if (verifiedLength == 6) {
                // not found
                pos++;
                verifiedLength = 3;
            }
        }

        var clean = temp.toString().replaceAll("[^0-9]", "");
        var firstAndLastOnly = clean.substring(0, 1) + clean.substring(clean.length() - 1);
        number = Integer.parseInt(firstAndLastOnly);
        return number;
    }

}
