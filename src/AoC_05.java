import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class AoC_05 {

    public static void main(String[] args) {
        new AoC_05();
    }

    public AoC_05() {
        var lines = Utilities.readLines("aoc05.txt");
        System.out.println(lines.size());

        var seeds = Seed.parse(lines.get(0));
        var patter = Pattern.compile("([a-z]+\\-to\\-[a-z]+) map:");
        var maps = new HashMap<Category, List<SourceDestMap>>();

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

        for (final Seed s : seeds.values()) {
            for (Category cat : Category.values()) {
                maps.get(cat).stream()
                        .map(map -> map.findDestination(cat.getSource(s)))
                        .filter(dest -> !dest.equals(BigInteger.valueOf(-1)))
                        .findFirst()
                        .ifPresentOrElse(
                                dest -> cat.performAction(s, dest),
                                () -> cat.performAction(s, cat.getSource(s)));
            }
            System.out.println(s);
        }

        // Find lowest location
        var lowestLocation = seeds.values().stream().map(s -> s.location).reduce(BigInteger::min).get();
        System.out.println("Lowest location: " + lowestLocation);

        // System.out.println(maps);

        // Part 1

        // Part 2
    }

    static class Seed {

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

        // Initiate seeds
        static Map<BigInteger, Seed> parse(String line) {
            return Arrays.asList(line.split(":")[1].trim().split(" ")).stream()
                    .map(String::trim)
                    .map(BigInteger::new)
                    .map(Seed::new)
                    .collect(HashMap::new, (map, seed) -> map.put(seed.number, seed), HashMap::putAll);
        }

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
