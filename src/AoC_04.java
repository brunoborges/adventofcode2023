import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AoC_04 {
    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc04.txt");
        var cards = lines.stream().map(Card::new).toList();

        // Part 1
        cards.stream().map(c -> c.cardPoints).reduce(Integer::sum).ifPresent(System.out::println);

        // Part 2
        var totalPile = new ArrayList<Card>();
        cards.forEach(c -> processCard(c, cards, totalPile));
        System.out.println(totalPile.size());
    }

    static void processCard(Card card, List<Card> originalCards, List<Card> pileOfCards) {
        pileOfCards.add(card);

        for (int i = card.cardNumber; i < card.countWinningNumbers() + card.cardNumber; i++) {
            var nextCard = originalCards.get(i);
            processCard(nextCard, originalCards, pileOfCards);
        }
    }

    static class Card {

        int cardNumber;
        List<Integer> winningNumbers;
        List<Integer> myNumbers;
        List<Integer> myWinningNumbers;
        int cardPoints = 0;
        List<Card> copies = new ArrayList<>();

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

        public int countWinningNumbers() {
            return myWinningNumbers.size();
        }

    }
}
