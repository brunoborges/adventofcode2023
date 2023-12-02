
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Utilities {

    public static List<String> readLines(String filename) {
        List<String> lines = null;

        try {
            URI fileUri = Utilities.class.getResource(filename).toURI();
            lines = Files.readAllLines(Paths.get(fileUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }

}
