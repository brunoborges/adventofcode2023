import java.math.BigInteger;
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

    BigInteger lowestLocation = null;

    // Initiate seeds (Part 1)
    List<Seed> parseSeedsPart1(String line) {
        return Arrays.asList(line.split(":")[1].trim().split(" ")).stream()
                .map(String::trim)
                .map(BigInteger::new)
                .map(Seed::new)
                .toList();
    }

    // Initiate seeds (Part 2)
    BigInteger parseSeedsAndFindLocationPart2(String line) {
        var lowestLocation = BigInteger.valueOf(Long.MAX_VALUE);

        String numbers = line.split(":")[1].trim();
        Pattern pattern = Pattern.compile("([0-9]+ [0-9]+)");
        var matcher = pattern.matcher(numbers);
        
        while (matcher.find()) {
            var pair = matcher.group().split(" ");
            var start = new BigInteger(pair[0]);
            var range = new BigInteger(pair[1]);

            for (BigInteger i = start; i.compareTo(start.add(range)) < 0; i = i.add(BigInteger.ONE)) {
                var seed = new Seed(i);
                findLocation(seed);

                if(seed.location.compareTo(lowestLocation) < 0) {
                    lowestLocation = seed.location;
                }
            }
        }

        return lowestLocation;
    }

    void findLocation(Seed seed) {
        for (Category cat : Category.values()) {
            maps.get(cat).stream()
                    .map(map -> map.findDestination(cat.getSource(seed)))
                    .filter(dest -> !dest.equals(BigInteger.valueOf(-1)))
                    .findFirst()
                    .ifPresentOrElse(
                            dest -> cat.performAction(seed, dest),
                            () -> cat.performAction(seed, cat.getSource(seed)));
        }
    }

    BigInteger findLowestLocation(List<Seed> seeds) {
        for (final Seed s : seeds) {
            findLocation(s);
            System.out.println(s);
        }

        return seeds.stream().map(s -> s.location).reduce(BigInteger::min).get();
    }

    class Seed {

        Seed(BigInteger number) {
            this.number = number;
        }

        BigInteger number;
        BigInteger soil;
        BigInteger fertilizer;
        BigInteger water;
        BigInteger light;
        BigInteger temperature;
        BigInteger humidity;
        BigInteger location;

        @Override
        public String toString() {
            return "Seed " + number + ", soil " + soil + ", fertilizer " + fertilizer + ", water " + water + ", light "
                    + light + ", temperature " + temperature + ", humidity " + humidity + ", location " + location
                    + ".";
        }

    }

    record SourceDestMap(BigInteger sourceRangeStart, BigInteger destinationRangeStart, BigInteger rangeLength) {
        static SourceDestMap parseRange(String line) {
            var parts = line.split(" ");
            BigInteger destinationRangeStart = new BigInteger(parts[0]);
            BigInteger sourceRangeStart = new BigInteger(parts[1]);
            BigInteger rangeLength = new BigInteger(parts[2]);

            return new SourceDestMap(sourceRangeStart, destinationRangeStart, rangeLength);
        }

        BigInteger findDestination(BigInteger source) {
            if (source.compareTo(sourceRangeStart) >= 0 && source.compareTo(sourceRangeStart.add(rangeLength)) < 0) {
                return destinationRangeStart.add(source.subtract(sourceRangeStart));
            } else {
                return BigInteger.valueOf(-1); // not in this particular range
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

        private BiConsumer<Seed, BigInteger> action;
        private Function<Seed, BigInteger> getSource;

        Category(Function<Seed, BigInteger> getSource, BiConsumer<Seed, BigInteger> action) {
            this.action = action;
            this.getSource = getSource;
        }

        public BigInteger getSource(Seed seed) {
            return getSource.apply(seed);
        }

        public void performAction(Seed seed, BigInteger value) {
            if (action != null) {
                action.accept(seed, value);
            }
        }
    }

}
