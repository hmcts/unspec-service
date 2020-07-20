package uk.gov.hmcts.reform.unspec.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class ResourceReader {

    private ResourceReader() {
        // NO-OP
    }

    public static String readString(String resourcePath) {
        return new String(ResourceReader.readBytes(resourcePath));
    }

    public static String read(String input) {
        try {
            URL resource = ResourceReader.class.getResource(input);
            URI url = resource.toURI();
            return Files.readString(Paths.get(url));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("no file found with the link '" + input + "'", e);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("failed to read from file '" + input + "'", e);
        }
    }

    public static byte[] readBytes(String resourcePath) {
        try (InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource does not exist");
            }
            return IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
