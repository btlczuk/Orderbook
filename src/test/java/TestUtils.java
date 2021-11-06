import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    public static String readFileFromPath(String path) throws URISyntaxException, IOException {
        URL url = TestUtils.class.getClassLoader().getResource(path);
        Path file = Paths.get(url.toURI());
        return new String(Files.readAllBytes(file));
    }
}
