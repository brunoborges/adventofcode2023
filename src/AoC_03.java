import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AoC_03 {

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc03.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        String previousLine = null;
        String currentLine = null;
        String nextLine = null;

        List<Integer> validNumbers = new ArrayList<>();
        List<Integer> gearRatios = new ArrayList<>();

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
            gearRatios.add(parsedLine.sumOfGearRatio);
        }

        System.out.println(
                "Valid numbers: " + validNumbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        int sum = validNumbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum: " + sum);

        // Part 2: print sum of all gear ratios
        int sumOfGearRatios = gearRatios.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum of all gear ratios: " + sumOfGearRatios);

    }

    static class Line {

        List<Integer> validNumbers = new ArrayList<>();
        int lineLength = -1;
        int sumOfGearRatio = 0;

        Pattern patternPart1 = Pattern.compile("\\d+");
        Pattern patternPart2 = Pattern.compile("\\*");

        public Line(String input, String previousLine, String nextLine) {
            lineLength = input.length();

            // Part 1
            Matcher matcher = patternPart1.matcher(input);

            // Check all occurrences
            while (matcher.find()) {
                if (isValidPartNumber(matcher, input, previousLine, nextLine))
                    validNumbers.add(Integer.parseInt(matcher.group()));
            }

            Matcher matcherPart2 = patternPart2.matcher(input);
            while (matcherPart2.find()) {
                sumOfGearRatio += gearRatio(matcherPart2, input, previousLine, nextLine);
            }

            if (sumOfGearRatio > 0) {
                System.out.println("Sum of gears' ratios in this line: " + sumOfGearRatio);
            }
        }

        private boolean isSymbol(char c) {
            return !Character.isDigit(c) && c != '.';
        }

        // Matcher here represents the '*' character
        private int gearRatio(Matcher matcher, String currentLine, String previousLine, String nextLine) {
            int sindex = matcher.start();

            List<Integer> gearNumbers = new ArrayList<>();

            Stream.of(previousLine, currentLine, nextLine).filter(Objects::nonNull).forEach(line -> {
                // Use regex to find numbers back agai in the previous line
                Matcher matcherPart1 = patternPart1.matcher(line);

                while (matcherPart1.find()) {
                    int msindex = matcherPart1.start();
                    int meindex = matcherPart1.end();

                    // 1st: number starts/ends, before/at/after * index (previous/next lines)
                    if (sindex >= msindex && sindex <= meindex) {
                        gearNumbers.add(Integer.parseInt(matcherPart1.group()));
                    }

                    // 2nd: number is right before or after the symbol index (same line)
                    else if (sindex == meindex || sindex + 1 == msindex) {
                        gearNumbers.add(Integer.parseInt(matcherPart1.group()));
                    }
                }
            });

            System.out.println(
                    "Gear numbers: " + gearNumbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            var gearRatio = gearNumbers.size() != 2 ? 0 : gearNumbers.stream().reduce(1, Math::multiplyExact);
            return gearRatio;
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
