import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AoC_03 {

    String[] symbols = new String[] { ">", "<", "^", "v" };

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc03_real.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        String previousLine = null;
        String currentLine = null;
        String nextLine = null;

        List<Integer> validNumbers = new ArrayList<>();

        for (int i = 0; i < lineCount; i++) {
            var line = lines.get(i);
            if (i > 0) {
                previousLine = lines.get(i - 1);
            }
            if (i < lineCount - 1) {
                nextLine = lines.get(i + 1);
            }
            currentLine = line;

            Line parsedLine = new Line(currentLine, previousLine, nextLine);
            validNumbers.addAll(parsedLine.validNumbers);
        }

        System.out.println(
                "Valid numbers: " + validNumbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        int sum = validNumbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum: " + sum);
    }

    static class Line {

        List<Integer> validNumbers = new ArrayList<>();
        int lineLength = -1;

        public Line(String input, String previousLine, String nextLine) {
            lineLength = input.length();
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(input);
            // Check all occurrences
            while (matcher.find()) {
                if (isValidPartNumber(matcher, input, previousLine, nextLine)) {
                    validNumbers.add(Integer.parseInt(matcher.group()));
                }
            }
        }

        private boolean isSymbol(char c) {
            return !Character.isDigit(c) && c != '.';
        }

        private boolean isValidPartNumber(Matcher matcher, String currentLine, String previousLine, String nextLine) {
            int sindex = matcher.start();
            int eindex = sindex + matcher.group().length();

            // let's check if the number is next to a non-digit character right before or
            // after it in the same line
            if (sindex > 0) {
                char c = currentLine.charAt(sindex - 1);
                if (isSymbol(c)) {
                    return true;
                }
            }
            if (eindex < currentLine.length()) {
                char c = currentLine.charAt(eindex);
                if (isSymbol(c)) {
                    return true;
                }
            }

            // Now let's check if there is any symbol in the previous or next line
            int begin = Math.max(0, sindex - 1);
            int end = Math.min(eindex + 1, lineLength);
            for (int i = begin; i < end; i++) {
                if (previousLine != null && isSymbol(previousLine.charAt(i)))
                    return true;
                if (nextLine != null && isSymbol(nextLine.charAt(i)))
                    return true;
            }

            return false;
        }

    }
}
