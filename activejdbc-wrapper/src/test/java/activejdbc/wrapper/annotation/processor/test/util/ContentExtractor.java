package activejdbc.wrapper.annotation.processor.test.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class ContentExtractor {
    private ContentExtractor() {
    }

    public static String fromFile(String filePath) throws IOException, URISyntaxException {
        return Files.lines(Paths.get(ClassLoader.getSystemResource(filePath).toURI()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
