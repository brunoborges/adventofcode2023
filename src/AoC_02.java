import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AoC_02 {

    public static void main(String[] args) {
        var lines = Utilities.readLines("aoc02.txt");
        var lineCount = lines.size();
        System.out.println("Line count: " + lineCount);

        List<Game> games = new ArrayList<>();
        for (var line : lines) {
            var game = new Game(line);
            games.add(game);
        }

        String configuration = "12 red cubes, 13 green cubes, 14 blue";
        Map<String, Integer> configMap = parseConfig(configuration);

        var gamesPart1 = games.stream().filter(g -> checkPossibility(g, configMap)).toList();

        // Part 1
        gamesPart1.stream().map(g -> g.gameNumber).reduce(Integer::sum).ifPresent(System.out::println);

        // Part 2 = 71036
        games.stream().map(g -> g.powerOfSets()).reduce(Integer::sum).ifPresent(System.out::println);
    }

    static boolean checkPossibility(Game game, Map<String, Integer> configMap) {
        for (var colorSet : game.colorSets) {
            for (var entry : colorSet.entrySet()) {
                var configCount = configMap.getOrDefault(entry.getKey(), 0);
                if (entry.getValue() > configCount) {
                    return false;
                }
            }
        }
        return true;
    }

    static Map<String, Integer> parseConfig(String configuration) {
        Map<String, Integer> configMap = new HashMap<>();
        for (var configPart : configuration.split(",")) {
            var configPartParts = configPart.trim().split(" ");
            configMap.put(configPartParts[1], Integer.parseInt(configPartParts[0]));
        }
        return configMap;
    }
}

class Game {

    public Game(String input) {
        var parts = input.split(":");
        this.gameNumber = Integer.parseInt(parts[0].substring(5));

        var colorSets = parts[1].split(";");
        for (var colorSet : colorSets) {
            var colorParts = colorSet.split(",");
            Map<String, Integer> colorMap = new HashMap<>();
            for (var colorPart : colorParts) {
                var color = colorPart.trim();
                if (color.length() == 0) {
                    continue;
                }
                var colorAndCount = color.split(" ");
                var colorName = colorAndCount[1];
                var count = Integer.parseInt(colorAndCount[0]);
                colorMap.put(colorName, count);
            }
            this.colorSets.add(colorMap);
        }
    }

    int powerOfSets() {
        Map<String, Integer> max = new HashMap<>();
        colorSets.stream().flatMap(m -> m.entrySet().stream()).forEach(
                e -> {
                    var color = e.getKey();
                    var count = e.getValue();
                    var maxCount = max.get(color);
                    if (maxCount == null) {
                        max.put(color, count);
                    } else {
                        if (count > maxCount) {
                            max.put(color, count);
                        }
                    }
                });

        return max.values().stream().reduce((e1, e2) -> e1 * e2).get();
    }

    int gameNumber;
    List<Map<String, Integer>> colorSets = new ArrayList<>();
}