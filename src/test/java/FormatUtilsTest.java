import org.junit.jupiter.api.Test;
import utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormatUtilsTest {
    @Test
    public void testTableOutput() {
        List<List<String>> rows = new ArrayList<>();
        List<String> headers = List.of("Price", "Size", "Something else");
        rows.add(headers);
        rows.add(List.of("1.098", "1267.34234", "other"));
        String output = FormatUtils.formatAsTable(rows);
        String expectedOutput = "Price  Size        Something else  \n" +
                "1.098  1267.34234  other           \n";
        assertEquals(expectedOutput, output);
    }
}
