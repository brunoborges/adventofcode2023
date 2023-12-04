import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AoC_04 {
    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc04.txt");
        var cards = new ArrayList<Card>();
        lines.stream().filter(s -> s.startsWith("Card")).forEach(s -> cards.add(new Card(s)));
        cards.stream().map(c -> c.cardPoints).reduce(Integer::sum).ifPresent(System.out::println);
    }

    static class Card {
        int cardNumber;
        List<Integer> winningNumbers;
        List<Integer> myNumbers;
        List<Integer> myWinningNumbers;
        int cardPoints = 0;

        public Card(String input) {
            var parts = input.split(":");
            this.cardNumber = Integer.parseInt(parts[0].trim().split("\\s+")[1]);
            var numbersGroups = parts[1].trim().split("\\|");

            var _winningNumbers = numbersGroups[0].trim().split("\\s+");
            winningNumbers = Arrays.asList(_winningNumbers).stream().map(Integer::parseInt).toList();

            var _myNumbers = numbersGroups[1].trim().split("\\s+");
            myNumbers = Arrays.asList(_myNumbers).stream().map(Integer::parseInt).toList();

            myWinningNumbers = myNumbers.stream().filter(winningNumbers::contains).toList();

            if (myWinningNumbers.size() > 0) {
                cardPoints = 1;
                int remainingNumbers = myWinningNumbers.size() - 1;
                while (remainingNumbers-- > 0) {
                    cardPoints *= 2;
                }
            }
        }
    }
}
