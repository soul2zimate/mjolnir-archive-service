package org.jboss.set.mjolnir.archive;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public final class TestUtils {

    private TestUtils() {
    }

    public static String readSampleResponse(String responseFileName) throws URISyntaxException, IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(responseFileName);

        if (resource == null) {
            throw new RuntimeException();
        }
        return FileUtils.readFileToString(
                Paths.get(resource.toURI()).toFile(),
                StandardCharsets.UTF_8.name());
    }
}
