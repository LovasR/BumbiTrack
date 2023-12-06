package hu.tibipi.bumbitrack.core;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * The NetUtils class contains utility methods for handling network-related operations.
 */
public class NetUtils {

    /**
     * Private constructor to prevent instantiation of the utility class.
     * Throws an IllegalStateException if an attempt is made to instantiate the class.
     */
    private NetUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Downloads JSON data from the specified URL using an HTTPS connection.
     *
     * @param urlString The URL string from which JSON data is to be downloaded.
     * @return A string containing the downloaded JSON data.
     * @throws IOException If an I/O exception occurs during the download process.
     */
    public static String downloadJsonFromURL(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
            BufferedReader reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            connection.disconnect();
        }

        return result.toString();
    }
}
