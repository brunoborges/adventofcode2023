import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class AoC_05 {

    public static void main(String[] args) {
        new AoC_05();
    }

    private HashMap<Category, List<SourceDestMap>> maps;

    public AoC_05() {
        var lines = Utilities.readLines("aoc05.txt");
        System.out.println(lines.size());

        // Produce maps
        this.maps = new HashMap<Category, List<SourceDestMap>>();
        var patter = Pattern.compile("([a-z]+\\-to\\-[a-z]+) map:");

        for (int i = 2; i < lines.size(); i++) {
            var matcher = patter.matcher(lines.get(i));
            if (matcher.matches()) {
                var category = Category.valueOf(matcher.group(1).replaceAll("-", "_"));
                var sublines = new ArrayList<SourceDestMap>();
                for (int j = i + 1; j < lines.size(); j++) {
                    if (lines.get(j).isBlank()) {
                        break;
                    }
                    sublines.add(SourceDestMap.parseRange(lines.get(j)));
                }
                maps.put(category, sublines);
            }
        }

        var seedsInput = lines.get(0);
        var seedsPart1 = parseSeedsPart1(seedsInput);

        // Part 1: Find lowest location
        // Here we can compute all seeds upfront
        lowestLocation = findLowestLocation(seedsPart1);
        System.out.println("Lowest location (Part 1): " + lowestLocation);

        // Part 2
        // Here we need to compute seeds on the fly
        lowestLocation = parseSeedsAndFindLocationPart2(seedsInput);
        System.out.println("Lowest location (Part 2): " + lowestLocation);
    }

    long lowestLocation = Long.MAX_VALUE;

    // Initiate seeds (Part 1)
    List<Seed> parseSeedsPart1(String line) {
        return Arrays.asList(line.split(":")[1].trim().split(" ")).stream()
                .map(String::trim)
                .map(Long::parseLong)
                .map(Seed::new)
                .toList();
    }

    // Initiate seeds (Part 2)
    long parseSeedsAndFindLocationPart2(String line) {
        var lowestLocation = Long.MAX_VALUE;

        String numbers = line.split(":")[1].trim();
        Pattern pattern = Pattern.compile("([0-9]+ [0-9]+)");
        var matcher = pattern.matcher(numbers);

        List<String> seedRanges = new ArrayList<String>();
        while (matcher.find()) {
            seedRanges.add(matcher.group());
        }

        lowestLocation = seedRanges.parallelStream()
                .map(seedRange -> {
                    var pair = seedRange.split(" ");
                    var start = Long.parseLong(pair[0]);
                    var range = Long.parseLong(pair[1]);

                    long thisLowestLocation = Long.MAX_VALUE;
                    for (long i = start; i < start + range; i++) {
                        var seed = new Seed(i);
                        findLocation(seed);

                        if (seed.location < thisLowestLocation) {
                            thisLowestLocation = seed.location;
                        }
                    }
                    return thisLowestLocation;
                }).reduce(Math::min).get();

        return lowestLocation;
    }

    void findLocation(Seed seed) {
        for (Category cat : Category.values()) {
            maps.get(cat).stream()
                    .map(map -> map.findDestination(cat.getSource(seed)))
                    .filter(dest -> dest != -1)
                    .findFirst()
                    .ifPresentOrElse(
                            dest -> cat.performAction(seed, dest),
                            () -> cat.performAction(seed, cat.getSource(seed)));
        }
    }

    Long findLowestLocation(List<Seed> seeds) {
        for (final Seed s : seeds) {
            findLocation(s);
            System.out.println(s);
        }

        return seeds.stream().map(s -> s.location).reduce(Math::min).get();
    }

    class Seed {

        Seed(long number) {
            this.number = number;
        }

        long number;
        long soil;
        long fertilizer;
        long water;
        long light;
        long temperature;
        long humidity;
        long location;

        @Override
        public String toString() {
            return "Seed " + number + ", soil " + soil + ", fertilizer " + fertilizer + ", water " + water + ", light "
                    + light + ", temperature " + temperature + ", humidity " + humidity + ", location " + location
                    + ".";
        }

    }

    record SourceDestMap(long sourceRangeStart, long destinationRangeStart, long rangeLength) {
        static SourceDestMap parseRange(String line) {
            var parts = line.split(" ");
            long destinationRangeStart = Long.parseLong(parts[0]);
            long sourceRangeStart = Long.parseLong(parts[1]);
            long rangeLength = Long.parseLong(parts[2]);

            return new SourceDestMap(sourceRangeStart, destinationRangeStart, rangeLength);
        }

        long findDestination(long source) {
            if (source >= sourceRangeStart && source < sourceRangeStart + rangeLength) {
                return destinationRangeStart + source - sourceRangeStart;
            } else {
                return -1; // not in this particular range
            }
        }
    }

    enum Category {
        seed_to_soil((s) -> s.number, (s, soil) -> s.soil = soil),
        soil_to_fertilizer((s) -> s.soil, (s, fertilizer) -> s.fertilizer = fertilizer),
        fertilizer_to_water((s) -> s.fertilizer, (s, water) -> s.water = water),
        water_to_light((s) -> s.water, (s, light) -> s.light = light),
        light_to_temperature((s) -> s.light, (s, temperature) -> s.temperature = temperature),
        temperature_to_humidity((s) -> s.temperature, (s, humidity) -> s.humidity = humidity),
        humidity_to_location((s) -> s.humidity, (s, location) -> s.location = location);

        private BiConsumer<Seed, Long> action;
        private Function<Seed, Long> getSource;

        Category(Function<Seed, Long> getSource, BiConsumer<Seed, Long> action) {
            this.action = action;
            this.getSource = getSource;
        }

        public Long getSource(Seed seed) {
            return getSource.apply(seed);
        }

        public void performAction(Seed seed, Long value) {
            if (action != null) {
                action.accept(seed, value);
            }
        }
    }

}
